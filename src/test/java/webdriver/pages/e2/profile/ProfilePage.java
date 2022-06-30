package webdriver.pages.e2.profile;

import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import webdriver.pages.e2.AbstractE2BasePage;
import webdriver.utils.WebDriverHelper;

public class ProfilePage extends AbstractE2BasePage {

  public ProfilePage(RemoteWebDriver webdriver) {
    super(webdriver);
  }



  public String clickEditTravelPref() {
    String mainHandle = driver.getWindowHandle();
    //slh-changed to xpath, ie couldn't load javascript href using findbylinktext.
    WebElement element = driver.findElementByXPath("//*[@id='travelPrefSpan']/a");
    new WebDriverHelper(driver).javascriptClick(element);
    sleep(3000);

    Set<String> allHandles = driver.getWindowHandles();
    allHandles.remove(mainHandle);

    driver.switchTo().window(allHandles.stream().findAny().get());
    waitForGetThere();

    WebElement e = driver.findElementByLinkText("Personal information");
    defaultWait.until(ExpectedConditions.elementToBeClickable(e));
    new WebDriverHelper(driver).javascriptClick(e);

    defaultWait.until(ExpectedConditions.titleContains("Personal information"));

    driver.findElementById("generalRequest").clear();
    driver.findElementById("generalRequest").sendKeys("TEST BOOKING DO NOT TICKET");
    String travelerTmcId = driver.findElementByName("profileName").getAttribute("value");
    System.out.println("Traveler's TMC ID: " + travelerTmcId);
    driver.findElementById("saveProfile").click();
    waitUntilMessageIsDisplayed("You have successfully saved changes to your personal information");

    //Closing GT Profile popup
    driver.close();
    // Switch to main window
    driver.switchTo().window(mainHandle);
    return travelerTmcId;
  }

  public void clickStopArrangingTravel(boolean isModern) {
    defaultWait.until(ExpectedConditions.elementToBeClickable(By.linkText("(Stop arranging travel)"))).click();
    if (isModern){
      defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By
              .cssSelector("button.search-dropdown-button.mat-raised-button.mat-button-base.mat-primary")));
    } else {
      defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("eTravelMainZero")));
    }
  }

  public void waitForGetThere() {
    //ie and edge don't always load the profile page.
    try {
      defaultLongWait.until(ExpectedConditions.titleIs("Profile"));
    } catch (Exception e) {
      System.out
          .println("Get there page failed to load ProfilePage.clickEditTravelPref with error: " +
              e.getMessage());
      throw e;
    }

  }

  //returns true if TMC Profile ID != N/A
  public boolean isTMCIDset() {
    WebElement e = defaultWait.until(ExpectedConditions.visibilityOf(driver
        .findElementByXPath("//td[text()='TMC Profile ID:']/following-sibling::td[1]")));
    return !e.getText().equalsIgnoreCase("N/A");
  }



  public String getNameElement(String element) {
    driver.findElementByLinkText("Edit Profile").click();
    defaultLongWait.until(
        ExpectedConditions.visibilityOfElementLocated(By.id("reservation" + element + "Text")));
    return driver.findElementById("reservation" + element + "Text").getAttribute("value");
  }

  public void cyclePageThroughAvailability() {
    driver.findElementByLinkText("Edit Availability").click();
    defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='Cancel']")))
        .click();
    defaultWait.until(
        ExpectedConditions.visibilityOfElementLocated(By.linkText("Edit Travel Preferences")));
  }

  public String[] getCreditCardInformation(String creditCard, int tableRow) {
    String[] creditCardValues = new String[2];
    String creditCardValue = driver
        .findElementByXPath("//html//table[7]/tbody[1]/tr[1]/td[1]/p[" + tableRow + "]").getText();
    if (!creditCardValue.contains("None")) {
      String replacedCardType = creditCardValue.replace(creditCard + ": ", "");
      creditCardValues = replacedCardType.split("    Card#: ");
    }
    return creditCardValues;
  }

  public String getTravelerName() {
    return driver.findElementByXPath("//div[@id='eTravelMainZero']/div/div/table/tbody/tr[4]/td")
        .getText();
  }
}
