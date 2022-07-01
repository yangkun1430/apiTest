package com.eden.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.Map;

public class HttpUtil {
    /**
     * 不带请求头的get方法封装
     *
     * @param url
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse get(String url){
        try {
            //创建一个可关闭的HttpClient对象
            CloseableHttpClient httpclient = HttpClients.createDefault();
            //创建一个HttpGet的请求对象
            HttpGet httpget = new HttpGet(url);
            //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
            CloseableHttpResponse httpResponse = httpclient.execute(httpget);
            return httpResponse;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 带请求头信息的get方法
     *
     * @param url
     * @param headermap，键值对形式
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static HttpResponse get(String url, Map<String, String> headermap){
        try {
            //创建一个可关闭的HttpClient对象
            CloseableHttpClient httpclient = HttpClients.createDefault();
            //创建一个HttpGet的请求对象
            HttpGet httpget = new HttpGet(url);
            //加载请求头到httpget对象
            for (Map.Entry<String, String> entry : headermap.entrySet()) {
                httpget.addHeader(entry.getKey(), entry.getValue());
            }
            //执行请求,相当于postman上点击发送按钮，然后赋值给HttpResponse对象接收
            HttpResponse httpResponse = httpclient.execute(httpget);
            return httpResponse;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 封装post方法
     *
     * @param url
     * @param entityString，其实就是设置请求json参数
     * @param headermap，带请求头
     * @return 返回响应对象
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static CloseableHttpResponse post(String url, String entityString, Map<String, String> headermap){
        try {
            //创建一个可关闭的HttpClient对象
            CloseableHttpClient httpclient = HttpClients.createDefault();
            //创建一个HttpPost的请求对象
            HttpPost httppost = new HttpPost(url);
            //设置json参数
            httppost.setEntity(new StringEntity(entityString));

            //加载请求头到httppost对象
            for (Map.Entry<String, String> entry : headermap.entrySet()) {
                Object value= entry.getValue();
                String a="";
                if(value instanceof JSONObject){
                    a=JSONObject.toJSONString(value);
                }else {
                    a=value.toString();
                }
                httppost.addHeader(entry.getKey(), a);
            }
            //发送post请求
            CloseableHttpResponse httpResponse = httpclient.execute(httppost);
            return httpResponse;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取响应状态码，常用来和TestBase中定义的状态码常量去测试断言使用
     *
     * @param response
     * @return 返回int类型状态码
     */
    public static int getStatusCode(CloseableHttpResponse response) {
        if(response==null){
            return 500;
        }else {
            int statusCode = response.getStatusLine().getStatusCode();
            return statusCode;
        }
    }

    /**
     * @param response, 任何请求返回的响应对象
     * @throws ParseException
     * @throws IOException
     * @return， 返回响应体的json格式对象，方便接下来对JSON对象内容解析
     * 接下来，一般会继续调用TestUtil类下的json解析方法得到某一个json对象的值
     */
    public static JSONObject getResponseJson(HttpResponse response) throws ParseException {
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            JSONObject responseJson = JSON.parseObject(responseString);
            return responseJson;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String getResponseString(HttpResponse response) throws ParseException, IOException {
        try {
            String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
            return responseString;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

//        InputStream in=entity.getContent();
//        File file = new File("./test.xml");
//        try {
//            FileOutputStream fout = new FileOutputStream(file);
//            int l = -1;
//            byte[] tmp = new byte[1024];
//            while ((l = in.read(tmp)) != -1) {
//                fout.write(tmp, 0, l);
//                // 注意这里如果用OutputStream.write(buff)的话，图片会失真，大家可以试试
//            }
//            fout.flush();
//            fout.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException ioException) {
//            ioException.printStackTrace();
//        } finally {
//            // 关闭低层流。
//            in.close();
//        }
    }
}
