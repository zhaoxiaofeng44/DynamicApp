package com.test.mydextest;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by zhaoxiaofeng on 2018/5/7.
 */

public class SmInstrumentation extends Instrumentation {

    private final String mPath;
    private AssetManager mComparisonAssetManager;

    public SmInstrumentation(String apkPath) {

        mPath = apkPath;
        Log.v("Main","SmInstrumentation >>  " + apkPath);
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {

        Log.v("Main","callActivityOnCreate >>  " + mPath);

        AssetManager assetManager = activity.getAssets();
        if(mComparisonAssetManager != assetManager){

            Log.v("Main","start >> addAssetPath " + mPath);
            try {
                Method addAssetPathMethod = SmAboveUtils.getMethodByClasss(assetManager.getClass(),"addAssetPath",new Class[]{String.class});
                if(null != addAssetPathMethod){
                    addAssetPathMethod.setAccessible(true);
                    addAssetPathMethod.invoke(assetManager, mPath);
                    Log.v("Main","addAssetPathMethod.invoke(assetManager, mPath) >>  " + mPath);
                }

                Method ensureStringBlocks = SmAboveUtils.getMethodByClasss(assetManager.getClass(),"ensureStringBlocks");
                if(null != addAssetPathMethod) {
                    ensureStringBlocks.setAccessible(true);
                    ensureStringBlocks.invoke(assetManager);
                    Log.v("Main","ensureStringBlocks.invoke(mAssetManager) >>  ");
                }
                mComparisonAssetManager = assetManager;
                Log.v("Main","finish >> addAssetPath " + mPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.callActivityOnCreate(activity, icicle);
    }
}
