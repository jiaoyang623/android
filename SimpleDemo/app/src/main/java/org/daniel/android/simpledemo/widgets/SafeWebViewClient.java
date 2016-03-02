package org.daniel.android.simpledemo.widgets;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SafeWebViewClient extends WebViewClient {
    //private String TAG = "YunpanWebViewClientEx";

    // private YunpanWebview yunpanWebview = null;

//	public YunpanWebViewClientEx(YunpanWebview view) {
//		this.yunpanWebview = view;
//	}

    @Override
    public void onLoadResource(WebView view, String url) {
        // Log.i("bug", "TAG=" + TAG + " onLoadResource() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                yunpan.injectJavascriptInterfaces(view);
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " onLoadResource() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onLoadResource(view, url);
    }

    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        // Log.i("bug", "TAG=" + TAG + " doUpdateVisitedHistory() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                yunpan.injectJavascriptInterfaces(view);
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " doUpdateVisitedHistory() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        // Log.i("bug", "TAG=" + TAG + " onPageStarted() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                yunpan.injectJavascriptInterfaces(view);
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " onPageStarted() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        // Log.i("bug", "TAG=" + TAG + " onPageFinished() begin");
        try {
            if (view instanceof SafeWebView) {
                SafeWebView yunpan = (SafeWebView) view;
                yunpan.injectJavascriptInterfaces(view);
            }
//			else {
//				Log.i("bug", "TAG=" + TAG + " onPageFinished() view not YunpanWebview");
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPageFinished(view, url);
    }
}
