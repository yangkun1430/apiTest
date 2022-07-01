package com.eden.cases;

import com.eden.utils.logUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Map;

public class TestWebsocket extends WebSocketClient {
    private static String wsText="";
    logUtil log = new logUtil(TestWebsocket.class);

    public TestWebsocket(URI serverUri) {
        super(serverUri);
    }

    public TestWebsocket(URI serverUri, Draft protocolDraft) {
        super(serverUri, protocolDraft);
    }

    public TestWebsocket(URI serverUri, Draft protocolDraft, Map<String, String> httpHeaders, int connectTimeout) {
        super(serverUri, protocolDraft, httpHeaders, connectTimeout);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
//        log.info("打开websocket");
    }

    @Override
    public void onMessage(String s) {
        log.info("接收ws:"+s);
        wsText=s;
//        System.out.println(wsText);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        log.info("关闭websocket");
    }

    public static String getWsText(){
        return wsText;
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        log.error("websocket测试出现异常！");
    }
}
