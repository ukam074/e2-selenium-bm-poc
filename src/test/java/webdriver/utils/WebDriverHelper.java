package webdriver.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static webdriver.tests.AbstractBaseIT.sleep;

public class WebDriverHelper {

  private RemoteWebDriver driver;
  private WebDriverWait wait;

  public WebDriverHelper(RemoteWebDriver driver) {
    this.driver = driver;
    wait = new WebDriverWait(this.driver, 60);
  }

  /**
   * Wrap this around generic WebDriver methods for situations where expected results are not consistent.
   * This doesn't help understand the root cause of the problem, but it is a 'band-aid'
   * Only use this to wrap around methods that return void, such as .click() methods
   *
   * @param r The method to be invoked within the exception handling logic.
   */
  public void bufferExceptions(Runnable r) {
    for (int i = 0; i < 2; i++) {
      try {
        r.run();
        break;
      } catch (Exception e) {
       // e.printStackTrace();
      }
    }
  }

  /**
   * Wrap this around generic Webdriver methods for situations where you want to execute a callable function
   * and explicitly sleep if it fails the first few times. Seems immediately usable for code where
   * an ExpectedConditions test fails after a new page loads, despite the wait condition being logically correct.
   * @param r The method to be invoked within the exception handling logic.
   */
  public void sleepBuffer(Runnable r) {
    for (int i = 0; i < 10; i++) {
      try {
        sleep(1000);
        r.run();
      } catch (Exception e) {
        System.out.println("Slept for " + (i + 1) + " second(s) waiting for runnable to complete successfully.");
      //  e.printStackTrace();
      }
    }
  }

  public void safeSetGenericInputTextXpath(String xpath, String inputText) {
    clearTheFieldXpath(xpath);
    driver.findElement(By.xpath(xpath)).sendKeys(inputText, Keys.TAB);
    try {
      new WebDriverWait(driver, 30).
              until(ExpectedConditions.textToBePresentInElementValue(driver.findElement(By.xpath(xpath)), inputText));
    }catch(Exception e) {}

  }

  public void safeSetGenericInputText( String elementId, String inputText) {
    WebDriverWait wait = new WebDriverWait(driver, 30);
    sleep(1000);
    clearTheField(elementId);
    sleep(2000);
    driver.findElement(By.id(elementId)).sendKeys(inputText, Keys.TAB);
    try {
      wait.until(ExpectedConditions.textToBePresentInElementValue(driver.findElementById(elementId), inputText));
    }catch(Exception e) {}
  }

  //Clear the field.
  public void clearTheField(String locator) {
    int fieldLength = 0;
    try {
      fieldLength = driver.findElementById(locator).getAttribute("value").length();
      if (fieldLength == 0) {
        return;
      }
    } catch (Exception e) {
    }

    StringBuilder backSpaceSequence = new StringBuilder();
    for (int i = 0; i < fieldLength; i++) {
      backSpaceSequence.append(Keys.BACK_SPACE);
    }
    driver.findElementById(locator).sendKeys(backSpaceSequence);
    try {
      new WebDriverWait(driver, 5).until(
          ExpectedConditions.textToBePresentInElementValue(driver.findElementById(locator), ""));
    } catch (Exception e) {
    }
  }

  //Clear the field.
  public void clearTheFieldXpath(String locator) {
    int fieldLength = 0;
    try {
      fieldLength = driver.findElementByXPath(locator).getAttribute("value").length();
      if (fieldLength == 0) {
        return;
      }
    } catch (Exception e) {
    }

    StringBuilder backSpaceSequence = new StringBuilder();
    for (int i = 0; i < fieldLength; i++) {
      backSpaceSequence.append(Keys.BACK_SPACE);
    }
    driver.findElementByXPath(locator).sendKeys(backSpaceSequence);
    try {
      new WebDriverWait(driver, 5).until(
              ExpectedConditions.textToBePresentInElementValue(driver.findElementByXPath(locator), ""));
    } catch (Exception e) {
    }
  }

  /**
   * Uses Javascript to click an element to avoid scrolling the viewport.
   *
   * @param element The WebElement to click.
   */
  public void javascriptClick(WebElement element) {
    wait.until(ExpectedConditions.visibilityOf(element));
    wait.until(ExpectedConditions.elementToBeClickable(element));
    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
  }

  /**
   * Uses Javascript to click an element to avoid scrolling the viewport.
   *
   * @param element The By locator of the element to click.
   */
  public void javascriptClick(By element) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(element));
    wait.until(ExpectedConditions.elementToBeClickable(element));
    ((JavascriptExecutor) driver)
        .executeScript("arguments[0].click();", driver.findElement(element));
  }

  /**
   * Uses Javascript to "sendKeys" to an element to workaround a gheckodriver bug that doesn't issue a change command
   * after sendKeys action. Substitute ths method for a "sendKeys" if your sendKeys value clears or resets.
   *
   * @param element The WebElement to click.
   */
  public void javascriptSendKeys(WebElement element, String value) {
    ((JavascriptExecutor) driver).executeScript("arguments[0].value=arguments[1]", element, value);
    ((JavascriptExecutor) driver).executeScript("arguments[0].focus();  return true", element);
  }
}
