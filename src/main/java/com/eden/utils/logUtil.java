package com.eden.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class logUtil {
    private final Class<?> clazz;
    private Logger logger;

    //这里是定义logUtil的构造函数，因为不明确需要记录log的类是什么样子的，所以使用泛型Class<?> clazz
    public logUtil(Class<?> clazz) {
        this.clazz = clazz;
        this.logger = Logger.getLogger(this.clazz);
        logUtil.initlog4j();
    }

    //定义记录log的方法
    private static void initlog4j() {
        //创建Propderties对象
        Properties prop = new Properties();
        /*Log4j建议只使用四个级别，优先级从高到低分别是ERROR、WARN、INFO、DEBUG
        这里定义能显示到的最低级别,若定义到INFO级别,则看不到DEBUG级别的信息了~!级别后面是输出端*/
//        prop.setProperty("log4j.rootLogger", "INFO,CONSOLE,E,F");
        prop.setProperty("log4j.rootLogger", "INFO,CONSOLE,F");
        prop.setProperty("log4j.appender.CONSOLE", "org.apache.log4j.ConsoleAppender");
        prop.setProperty("log4j.appender.CONSOLE.layout", "org.apache.log4j.PatternLayout");
        prop.setProperty("log4j.appender.CONSOLE.layout.ConversionPattern", "[%d{YYYY-MM-dd HH:mm:ss,SSS}] %-5p %c %m%n");

        //设置日志输出的路径
        String src = "test-output/log";
        //设置日期格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //获取当前日期，并根据当前的日期建立文件夹，将生成的.log文件放入当前日期的文件夹。
        String date = dateFormat.format(new Date()).toString();
        File dir = new File(src + "/" + date);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filepath = dir.getAbsolutePath() + "/" + "log_" + date + ".log";

//        prop.setProperty("log4j.appender.E", "org.apache.log4j.FileAppender");
//        prop.setProperty("log4j.appender.E.file", filepath);
//        prop.setProperty("log4j.appender.E.layout", "org.apache.log4j.PatternLayout");
//        prop.setProperty("log4j.appender.E.layout.ConversionPattern", "[%d{YYYY-MM-dd HH:mm:ss,SSS}] %-5p %c %m%n");
        prop.setProperty("log4j.appender.F", "org.apache.log4j.FileAppender");
        prop.setProperty("log4j.appender.file.encoding", "UTF-8");
        //生成log格式的日志，并将生成的.log的日志文件放入当前日期的文件夹。
        String filepathHtml = dir.getAbsolutePath() + "/" + "log_" + date + ".log";
        prop.setProperty("log4j.appender.F.file", filepathHtml);
        prop.setProperty("log4j.appender.F.layout", "org.apache.log4j.PatternLayout");
        prop.setProperty("log4j.appender.F.layout.ConversionPattern", "[%d{YYYY-MM-dd HH:mm:ss,SSS}] %-5p %c %m%n");
        PropertyConfigurator.configure(prop);
    }

    public void getTrace(Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        logger.error(buffer);
    }

    public void info(String message) {
        logger.info(message);
    }

    public void debug(String message) { logger.debug(message); }

    public void error(String message) { logger.error(message); }

    public void trace(String message) {
        logger.trace(message);
    }
}
