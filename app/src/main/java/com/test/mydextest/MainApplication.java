package com.test.mydextest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by zhaoxiaofeng on 2018/4/16.
 */

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("mytest","mainapplication");

        final  String  dexPath = copyFileFromAssets(this,"test.apk");
        final String optimizedDexOutputPath = getOptimizedDexPath(this);
        SimpleDexUtils.injectAbove(this,dexPath,optimizedDexOutputPath,null);
    }

    public static String getOptimizedDexPath(Context context) {
        return context.getDir("outdex", Context.MODE_PRIVATE).getAbsolutePath();
    }

    private static final int BUF_SIZE = 8 * 1024;
    public static String copyFileFromAssets(Context context, String dexName) {
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
//
//    public static void loadAndCall(Context context, String dexName) {
//        final File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE), dexName);
//        final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);
//
//        DexClassLoader cl = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
//                optimizedDexOutputPath.getAbsolutePath(),
//                null,
//                context.getClassLoader());
//        call(cl);
//    }

//    public static void call(ClassLoader cl) {
//        Class myClasz = null;
//        try {
//
//            Log.d("mytest", "result start-" );
//            myClasz =
//                    cl.loadClass("com.test.mytest.MyTest");
//            Log.d("mytest", "result myClasz" + myClasz);
//            Object instance = myClasz.getConstructor().newInstance();
//            Log.d("mytest", "result myClasz instance" + instance);
//
//            Method method = myClasz.getDeclaredMethod("sub",new Class[]{int.class,int.class});
//            Log.d("mytest", "result myClasz method   " + method);
//            Object result = method.invoke(instance,6,1);
//            Log.d("mytest", "result-"  + result);
//
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InstantiationException e) {
//            e.printStackTrace();
//        }
//    }

//    private static void test(String fileDir) {
//        List<File> fileList = new ArrayList<File>();
//        File file = new File(fileDir);
//        File[] files = file.listFiles();// 获取目录下的所有文件或文件夹
//        if (files == null) {// 如果目录为空，直接退出
//            return;
//        }
//        // 遍历，目录下的所有文件
//        for (File f : files) {
//            if (f.isFile()) {
//                fileList.add(f);
//                Log.v("test >> ",f.getAbsolutePath());
//            } else if (f.isDirectory()) {
//                //Log.v("test >> ",f.getAbsolutePath());
//                test(f.getAbsolutePath());
//            }
//        }
//        for (File f1 : fileList) {
//            //System.out.println(f1.getName());
//        }
//    }
//
//
//    public static Boolean injectAboveEqualApiLevel14(Context context,String dexPath, String defaultDexOptPath, String nativeLibPath) {
//        Log.i("mytest", "--> injectAboveEqualApiLevel14");
//
//        test(defaultDexOptPath);
//        ClassLoader pathClassLoader =  context.getClassLoader();
//        DexClassLoader dexClassLoader = new DexClassLoader(dexPath, defaultDexOptPath, nativeLibPath, pathClassLoader.getParent());
//        try {
//
//            Object arr = getDexElements(getPathList(pathClassLoader));
//
//            int len = Array.getLength(arr);
//            for (int k = 0; k < len; ++k) {
//                Log.v("mytest","class cc  " + Array.get(arr, k).toString());
//            }
//
//            Object dexElements = combineArray(
//                    getDexElements(getPathList(dexClassLoader)),
//                    getDexElements(getPathList(pathClassLoader))
//            );
//            Object pathList = getPathList(pathClassLoader);
//            setField(pathList, pathList.getClass(), "dexElements", dexElements);
//        } catch (Throwable e) {
//            e.printStackTrace();
//            return false;
//        }
//        Log.i("mytest", "<-- injectAboveEqualApiLevel14 End.");
//        return true;
//    }
//
//    private static Object getPathList(Object baseDexClassLoader)
//            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
//        return getField(baseDexClassLoader, Class.forName("dalvik.system.BaseDexClassLoader"), "pathList");
//    }
//
//
//    private static Object getDexElements(Object paramObject)
//            throws IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
//        return getField(paramObject, paramObject.getClass(), "dexElements");
//    }
//
//
//    private static Object getField(Object obj, Class<?> cl, String field)
//            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
//        Field localField = cl.getDeclaredField(field);
//        localField.setAccessible(true);
//        return localField.get(obj);
//    }
//
//
//    private static void setField(Object obj, Class<?> cl, String field, Object value)
//            throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
//        Field localField = cl.getDeclaredField(field);
//        localField.setAccessible(true);
//        localField.set(obj, value);
//    }
//
//    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
//        Class<?> localClass = arrayLhs.getClass().getComponentType();
//        int i = Array.getLength(arrayLhs);
//        int j = i + Array.getLength(arrayRhs);
//        Object result = Array.newInstance(localClass, j);
//        for (int k = 0; k < j; ++k) {
//            if (k < i) {
//                Array.set(result, k, Array.get(arrayLhs, k));
//            } else {
//                Array.set(result, k, Array.get(arrayRhs, k - i));
//            }
//        }
//        return result;
//    }
}