package com.eden.cases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eden.bean.ApiDataBean;
import com.eden.impl.requestImpl;
import com.eden.utils.HttpUtil;
import com.eden.utils.logUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Base implements requestImpl {
    HashMap<String, String> map = new HashMap<>();
    logUtil log = new logUtil(Base.class);
    Map<String, String> successMap = new LinkedHashMap<>();
    Map<String, String> failedMap = new LinkedHashMap<>();
    Map<String, String> successReason = new LinkedHashMap<>();
    Map<String, String> failedReason = new LinkedHashMap<>();
    Map<String, String> failedWantValue = new LinkedHashMap<>();

    @Override
    public boolean isRun(ApiDataBean apiDataBean) {
        if (apiDataBean.getRun().equals("y")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int order(ApiDataBean apiDataBean) {
        return apiDataBean.getOrder();
    }

    @Override
    public boolean needHeaders(ApiDataBean apiDataBean) {
        if (apiDataBean.getHeader().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean needParams(ApiDataBean apiDataBean) {
        if (apiDataBean.getParam().equals("")) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public String params(ApiDataBean apiDataBean) {
        if (needParams(apiDataBean) == true) {
            String p = apiDataBean.getParam();
            int replaceTimes = apiDataBean.getParam().length() - apiDataBean.getParam().replaceAll("\\$", "").length();
            for (int i = 0; i < replaceTimes; i++) {
                p = replace(p);
            }
            log.info("参数：" + p);
            return p;
        } else {
            log.info("参数：" + "没有");
            return "";
        }
    }

    @Override
    public String url(ApiDataBean apiDataBean) {
        String url = apiDataBean.getUrl();
        int replaceTimes = apiDataBean.getUrl().length() - apiDataBean.getUrl().replaceAll("\\$", "").length();
        for (int i = 0; i < replaceTimes; i++) {
            url = replace(url);
        }
//        log.info("URL：" + url);
        return url;
    }


    @Override
    public void sleep(ApiDataBean apiDataBean) throws InterruptedException {
        log.info("线程等待" + apiDataBean.getSleep() + "秒后将测试下一个API");
        log.info("=========================================================");
        Thread.sleep(apiDataBean.getSleep() * 1000);
    }

    @Override
    public List<Boolean> verifySuccessJson(ApiDataBean apiDataBean, JSONObject json) {
        List<Boolean> verifyRS = new ArrayList<>();
        Boolean verifyResult = false;
        String verifyString = apiDataBean.getVerify();
            String[] verifyList = verifyString.split(",");
            for (String verify : verifyList) {
                String trimValue = verify.replace(" ", "");
                if (trimValue.contains("$")) {
                    trimValue = replace(trimValue);
                }
                if (json.toJSONString().replace(" ", "").contains(trimValue)) {
                    successReason.put(apiDataBean.getText(), json.toJSONString());
                    if(replace(verify)!=""){
                        log.info("验证通过！返回结果包含：" + replace(verify));
                    }else {
                        log.info("未设置验证值");
                    }
                    verifyResult = true;
                } else {
                    failedReason.put(apiDataBean.getText(), json.toJSONString());
                    failedWantValue.put(apiDataBean.getText(), replace(verify));
                    log.error("验证失败！期望返回的值：" + replace(verify) + ",实际结果：" + json.toJSONString());
                    verifyResult = false;
                }
                verifyRS.add(verifyResult);
            }

        return verifyRS;
    }

    public List<Boolean> verifySuccessWS(ApiDataBean apiDataBean,String text){
        List<Boolean> verifyRS = new ArrayList<>();
        Boolean verifyResult = false;
        String verifyString = apiDataBean.getVerify();
        String[] verifyList = verifyString.split(",");
        for (String verify : verifyList) {
            String trimValue = verify.replace(" ", "");
            if (trimValue.contains("$")) {
                trimValue = replace(trimValue);
            }
            if (text.replace(" ", "").contains(trimValue)) {
                successReason.put(apiDataBean.getText(), text);
                if(replace(verify)!=""){
                    log.info("验证通过！返回结果包含：" + replace(verify));
                }else {
                    log.info("未设置验证值");
                }
                verifyResult = true;
            } else {
                failedReason.put(apiDataBean.getText(), text);
                failedWantValue.put(apiDataBean.getText(), replace(verify));
                log.error("验证失败！期望返回的值：" + replace(verify) + ",实际结果：" + text);
                verifyResult = false;
            }
            verifyRS.add(verifyResult);
        }
        return verifyRS;
    }

    @Override
    public List<Boolean> verifySuccessText(ApiDataBean apiDataBean, String text) {
        List<Boolean> verifyRS = new ArrayList<>();
        Boolean verifyResult = false;
        String verifyString = apiDataBean.getVerify();
            String[] verifyList = verifyString.split(",");
            for (String verify : verifyList) {
                String trimValue = verify.replace(" ", "");
                if (trimValue.contains("$")) {
                    trimValue = replace(trimValue);
                }
                if (text.replace(" ", "").contains(trimValue)) {
                    successReason.put(apiDataBean.getText(), text);
                    if(replace(verify)!=""){
                        log.info("验证通过！返回结果包含：" + replace(verify));
                    }else {
                        log.info("未设置验证值");
                    }
                    verifyResult = true;
                } else {
                    failedReason.put(apiDataBean.getText(), text);
                    failedWantValue.put(apiDataBean.getText(), replace(verify));
                    log.error("验证失败！期望返回的值：" + replace(verify) + ",实际结果：" + text);
                    verifyResult = false;
                }
                verifyRS.add(verifyResult);
            }
        return verifyRS;
    }

    @Override
    public void saved(ApiDataBean apiDataBean, JSONObject jsonObject) {
        if (apiDataBean.getSavekey().equals("")) {

        } else {
            String savekeys = apiDataBean.getSavekey();
            String[] keys = savekeys.split(",");
            for (String key : keys) {
                JSONObject rsjson = jsonObject;
                if (key.contains("-") && key.contains("[") == false) {
                    String[] k1s = key.split("-");
                    String savekey=subString(k1s[k1s.length-1],"(",")");
                    k1s[k1s.length-1]=k1s[k1s.length-1].split("\\(")[0];
                    for (String k1 : k1s) {
                        try {
                            JSONObject jb = rsjson.getJSONObject(k1);
                            rsjson = jb;
                        } catch (Exception e) {
                            map.put(savekey, rsjson.getString(k1));
//                        log.error(2 + k1 + ":" + rsjson.getString(k1));
                        }
                    }
                } else if (key.contains("[") && key.contains("]")) {
                    String[] ks = key.split("-");
                    String savekey=subString(ks[ks.length-1],"(",")");
                    ks[ks.length-1]=ks[ks.length-1].split("\\(")[0];
                    for (int i = 0; i < ks.length; i++) {
                        if (ks[i].contains("[")) {
                            int strEndIndex = ks[i].indexOf("[");
                            String subKey = ks[i].substring(0, strEndIndex);
                            JSONArray jsonArray = rsjson.getJSONArray(subKey);
                            String indexString = subString(ks[i], "[", "]");
                            int index = Integer.parseInt(indexString) - 1;
                            rsjson = (JSONObject) jsonArray.get(index);
                        } else {
                            try {
                                JSONObject jb = rsjson.getJSONObject(ks[i]);
                                rsjson = jb;
                            } catch (Exception e) {
                                map.put(savekey, rsjson.getString(ks[i]));
//                        log.error(2 + k1 + ":" + rsjson.getString(k1));
                            }
                        }
                    }
                } else {
                    String savekey=subString(key,"(",")");
                    key=key.split("\\(")[0];
                    map.put(savekey, rsjson.getString(key));
//                log.error(1 + rsjson.getString(key));
                }
            }
        }
    }

    @Override
    public void sendRequest(ApiDataBean apiDataBean) {

    }

    public void addAccessKey() {
        String appkey = "d8e8560b4df405f1e7580262b4652008";
        String appsecret = "d9aa5247ff9336c95b7941604d987f6a";
        String timestamp = Long.toString(System.currentTimeMillis());
        String requestId = UUID.randomUUID().toString().trim().replaceAll("-", "");
        String signature = DigestUtils.md5Hex(appsecret + timestamp);
        Map<String, String> accesskeyMap = new HashMap<>();
        accesskeyMap.put("appkey", appkey);
        accesskeyMap.put("timestamp", timestamp);
        accesskeyMap.put("signature", signature);
        accesskeyMap.put("requestId", requestId);
        map.put("accesskey", JSONObject.toJSONString(accesskeyMap));
    }

    public void addTimeKey(){
        long now=System.currentTimeMillis();
        Long  time = System.currentTimeMillis();  //当前时间的时间戳
        long todayStart = time/(1000*3600*24)*(1000*3600*24) - TimeZone.getDefault().getRawOffset();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),23,59,59);
        long todayEnd = calendar.getTime().getTime();
        long yesterday=now-86400000;
        long tomorrow=now+86400000;
        long lastWeek=now-86400000*7;
        map.put("now",now+"");
        map.put("todayStart",todayStart+"");
        map.put("todayEnd",todayEnd+"");
        map.put("yesterday",yesterday+"");
        map.put("tomorrow",tomorrow+"");
        map.put("lastWeek",lastWeek+"");
//        System.out.println(map);
    }

    @Override
    public Map<String, String> headMap(ApiDataBean apiDataBean) {
        Map<String, String> headMap = new HashMap<>();
        if (needHeaders(apiDataBean) == true) {
            addAccessKey();
            String h = apiDataBean.getHeader();
            if (h.contains("${")) {
                String h1 = replace(apiDataBean.getHeader());
                String h2 = h1.replace("\"{\"signature", "{\"signature");
                String h3 = h2.replace("\"}\"", "\"}");
                headMap = (Map) JSON.parse(h3);
//                headMap.put("Content-Type", "application/json");
                log.info("请求头：" + headMap);
                return headMap;
            } else {
                headMap = (Map) JSON.parse(h);
//                headMap.put("Content-Type", "application/json");
                log.info("请求头：" + headMap);
                return headMap;
            }
        } else {
//            headMap.put("Content-Type", "application/json");
//            log.info("请求头：" + "未添加请求头");
            return headMap;
        }
    }

    @Override
    public String replace(String s) {
        if (s.contains("${")) {
            Pattern replaceParamPattern = Pattern.compile("\\$\\{(.*?)\\}");
            Matcher m = replaceParamPattern.matcher(s);
            int i = m.groupCount();
            for (int j = 0; j < i; j++) {
                m.find();
                String key = m.group(i);
                if(map.get(key)!=null){
                    s = s.replace("${" + key + "}", map.get(key));
                }else {
                    log.error("出现异常！！！！因为接口需要用到保存的key:"+key+" 不存在，测试无法进行");
                    return null;
                }
            }
            return s;
        } else {
            return s;
        }

    }

    @Override
    public JSONObject getResponse(ApiDataBean apiDataBean) throws IOException {
        CloseableHttpResponse response = HttpUtil.post(url(apiDataBean), params(apiDataBean), headMap(apiDataBean));
        JSONObject json = HttpUtil.getResponseJson(response);
        return json;
    }

    @Override
    public String getResponseJsonString(JSONObject jsonObject) {
        log.info("返回结果：" + jsonObject.toJSONString());
        return jsonObject.toJSONString();
    }

    @Override
    public String getResponseTextString(HttpResponse response) throws IOException {
        try {
            String responseText=HttpUtil.getResponseString(response);
            log.info("返回结果：" + responseText);
            return responseText;
        }catch (Exception e){
            e.printStackTrace();
            log.info("获取返回结果异常");
            return "获取返回结果异常";
        }
    }

    public String subString(String str, String strStart, String strEnd) {

        /* 找出指定的2个字符在 该字符串里面的 位置 */
        int strStartIndex = str.indexOf(strStart);
        int strEndIndex = str.indexOf(strEnd);

        /* index 为负数 即表示该字符串中 没有该字符 */
        if (strStartIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strStart + ", 无法截取目标字符串";
        }
        if (strEndIndex < 0) {
            return "字符串 :---->" + str + "<---- 中不存在 " + strEnd + ", 无法截取目标字符串";
        }
        /* 开始截取 */
        String result = str.substring(strStartIndex, strEndIndex).substring(strStart.length());
        return result;
    }

}
