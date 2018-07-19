package com.example.hookgethtmlinfo;
import android.os.Environment;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage{
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.example.androidcalljs"))
            return;

        XposedHelpers.findAndHookMethod("com.example.androidcalljs.TestActivity", lpparam.classLoader, "initWebView", new XC_MethodHook() {


            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedHelpers.findAndHookMethod("android.webkit.WebView", lpparam.classLoader, "setWebViewClient", WebViewClient.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.args[0] = new MyWebViewClient();
                    }
                });
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Class clazz = param.thisObject.getClass();
                Field field = clazz.getDeclaredField("webView");
                field.setAccessible(true);
                WebView webView = (WebView) field.get(param.thisObject);
                webView.addJavascriptInterface(new MyJavascriptInterface(), "android");
            }
        });
    }

    private class MyWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            XposedBridge.log(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Execute your javascript below
            String jsLine = initJS();
            XposedBridge.log("jsLine->>"+jsLine);
            view.loadUrl("javascript:"+jsLine);
        }
    }

    private String  initJS() {
        String jsString = null;
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String jsDirPath = sdcardPath + File.separator + "JsFile";
        XposedBridge.log("jsDirPath->>"+jsDirPath);
        File file = new File(jsDirPath);
        if(!file.exists()){
            file.mkdirs();
        }
        String jsFilePath = jsDirPath+File.separator + "collect.js";
        XposedBridge.log("jsFilePath->>"+jsFilePath);
        File jsFile = new File(jsFilePath);
        if(jsFile.exists()){
            try {
                FileInputStream fileInputStream = new FileInputStream(jsFile);
                Long fileLength = jsFile.length();
                byte[] buff = new byte[fileLength.intValue()];
                fileInputStream.read(buff);
                fileInputStream.close();
                jsString = new String(buff, "UTF-8");
                return jsString;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsString;
    }

    class MyJavascriptInterface {
        @JavascriptInterface
        public void startFunction(String bookJson){
            XposedBridge.log("bookJson-->"+bookJson);
            Gson gson = new Gson();
            BookInfo[] bookinfoList = gson.fromJson(bookJson, BookInfo[].class);
            for(BookInfo bookInfo:bookinfoList){
                XposedBridge.log("bookInfo-->"+bookInfo.toString());
            }
        }
    }

}
