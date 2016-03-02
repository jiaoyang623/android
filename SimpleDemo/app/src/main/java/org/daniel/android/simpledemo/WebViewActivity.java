package org.daniel.android.simpledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.widget.Toast;

import org.daniel.android.simpledemo.widgets.SafeWebChromeClient;
import org.daniel.android.simpledemo.widgets.SafeWebView;
import org.daniel.android.simpledemo.widgets.SafeWebViewClient;

/**
 * Created by jiaoyang on 2016/2/23.
 */
public class WebViewActivity extends AppCompatActivity implements View.OnClickListener {
	private SafeWebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview);
		findViewById(R.id.click).setOnClickListener(this);
		mWebView = (SafeWebView) findViewById(R.id.web);
		WebSettings setting = mWebView.getSettings();
		setting.setJavaScriptEnabled(true);//支持js
		mWebView.setWebChromeClient(new SafeWebChromeClient() {
		});
		mWebView.setWebViewClient(new SafeWebViewClient() {
		});
		mWebView.addJavascriptInterface(new JSBridge(), "JSBridge");
	}

	@Override
	public void onClick(View v) {
		mWebView.loadUrl("file:///android_asset/test.html");
	}

	public class JSBridge {

		@JavascriptInterface
		public void show(String message) {
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
		}
	}
}
