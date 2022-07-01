package com.eden.bean;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Map;

@Data
public class ApiDataBean {
    private int order;
    private String application;
    private String run;
    private String text;
    private String method;
    private String url;
    private String header;
    private String param;
    private int sleep;
    private String verify;
    private String savekey;
    private String auth;

    public ApiDataBean(int order, String application, String run, String text, String method, String url, String header, String param, int sleep, String verify, String savekey,String auth) {
        this.order = order;
        this.application = application;
        this.run = run;
        this.text = text;
        this.method = method;
        this.url = url;
        this.header = header;
        this.param = param;
        this.sleep = sleep;
        this.verify = verify;
        this.savekey = savekey;
        this.auth = auth;
    }

}
