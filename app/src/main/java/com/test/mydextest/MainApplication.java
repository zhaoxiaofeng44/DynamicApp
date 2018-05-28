package com.test.mydextest;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import android.util.TypedValue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by zhaoxiaofeng on 2018/4/16.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        SmAboveUtils.injectAbove(this, SmAboveUtils.copy(this,"test.apk"));
    }
}