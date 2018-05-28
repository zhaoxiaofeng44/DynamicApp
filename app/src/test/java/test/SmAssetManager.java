package test;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhaoxiaofeng on 2018/5/8.
 */

public class SmAssetManager {

    protected List<AssetManager> _assets;
    protected Map<String,AssetManager> _mapAssets;

    public SmAssetManager(){
        _assets = new ArrayList<>();
        _mapAssets = new HashMap<>();
    }

    public void close() {

        for(AssetManager asset : _assets){
            asset.close();
        }
    }

    public void add(AssetManager asset) throws IOException {

        _assets.add(asset);
        String [] files = asset.list("");
        for(String file : files){
            _mapAssets.put(file,asset);
            Log.e("Main", " myAss.list >>= " + file);
        }
    }

    public InputStream open(String fileName) throws IOException {

        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.open(fileName);
        }
        return null;
    }

    public InputStream open(String fileName, int accessMode) throws IOException {

        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.open(fileName,accessMode);
        }
        return null;
    }

    public AssetFileDescriptor openFd(String fileName) throws IOException {

        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.openFd(fileName);
        }
        return null;
    }

    public String[] list(String dir){

        Set<String> files = _mapAssets.keySet();
        List<String> list = new ArrayList<>();
        for(String file : files){
            if(-1 != file.indexOf(dir)){
                list.add(file);
            }
        }
        String[] array =new String[list.size()];
        list.toArray(array);
        return array;
    }

    public AssetFileDescriptor openNonAssetFd(String fileName) throws IOException {

        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.openNonAssetFd(fileName);
        }
        return null;
    }

    public AssetFileDescriptor openNonAssetFd(int cookie, String fileName) throws IOException {

        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.openNonAssetFd(cookie,fileName);
        }
        return null;
    }

    public XmlResourceParser openXmlResourceParser(String fileName) throws IOException {

        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.openXmlResourceParser(fileName);
        }
        return null;
    }

    public final XmlResourceParser openXmlResourceParser(int cookie, String fileName) throws IOException {
        AssetManager asset = _mapAssets.get(fileName);
        if(null != asset){
            return asset.openXmlResourceParser(cookie,fileName);
        }
        return null;
    }

    public String[] getLocales(){

        if(_assets.size() > 0){
            return _assets.get(0).getLocales();
        }
        return null;
    }

    private String _baseName(String fileName){
        return null;
    }
}
