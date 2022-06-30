package webdriver.utils;

import static webdriver.tests.AbstractBaseIT.sleep;

import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

public class BasePageUtils {

  /**
   * Waits for up to 10 seconds for the specified WebElement to become stale, and then releases
   * execution back to the calling Class. This method should be used when you know an action on a
   * page will cause the page to refresh. Instead of using an explicit thread sleep, this method
   * will wait for the page to refresh, causing the specified element to become stale, and then
   * hands exits the loop once the refresh is complete.
   *
   * @param elementToInspect the element that will eventually refresh
   */
  public static void staleSafeWait(WebElement elementToInspect) {
    boolean hasElementBecomeStale = false;
    int i = 0;
    while (!hasElementBecomeStale && i < 100) {
      try {
        elementToInspect.isDisplayed();
        i++;
        sleep(100);
      } catch (StaleElementReferenceException e) {
        hasElementBecomeStale = true;
      }
    }
  }
}
