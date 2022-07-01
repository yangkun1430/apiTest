package com.eden.utils;

import com.alibaba.fastjson.JSONObject;
import com.eden.cases.Base;
import com.eden.cases.TestWebsocket;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft_6455;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class tes {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpget = new HttpGet("https://tvunetworks.atlassian.net/sr/jira.issueviews:searchrequest-xml/temp/SearchRequest.xml?jqlQuery=reporter+in+%28currentUser%28%29%29++and+Created+%3C+Now%28%29+AND+Created+%3E%3D+%22-2d%22+order+by+created+DESC&tempMax=1000");
//        httpget.addHeader("Authorization","Basic ZWRlbnlhbmdAdHZ1bmV0d29ya3MuY29tOkxzRHdQc05MWjFuNGRxTTlROGNEMzEzQQ==");
//        HttpResponse response = httpclient.execute(httpget);
//        HttpEntity entity = response.getEntity();
//        InputStream in = entity.getContent();
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
//        httpclient.close();
//        String appkey="d8e8560b4df405f1e7580262b4652008";
//        String appsecret="d9aa5247ff9336c95b7941604d987f6a";
//        String timestamp=Long.toString(System.currentTimeMillis());
//        String requestId= UUID.randomUUID().toString().trim().replaceAll("-", "");
//        String signature= DigestUtils.md5Hex(appsecret+timestamp);
//        Map<String,String> accesskeyMap=new HashMap<>();
//        accesskeyMap.put("appkey",appkey);
//        accesskeyMap.put("timestamp",timestamp);
//        accesskeyMap.put("signature",signature);
//        accesskeyMap.put("requestId",requestId);
//        new Base().addTimeKey();
        TestWebsocket testWebsocket=new TestWebsocket(new URI("ws://cc-ms-qa1.tvunetworks.com/ws/pageRequest?token=7777C08DE042424AA22E1F731FC58D55&taskId=huJ6dU1L9o5x1yyZ4EXyj0Po0wWFEgVj_q"),new Draft_6455());
        testWebsocket.connect();
        while (!testWebsocket.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            System.out.println(testWebsocket.getReadyState());
        }
        testWebsocket.send("{\"type\":\"shareNum\",\"module\":\"shareList\",\"data\":{\"flag\":false,\"condition\":\"\"}}");
        Thread.sleep(2000);
        System.out.println("sss"+testWebsocket.getWsText());
//        System.out.println(JSONObject.toJSONString(accesskeyMap));
    }
}
