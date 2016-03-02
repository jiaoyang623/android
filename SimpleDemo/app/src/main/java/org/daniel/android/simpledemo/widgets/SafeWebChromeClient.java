package org.daniel.android.simpledemo.widgets;

import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class SafeWebChromeClient extends WebChromeClient {

    //private String TAG = "YunpanWebChromeClientEx";

    // private YunpanWebview yunpanWebview = null;

//	public YunpanWebChromeClientEx(YunpanWebview view) {
//		this.yunpanWebview = view;
//	}

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        //Log.i("bug", "TAG=" + TAG + " onProgressChanged() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                yunpan.injectJavascriptInterfaces(view);
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " onProgressChanged() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onProgressChanged(view, newProgress);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        //Log.i("bug", "TAG=" + TAG + " onJsPrompt() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                if (yunpan.handleJsInterface(view, url, message, defaultValue, result)) {
                    return true;
                }
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " onJsPrompt() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        //Log.i("bug", "TAG=" + TAG + " onReceivedTitle() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                yunpan.injectJavascriptInterfaces(view);
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " onReceivedTitle() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
