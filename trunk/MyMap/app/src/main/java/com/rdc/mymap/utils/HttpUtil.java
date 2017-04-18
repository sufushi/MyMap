package com.rdc.mymap.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.baidu.mapapi.http.HttpClient;
import com.rdc.mymap.config.SharePreferencesConfig;
import com.rdc.mymap.config.URLConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by wsoyz on 2017/4/11.
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";

    SharedPreferences mPreferences;
    SharedPreferences.Editor mEditor;
    public HttpUtil() {

    }

    public static Bitmap getPhpop(int i) {
        Bitmap bm = null;
        try {
            HttpURLConnection connection = null;
            URL url;
            InputStream is = null;
            String urlS = URLConfig.PIURL + URLConfig.ACTION_PHOTO + i + ".jpg";
            Log.d(TAG,"getting photo from "+urlS);
            url = new URL(urlS);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                is = null;
                is = connection.getInputStream();
                bm = BitmapFactory.decodeStream(is);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }
    public static String submitPostDataWithCookie(Map<String, String> params, String cookie, String Action){
        Log.d(TAG,"sending cookie:"+cookie);
        byte[] data = getRequestData(params, "utf-8").toString().getBytes();                             //获得请求体
        try {
            URL url = new URL(URLConfig.PIURL + Action);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);                                                  //设置连接超时时间
            httpURLConnection.setDoInput(true);                                                         //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                                                        //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");                                                 //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);                                                      //使用Post方式不能使用缓存
            httpURLConnection.setRequestProperty("Cookie", cookie);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));        //设置请求体的长度
            OutputStream outputStream = httpURLConnection.getOutputStream();                            //获得输出流，向服务器写入数据
            outputStream.write(data);
            int response = httpURLConnection.getResponseCode();                                         //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                                                  //处理服务器的响应结果
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }
    public static String submitPostData(Map<String, String> params, String encode, String Action) {
        if(null == encode) encode = "utf-8";
        byte[] data = getRequestData(params, "utf-8").toString().getBytes();                             //获得请求体
        try {
            URL url = new URL(URLConfig.PIURL + Action);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setConnectTimeout(3000);                                                  //设置连接超时时间
            httpURLConnection.setDoInput(true);                                                         //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);                                                        //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");                                                 //设置以Post方式提交数据
            httpURLConnection.setUseCaches(false);                                                      //使用Post方式不能使用缓存
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));        //设置请求体的长度
            OutputStream outputStream = httpURLConnection.getOutputStream();                            //获得输出流，向服务器写入数据
            outputStream.write(data);
            int response = httpURLConnection.getResponseCode();                                         //获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                String cookie = httpURLConnection.getHeaderField("set-cookie");
                if(encode.equals(SharePreferencesConfig.COOKIE_STRING)) return cookie;
                Log.d(TAG,"cookie:"+cookie);
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream);                                                  //处理服务器的响应结果
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "-" + stringBuffer.toString());
        return stringBuffer;
    }

    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        Log.d(TAG, " OK!  --  " + resultData);
        return resultData;
    }


}
