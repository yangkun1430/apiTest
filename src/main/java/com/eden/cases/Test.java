package com.eden.cases;

import com.alibaba.fastjson.JSONObject;
import com.eden.bean.ApiDataBean;
import com.eden.utils.DataBaseUtil;
import com.eden.utils.HttpUtil;
import com.eden.utils.PDFUtil;
import com.eden.utils.logUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft_6455;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class Test extends Base {
    logUtil log = new logUtil(Test.class);
    Document document = new Document(PageSize.A4);
    PDFUtil pdfUtil = new PDFUtil();

    String auth=auth();
    String filePath=path();

    //报告路径
    public String auth(){
        System.out.println("请输入测试人员名称：");
        Scanner scan = new Scanner(System.in);
        String auth = scan.next();
        return auth;
    }

    public String path(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
        String nowDate = formatter.format(date);
        String filePath="/home/apiTestReport/"+auth + "_TestReport_" + nowDate + ".pdf";
        return filePath;
    }


    // 定义全局的字体静态变量
    private static Font titlefont;
    private static Font headfont;
    private static Font keyfont;
    private static Font textfont;
    // 最大宽度
    private static int maxWidth = 520;

    // 静态代码块
    static {
        try {
            BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            titlefont = new Font(bfChinese, 16, Font.BOLD);
            headfont = new Font(bfChinese, 14, Font.BOLD);
            keyfont = new Font(bfChinese, 10, Font.BOLD);
            textfont = new Font(bfChinese, 10, Font.NORMAL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeTest
    public void openDoc() {
        try {
            addTimeKey();
            File absDir=new File("/home/apiTestReport/");
            if(!absDir.exists()){
                absDir.mkdir();
            }
            File file = new File(filePath);
            file.createNewFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            document.addTitle(auth + "接口测试报告");
            Paragraph paragraph = new Paragraph("API Autotest Report("+auth+")", titlefont);
            paragraph.setAlignment(1); //设置文字居中 0靠左   1，居中     2，靠右
            paragraph.setIndentationLeft(12); //设置左缩进
            paragraph.setIndentationRight(12); //设置右缩进
            paragraph.setFirstLineIndent(24); //设置首行缩进
            paragraph.setLeading(20f); //行间距
            paragraph.setSpacingBefore(5f); //设置段落上空白
            paragraph.setSpacingAfter(10f); //设置段落下空白
            document.add(paragraph);
            // 直线
            Paragraph p1 = new Paragraph();
            p1.add(new Chunk(new LineSeparator()));
            document.add(p1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @org.testng.annotations.Test
    public void test() throws IOException, NoSuchAlgorithmException, InterruptedException {
        JSONObject jsonObject = DataBaseUtil.orderDataJson(auth);
        JSONObject RSjson = null;
        LinkedHashMap<String, ApiDataBean> linkedHashMap = DataBaseUtil.getLinkedApiMap(jsonObject);
        int size = linkedHashMap.size();
        for (int i = 1; i <= size; i++) {
            List<Boolean> verifyRS = new ArrayList<>();
            ApiDataBean api = linkedHashMap.get(i + "");
            String url = url(api);
            log.info(">>>>>>>>开始测试API(" + api.getText() + ")：" + url);
            try {
                if (api.getMethod().equals("get")) {
                    HttpResponse response = HttpUtil.get(url, headMap(api));
                    int responseCode=response.getStatusLine().getStatusCode();
                    try {
                        RSjson = HttpUtil.getResponseJson(response);
                        String responseJsonString=getResponseJsonString(RSjson);
                        verifyRS = verifySuccessJson(api, RSjson);
                        if (responseCode!=200){
                            verifyRS.add(false);
                            failedReason.put(api.getText(),responseJsonString);
                        }
                    } catch (Exception e) {
                        response = HttpUtil.get(url, headMap(api));
                        String responseText=getResponseTextString(response);
                        verifyRS = verifySuccessText(api, responseText);
                        if (responseCode!=200){
                            verifyRS.add(false);
                            failedReason.put(api.getText(),responseText);
                        }
                    }
                } else if (api.getMethod().equals("post")) {
                    CloseableHttpResponse response = HttpUtil.post(url, params(api), headMap(api));
                    int responseCode=response.getStatusLine().getStatusCode();
                    try {
                        RSjson = HttpUtil.getResponseJson(response);
                        String responseJsonString =getResponseJsonString(RSjson);
                        verifyRS = verifySuccessJson(api, RSjson);
                        if (responseCode!=200){
                            verifyRS.add(false);
                            failedReason.put(api.getText(),responseJsonString);
                        }
                    } catch (Exception e) {
                        response = HttpUtil.post(url, params(api), headMap(api));
                        String responseText=HttpUtil.getResponseString(response);
                        verifyRS = verifySuccessText(api, responseText);
                        if (responseCode!=200){
                            verifyRS.add(false);
                            failedReason.put(api.getText(),responseText);
                        }
                    }
                }else if(api.getMethod().equals("websocket")){
                    Boolean needCookie=needHeaders(api);
                    TestWebsocket testWebsocket;
                    if(needCookie){
                        System.out.println(url.replace("wss","ws"));
                        testWebsocket=new TestWebsocket(new URI(url.replace("wss","ws")),new Draft_6455(),headMap(api),5000);
                        testWebsocket.connect();
                    }else {
                        testWebsocket=new TestWebsocket(new URI(url.replace("wss","ws")));
                        testWebsocket.connect();
                    }
                    int contime=0;
                    while(!testWebsocket.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
                        Thread.sleep(1000);
//                        log.info(testWebsocket.getReadyState()+"");
                        contime++;
                        if(contime>10){
                            verifyRS.add(false);
                            failedReason.put(api.getText(),"websocket建立连接超过10s未成功");
                        }
                    }
                    testWebsocket.send(params(api));
                    Thread.sleep(5000);
                    testWebsocket.close();
                    if(!TestWebsocket.getWsText().equals("")){
                        verifyRS = verifySuccessWS(api, TestWebsocket.getWsText());
                    }else {
                        verifyRS.add(false);
                        failedReason.put(api.getText(),"5s未得到返回值，判断为测试失败");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                verifyRS.add(false);
            }

            Boolean containsFalse = verifyRS.contains(false);
            if (containsFalse) {
                failedMap.put(api.getText(), url);
                log.error("<<<<<<<<测试失败(" + api.getText() + ")--API:" + url);
                log.info("已经临时储存的key：" + map.toString());
                log.info("=========================================================");
            } else {
                successMap.put(api.getText(), url);
                log.info("<<<<<<<测试通过(" + api.getText() + ")--API:" + url);
                saved(api, RSjson);
                log.info("已经临时储存的key：" + map.toString());
                sleep(api);
            }
        }
    }

    @AfterTest
    public void printResult() {
        try {
            int s = 1;
            int f = 1;
            System.out.println("\n");
            System.out.println("*************************************************************************");

            if (failedMap.isEmpty()) {
            } else {
                System.out.println("测试失败的接口:");
                // 表格
                PdfPTable failedtable = pdfUtil.createTable(new float[]{30, 80, 50, 160,80, 160});
                failedtable.addCell(pdfUtil.createCell("测试失败：", headfont, Element.ALIGN_LEFT, 6, false));
                failedtable.addCell(pdfUtil.createCell("序号", keyfont, Element.ALIGN_CENTER));
                failedtable.addCell(pdfUtil.createCell("接口描述", keyfont, Element.ALIGN_CENTER));
                failedtable.addCell(pdfUtil.createCell("测试结果", keyfont, Element.ALIGN_CENTER));
                failedtable.addCell(pdfUtil.createCell("接口路径", keyfont, Element.ALIGN_CENTER));
                failedtable.addCell(pdfUtil.createCell("未包含期望值", keyfont, Element.ALIGN_CENTER));
                failedtable.addCell(pdfUtil.createCell("异常返回结果", keyfont, Element.ALIGN_CENTER));
                for (String key : failedMap.keySet()) {
                    System.out.println(f + ". " + key + "  " + failedMap.get(key));
                    System.out.println("返回结果：" + failedReason.get(key));
                    // 表格
                    failedtable.addCell(pdfUtil.createCell(f + "", textfont));
                    failedtable.addCell(pdfUtil.createCell(key, textfont,Element.ALIGN_LEFT));
                    failedtable.addCell(pdfUtil.createCell("Failed", textfont));
                    failedtable.addCell(pdfUtil.createCell(failedMap.get(key), textfont,Element.ALIGN_LEFT));
                    failedtable.addCell(pdfUtil.createCell(failedWantValue.get(key), textfont,Element.ALIGN_LEFT));
                    failedtable.addCell(pdfUtil.createCell(failedReason.get(key), textfont,Element.ALIGN_LEFT));

                    f++;
                }
                document.add(failedtable);
            }

            if (successMap.isEmpty()) {
            } else {
                System.out.println("\n测试通过的接口:");
                // 表格
                PdfPTable passtable = pdfUtil.createTable(new float[]{30, 80, 50, 160, 160});
                passtable.addCell(pdfUtil.createCell("测试通过：", headfont, Element.ALIGN_LEFT, 6, false));
                passtable.addCell(pdfUtil.createCell("序号", keyfont, Element.ALIGN_CENTER));
                passtable.addCell(pdfUtil.createCell("接口描述", keyfont, Element.ALIGN_CENTER));
                passtable.addCell(pdfUtil.createCell("测试结果", keyfont, Element.ALIGN_CENTER));
                passtable.addCell(pdfUtil.createCell("接口路径", keyfont, Element.ALIGN_CENTER));
                passtable.addCell(pdfUtil.createCell("返回结果", keyfont, Element.ALIGN_CENTER));
                for (String key : successMap.keySet()) {
                    System.out.println(s + ". " + key + "  " + successMap.get(key));
                    // 表格
                    passtable.addCell(pdfUtil.createCell(s + "", textfont));
                    passtable.addCell(pdfUtil.createCell(key, textfont,Element.ALIGN_LEFT));
                    passtable.addCell(pdfUtil.createCell("Pass", textfont));
                    passtable.addCell(pdfUtil.createCell(successMap.get(key), textfont,Element.ALIGN_LEFT));
                    passtable.addCell(pdfUtil.createCell(successReason.get(key), textfont,Element.ALIGN_LEFT));

                    s++;
                }
                document.add(passtable);
            }

            System.out.println("*************************************************************************");
//            System.out.println("\n测试报告路径："+System.getProperty("user.dir")+"/"+filePath);
            System.out.println("\n测试报告路径："+filePath);
            document.close();
            CloseableHttpResponse response = HttpUtil.get("http://10.12.128.222:8181/apiTest/stop?auth="+auth);
            String responseText=getResponseTextString(response);
            System.out.println(responseText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
