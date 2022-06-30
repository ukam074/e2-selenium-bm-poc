package webdriver.utils;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

import java.io.File;


public class ExtentWrapper {

    public String testName;
    private static String reportPath = "./src/test/resources/reports/";
    private static File reportFile = new File(reportPath + "extent.html");
    private static File reportDir = new File(reportPath);
    public static ExtentHtmlReporter htmlReporter = null;
    public static ExtentReports extent = null;
    public static ExtentTest test = null;
    public static ExtentTest node = null;

    //constructor
    public void ExtentWrapper(){
    }

    /**
     * remove existing extent report
     */
    public static void cleanReport() {
        try {
            reportFile.delete();
        } catch (Exception x){
            System.out.println(x.getMessage());
        }
    }


    public static void configExtentReport(){
        reportDir.mkdirs();
        htmlReporter = new ExtentHtmlReporter(reportFile);
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
        extent.setAnalysisStrategy(AnalysisStrategy.TEST);
    }

    public static void setExtentTest(String name){
        test = extent.createTest(name);
    }

    public static void setExtentNode(String name){
        node = test.createNode(name);
    }

    public static ExtentTest getExtentTest(){
        return test;
    }

}
