//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package org.daniel.android.workbanch.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

public class NetworkUtils {
    private static final String TAG = NetworkUtils.class.getName();
    private static final String proxyAddr = "10.0.0.172";
    private static final int proxyPort = 80;

    public static boolean needProxy(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.checkPermission("android.permission.ACCESS_NETWORK_STATE", context.getPackageName()) != 0) {
            return false;
        } else {
            try {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.getType() != 1) {
                    String subNetwork = networkInfo.getExtraInfo();
                    if (subNetwork != null && (subNetwork.equals("cmwap") || subNetwork.equals("3gwap") || subNetwork.equals("uniwap"))) {
                        return true;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    public static byte[] postData(Context context, byte[] data, String url) {
        HttpPost httpPost = new HttpPost(url);
        BasicHttpParams basicHttpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(basicHttpParams, 10000);
        HttpConnectionParams.setSoTimeout(basicHttpParams, 30000);
        DefaultHttpClient httpClient = new DefaultHttpClient(basicHttpParams);

        try {
            if (needProxy(context)) {
                HttpHost httpHost = new HttpHost(proxyAddr, proxyPort);
                httpClient.getParams().setParameter("http.route.default-proxy", httpHost);
            }

            InputStreamEntity streamEntity = new InputStreamEntity(new ByteArrayInputStream(data), data.length);
            httpPost.setEntity(streamEntity);

            HttpResponse response = httpClient.execute(httpPost);

            int statusCode = response.getStatusLine().getStatusCode();
            Log.i(TAG, "status code : " + statusCode);
            if (response.getStatusLine().getStatusCode() == 200) {
                Log.i(TAG, "Sent message to " + url);
                HttpEntity httpEntity = response.getEntity();
                if (httpEntity != null) {
                    InputStream inputStream = httpEntity.getContent();

                    byte[] result;
                    try {
                        result = Helper.readBytes(inputStream);
                    } finally {
                        Helper.closeStream(inputStream);
                    }

                    return result;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (ClientProtocolException e) {
            Log.i(TAG, "ClientProtocolException,Failed to send message.", e);
            return null;
        } catch (IOException e) {
            Log.i(TAG, "IOException,Failed to send message.", e);
            return null;
        }
    }


    public static String request(RequestData request) {
        String method = request.getPostMethod().trim();
        checkRequestMethod(method);
        String result = null;
        if (RequestData.METHOD_GET.equals(method)) {
            result = postRequest(request.getUrl(), request.getParams());
        } else if (RequestData.METHOD_POST.equals(method)) {
            result = sendRequest(request.url, request.getParams(), false);
        }
        return result;
    }

    private static String sendRequest(String url, Map<String, String> data, boolean needCompress) {
        final int requestId = (new Random()).nextInt(1000);

        String paramsString = Helper.mapToJson(data).toString();
        HttpPost post = new HttpPost(url);
        HttpParams params = genParams();
        DefaultHttpClient client = new DefaultHttpClient(params);

        try {
            //是否用压缩
            if (needCompress) {
                String content = "content=" + paramsString;
                byte[] compressedData = DeflaterHelper.deflaterCompress(content, Charset.defaultCharset().toString());
                post.addHeader("Content-Encoding", "deflate");
                InputStreamEntity entity = new InputStreamEntity(new ByteArrayInputStream(compressedData), (long) compressedData.length);
                post.setEntity(entity);
            } else {
                ArrayList content = new ArrayList(1);
                content.add(new BasicNameValuePair("content", paramsString));
                post.setEntity(new UrlEncodedFormEntity(content, "UTF-8"));
            }

            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream content = entity.getContent();
                    Header header = response.getFirstHeader("Content-Encoding");
                    if (header != null && header.getValue().equalsIgnoreCase("deflate")) {
                        content = new InflaterInputStream(content);
                    }

                    String result = Helper.readString(content);
                    Log.i(TAG, requestId + ":\tresponse: " + Helper.LINE_SEPARATOR + result);
                    return result;
                } else {
                    return null;
                }
            } else {
                Log.i(TAG, requestId + ":\tFailed to send message. StatusCode = " + response.getStatusLine().getStatusCode() + Helper.LINE_SEPARATOR + url);
                return null;
            }
        } catch (ClientProtocolException e) {
            Log.i(TAG, requestId + ":\tClientProtocolException,Failed to send message." + url, e);
            return null;
        } catch (IOException e) {
            Log.i(TAG, requestId + ":\tIOException,Failed to send message." + url, e);
            return null;
        }
    }

    private static String postRequest(String url, Map<String, String> params) {
        int requestId = (new Random()).nextInt(1000);

        try {
            String separator = System.getProperty("line.separator");
            if (url.length() <= 1) {
                Log.i(TAG, requestId + ":\tInvalid baseUrl.");
                return null;
            } else {
                Log.i(TAG, requestId + ":\tget: " + url);
                HttpGet httpGet = new HttpGet(url);
                if (params != null && params.size() > 0) {
                    Set keySet = params.keySet();
                    Iterator iterator = keySet.iterator();

                    while (iterator.hasNext()) {
                        String key = (String) iterator.next();
                        httpGet.addHeader(key, params.get(key));
                    }
                }

                HttpParams httpParams = genParams();
                DefaultHttpClient client = new DefaultHttpClient(httpParams);
                HttpResponse response = client.execute(httpGet);
                if (response.getStatusLine().getStatusCode() == 200) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        InputStream content = entity.getContent();
                        Header header = response.getFirstHeader("Content-Encoding");
                        if (header != null && header.getValue().equalsIgnoreCase("gzip")) {
                            Log.i(TAG, requestId + "  Use GZIPInputStream get data....");
                            content = new GZIPInputStream(content);
                        } else if (header != null && header.getValue().equalsIgnoreCase("deflate")) {
                            Log.i(TAG, requestId + "  Use InflaterInputStream get data....");
                            content = new InflaterInputStream(content);
                        }

                        String result = Helper.readString(content);
                        Log.i(TAG, requestId + ":\tresponse: " + separator + result);
                        if (result == null) {
                            return null;
                        }

                        return result;
                    }
                } else {
                    Log.i(TAG, requestId + ":\tFailed to send message. StatusCode = " + response.getStatusLine().getStatusCode() + Helper.LINE_SEPARATOR + url);
                }

                return null;
            }
        } catch (ClientProtocolException e) {
            Log.i(TAG, requestId + ":\tClientProtocolException,Failed to send message." + url, e);
            return null;
        } catch (Exception e) {
            Log.i(TAG, requestId + ":\tIOException,Failed to send message." + url, e);
            return null;
        }
    }

    private static HttpParams genParams() {
        BasicHttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 20000);
        HttpProtocolParams.setUserAgent(params, System.getProperty("http.agent"));
        return params;
    }

    private static void checkRequestMethod(String method) {
        if (Helper.isEmpty(method) || !(RequestData.METHOD_GET.equals(method.trim()) ^ RequestData.METHOD_POST.equals(method.trim()))) {
            throw new RuntimeException("验证请求方式失败[" + method + "]");
        }
    }

    public static abstract class RequestData {
        protected static String METHOD_POST = "POST";
        protected static String METHOD_GET = "GET";
        protected String url;

        public abstract Map<String, String> getParams();

        public abstract String getUrl();

        protected String getPostMethod() {
            return METHOD_POST;
        }

        public RequestData(String url) {
            this.url = url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

}
