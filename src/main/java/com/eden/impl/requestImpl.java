package com.eden.impl;

import com.alibaba.fastjson.JSONObject;
import com.eden.bean.ApiDataBean;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

public interface requestImpl {
    boolean isRun(ApiDataBean apiDataBean);

    int order(ApiDataBean apiDataBean);

    boolean needHeaders(ApiDataBean apiDataBean);

    boolean needParams(ApiDataBean apiDataBean);

    String params(ApiDataBean apiDataBean);

    String url(ApiDataBean apiDataBean);

    void sleep(ApiDataBean apiDataBean) throws InterruptedException;

    List<Boolean> verifySuccessJson(ApiDataBean apiDataBean, JSONObject json);

    List<Boolean> verifySuccessText(ApiDataBean apiDataBean, String text);

    void saved(ApiDataBean apiDataBean, JSONObject jsonObject) throws IOException, NoSuchAlgorithmException;

    void sendRequest(ApiDataBean apiDataBean);

    Map<String, String> headMap(ApiDataBean apiDataBean) throws NoSuchAlgorithmException;

    String replace(String s) throws IOException;

    JSONObject getResponse(ApiDataBean apiDataBean) throws IOException, NoSuchAlgorithmException;

    String getResponseJsonString(JSONObject jsonObject);

    String getResponseTextString(HttpResponse response) throws IOException;
}
