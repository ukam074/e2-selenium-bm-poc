package webdriver.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WaitTimeManager {
  private static ConfigFileReader reader;

  public WaitTimeManager(){
    reader = FileReaderManager.getInstance().getConfigReader();
  }

  public static long getImplicitlyWait() {
    String implicitWait = reader.getProperty("implicitWait");
    if(implicitWait != null) {
      try{
        return Long.parseLong(implicitWait);
      }catch(NumberFormatException e) {
        throw new RuntimeException("Not able to parse value : " + implicitWait + " in to Long");
      }
    }
    return 30;
  }

  public static long getDefaultWait() {
    String wait = reader.getProperty("defaultWait");
    if(wait != null) {
      try{
        return Long.parseLong(wait);
      }catch(NumberFormatException e) {
        throw new RuntimeException("Not able to parse value : " + wait + " in to Long");
      }
    }
    return 30;
  }

  public static long getShortWait() {
    String wait = reader.getProperty("shortWait");
    if(wait != null) {
      try{
        return Long.parseLong(wait);
      }catch(NumberFormatException e) {
        throw new RuntimeException("Not able to parse value : " + wait + " in to Long");
      }
    }
    return 3;
  }

  public static long getLongWait() {
    String wait = reader.getProperty("longWait");
    if(wait != null) {
      try{
        return Long.parseLong(wait);
      }catch(NumberFormatException e) {
        throw new RuntimeException("Not able to parse value : " + wait + " in to Long");
      }
    }
    return 120;
  }
  public static void untilJqueryIsDone(WebDriver driver){
    untilJqueryIsDone(driver, getImplicitlyWait() );
  }

  public static void untilJqueryIsDone(WebDriver driver, Long timeoutInSeconds){
    until(driver, (d) ->
    {
      Boolean isJqueryCallDone = (Boolean)((JavascriptExecutor) driver).executeScript("return jQuery.active==0");
      if (!isJqueryCallDone) System.out.println("JQuery call is in Progress");
      return isJqueryCallDone;
    }, timeoutInSeconds);
  }

  public static void untilPageLoadComplete(WebDriver driver) {
    untilPageLoadComplete(driver, getImplicitlyWait());
  }

  public static void untilPageLoadComplete(WebDriver driver, Long timeoutInSeconds){
    until(driver, (d) ->
    {
      Boolean isPageLoaded = (Boolean)((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
      if (!isPageLoaded) System.out.println("Document is loading");
      return isPageLoaded;
    }, timeoutInSeconds);
  }

  public static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition){
    until(driver, waitCondition, getImplicitlyWait());
  }


  private static void until(WebDriver driver, Function<WebDriver, Boolean> waitCondition, Long timeoutInSeconds){
    WebDriverWait webDriverWait = new WebDriverWait(driver, timeoutInSeconds);
    webDriverWait.withTimeout(timeoutInSeconds, TimeUnit.SECONDS);
    try{
      webDriverWait.until(waitCondition);
    }catch (Exception e){
      System.out.println(e.getMessage());
    }
  }

}
