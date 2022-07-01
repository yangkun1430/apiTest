package com.eden.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eden.bean.ApiDataBean;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Locale;

public class DataBaseUtil {
    static String Database = "jdbc:mysql://10.12.128.243:3306/api_test?useUnicode=true&characterEncoding=UTF-8";
    static String Database_name = "root";
    static String Database_password = "123456";
    static Connection con;

    //获取数据库连接
    static {
        try {
            con = DriverManager.getConnection(Database, Database_name, Database_password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //某个项目所有请求信息
    public static JSONObject apiDataJson(String application) {
        JSONArray array = new JSONArray();
        try {
            Statement stet = con.createStatement();
            String sql = "select * from apiData where application =" + "'" + application + "' order by `order`";
            ResultSet rs = stet.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                JSONObject jsonObj2 = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    try {
                        int sec = Integer.parseInt(value);
                        jsonObj2.put(columnName, sec);
                    } catch (Exception e) {
                        jsonObj2.put(columnName, value);
                    }
                    if (value == null) {
                        jsonObj2.put(columnName, "");
                    } else {
                        if (value.contains(":") && value.contains("{") && value.contains("}")) {
                            JSONObject jsonObj3 = JSONObject.parseObject(value);
                            jsonObj2.put(columnName, jsonObj3);
                        }
                    }
                }
                array.add(jsonObj2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String urlData = "{" + "\"" + application + "\"" + ":" + array.toString() + "}";
        JSONObject json = JSONObject.parseObject(urlData);
        return json;
    }

    //某个项目需要运行的请求信息
    public static JSONObject runApiDataJson(String application) {
        JSONArray array = new JSONArray();
        try {
            Statement stet = con.createStatement();
            String sql = "select * from apiData where application =" + "'" + application + "' order by `order`";
            ResultSet rs = stet.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            out:
            while (rs.next()) {
                JSONObject jsonObj2 = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    if (columnName.equals("run") && value.equals("n")) {
                        continue out;
                    } else {
                        try {
                            int sec = Integer.parseInt(value);
                            jsonObj2.put(columnName, sec);
                        } catch (Exception e) {
                            jsonObj2.put(columnName, value);
                        }
                        if (value == null) {
                            jsonObj2.put(columnName, "");
                        } else {
                            if (value.contains(":") && value.contains("{") && value.contains("}")) {
                                JSONObject jsonObj3 = JSONObject.parseObject(value);
                                jsonObj2.put(columnName, jsonObj3);
                            }
                        }
                    }
                }
                array.add(jsonObj2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String urlData = "{" + "\"" + application + "\"" + ":" + array.toString() + "}";
        JSONObject json = JSONObject.parseObject(urlData);
        return json;
    }

    //所有需要运行且排了执行顺序的请求信息
    public static JSONObject orderDataJson(String auth) {
        JSONArray array = new JSONArray();
        JSONObject jsonObj = new JSONObject();
        try {
            auth = auth.toLowerCase(Locale.ROOT);
            Statement stet = con.createStatement();
            String sql = "select * from apiData where run='y' and `order` <> 0 and auth like '%" + auth + "%' order by `order`";
            ResultSet rs = stet.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            int j = 1;
            while (rs.next()) {
                JSONObject jsonObj2 = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    String value = rs.getString(columnName);
                    try {
                        int sec = Integer.parseInt(value);
                        jsonObj2.put(columnName, sec);
                    } catch (Exception e) {
                        jsonObj2.put(columnName, value);
                    }
                    if (value == null) {
                        jsonObj2.put(columnName, "");
                    } else {
                        if (value.contains(":") && value.contains("{") && value.contains("}") && !value.contains(",$") && !value.contains("=$")) {
                            JSONObject jsonObj3 = JSONObject.parseObject(value);
                            jsonObj2.put(columnName, jsonObj3);
                        }
                    }
                }
                jsonObj.put(Integer.toString(j), jsonObj2);
                array.add(jsonObj);
                j++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj;
    }

    //根据描述获得bean对象--暂不用
//    public ApiDataBean Api(String text){
//        HashMap<String, ApiDataBean> apiMap=new HashMap<>();
//
//    }

    //有序map，用数据库顺序直接执行全部
    public static LinkedHashMap<String, ApiDataBean> getLinkedApiMap(JSONObject jsonObject) {
        LinkedHashMap<String, ApiDataBean> linkedHashMap = new LinkedHashMap<>();
//        System.out.println(jsonObject.toJSONString());
        int size = jsonObject.size();
        for (int i = 1; i <= size; i++) {
            JSONObject jsonObject1 = (JSONObject) jsonObject.get(i + "");
            int order = (int) jsonObject1.get("order");
            String application = (String) jsonObject1.get("application");
            String run = (String) jsonObject1.get("run");
            String text = (String) jsonObject1.get("text");
            String method = (String) jsonObject1.get("method");
            String url = (String) jsonObject1.get("url");
            String header = "";
            if (jsonObject1.get("header").equals("")) {
                header = "";
            } else {
                JSONObject h = (JSONObject) jsonObject1.get("header");
                header = h.toJSONString();
            }
            String param = "";
            if (jsonObject1.get("param").equals("")) {
                param = "";
            } else {
                JSONObject p = (JSONObject) jsonObject1.get("param");
                param = p.toJSONString();
            }

            int sleep = (int) jsonObject1.get("sleep");
            String verify = "";
            if (jsonObject1.get("verify").equals("")) {
                verify = "";
            } else {
                verify = jsonObject1.get("verify").toString();
            }
            String savekey = (String) jsonObject1.get("savekey");
            String auth = (String) jsonObject1.get("auth");
            ApiDataBean apiDataBean = new ApiDataBean(order, application, run, text, method, url, header, param, sleep, verify, savekey, auth);
            linkedHashMap.put(i + "", apiDataBean);
        }
        return linkedHashMap;
    }

}
