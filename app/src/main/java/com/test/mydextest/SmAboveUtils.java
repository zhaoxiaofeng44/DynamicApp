package com.test.mydextest;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dalvik.system.BaseDexClassLoader;
import dalvik.system.DexClassLoader;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;



/**
 * Created by zhaoxiaofeng on 2018/4/26.
 */

public class SmAboveUtils {

    private static void test(String fileDir) {

        File file = new File(fileDir);
        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
        if (files == null) {// 如果目录为空，直接退出
            return;
        }
        // 遍历，目录下的所有文件
        for (File f : files) {
            if (f.isFile()) {
                Log.v("test >> ",f.getAbsolutePath());
            } else if (f.isDirectory()) {
                //Log.v("test >> ",f.getAbsolutePath());
                test(f.getAbsolutePath());
            }
        }
    }

    public static String copy(Context context, String dexName) {

        final int BUF_SIZE = 8 * 1024;
        File storagePath = new File(context.getDir("sys", Context.MODE_PRIVATE),
                dexName);
        try {
            BufferedInputStream bis = new BufferedInputStream(context.getAssets().open(dexName));
            OutputStream dexWriter = new BufferedOutputStream(
                    new FileOutputStream(storagePath));
            byte[] buf = new byte[BUF_SIZE];
            int len;
            while((len = bis.read(buf, 0, BUF_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
            dexWriter.close();
            bis.close();
            return storagePath.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void injectAbove(Context context, String apkPath){

        final String optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE).getAbsolutePath();

        hookActivityResource(context,apkPath);
        hookActivityClasses(context,apkPath,optimizedDexOutputPath,null);
    }

    public static boolean hookActivityResource(Context context,String apkPath){
        try {
            //获取ActiivtiyThread类
            Class<?> mActivityThreadClass = Class.forName("android.app.ActivityThread");

            //获取当前ActivityThread
            Method currentActivityThread = mActivityThreadClass.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object mCurrentActivityThread = currentActivityThread.invoke(null);

            //获取mInstrumentation字段
            Field mInstrumentationField = mActivityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            mInstrumentationField.set(mCurrentActivityThread,new SmInstrumentation(apkPath));

            Log.v("Main","invoke(mInstrumentationField) >>  ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Boolean hookActivityClasses(Context context, String dexPath, String defaultDexOptPath, String nativeLibPath) {

        BaseDexClassLoader pathClassLoader =  (BaseDexClassLoader)context.getClassLoader();
        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, defaultDexOptPath, nativeLibPath, pathClassLoader.getParent());
        try {
            Object combineElements = combineArray(
                    getDexElements(dexClassLoader),
                    getDexElements(pathClassLoader)
            );
            setDexElements(pathClassLoader,combineElements);
        } catch (Throwable e) {
            e.printStackTrace();;
        }
        return true;
    }

    /**
     * 拼接DexClassLoader
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Log.v("mytest","test >> "+i + "   " + j);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }

    /**
     * 获取DexClassLoader里的Elements
     */
    private static Object getDexElements(Object baseDexClassLoader)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Object pathList = getObjectValue(baseDexClassLoader,"pathList");
        if(null != pathList){
            return getObjectValue(pathList,"dexElements");
        }
        return null;
    }

    /**
     * 设置DexClassLoader里的Elements
     */
    private static void setDexElements(Object baseDexClassLoader,Object value)
            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        Object pathList = getObjectValue(baseDexClassLoader,"pathList");
        if(null != pathList){
            setObjectValue(pathList,"dexElements",value);
        }
    }

    /**
     * 根据属性名,获取属性
     */
    public static Object getObjectValue(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field classField = getFieldByClasss(obj,fieldName);
        if(null != classField){

            Log.v("mytest","test >> getObjectValue "+fieldName + "   " + obj);
            classField.setAccessible(true);
            return classField.get(obj);
        }
        return null;
    }

    /**
     * 根据属性名,更改属性
     */
    public static void setObjectValue(Object obj, String fieldName, Object value)
            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field classField = getFieldByClasss(obj,fieldName);
        if(null != classField){
            Log.v("mytest","test >> setObjectValue "+fieldName + "   " + obj +"  " + value);
            classField.setAccessible(true);
            classField.set(obj, value);
        }
    }

    /**
     * 根据属性名获取属性元素，包括各种安全范围和所有父类
     */
    public static Field getFieldByClasss(Object object,String fieldName)
            throws NoSuchFieldException {
        Field field = null;
        Class<?> clazz = object.getClass();
        for (; null == field && clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                // 这里甚么都不能抛出去, 如果这里的异常打印或者往外抛，则就不会进入
            }
        }
        return field;
    }


    /**
     * 根据属性名获取属性方法，包括各种安全范围和所有父类
     */
    public static Method getMethodByClasss(Class<?> clazz,String fieldName, Class... parameterTypes)
            throws NoSuchFieldException {
        Method field = null;
        for (; null == field && clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredMethod(fieldName,parameterTypes);
            } catch (Exception e) {
                // 这里甚么都不能抛出去, 如果这里的异常打印或者往外抛，则就不会进入
            }
        }
        return field;
    }

}
