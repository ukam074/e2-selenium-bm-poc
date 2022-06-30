package webdriver.pages.e2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import webdriver.pages.AbstractBasePage;
import webdriver.pages.e2.profile.ProfilePage;
import webdriver.utils.BasePageUtils;

public abstract class AbstractE2BasePage extends AbstractBasePage {

  public static final String LABEL_RED = "rgba(255, 0, 0, 1)";
  public static final String BORDER_RED = "rgb(255, 0, 0)";

  public AbstractE2BasePage(RemoteWebDriver webdriver) {
    super(webdriver);
  }

  /*---------------------------- Action Methods ----------------------------*/


  public ProfilePage clickProfile() {
    try {
      if (driver.findElements(By.id("dd")).size() >= 1) {
        driver.findElement(By.id("dd")).click();
        if (driver.findElements(By.id("ProfileNavLink")).size() >= 1) {
          defaultWait.until(
              ExpectedConditions.elementToBeClickable(driver.findElement(By.id("ProfileNavLink"))))
              .click();
        }
      }
      if (driver.findElements(By.id("ArrangerProfileNavLink")).size() >= 1) {
        defaultWait.until(ExpectedConditions
            .elementToBeClickable(driver.findElement(By.id("ArrangerProfileNavLink"))))
            .click();
      }
    } catch (ElementNotVisibleException e) {
      System.out.println("Unable to click into profile");
    }

    defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.name("e2TTravelersProfile")));
    return new ProfilePage(driver);
  }


  public ProfilePage clickTravelersProfile() {
    driver.findElement(By.xpath(".//*[@id='ArrangerProfileNav']")).click();
    return new ProfilePage(driver);
  }


  public LoginPage clickLogoutLink() {

    if ((driver.findElements(By.xpath("//*[text()='Logout']")).size() > 0) && driver
        .findElement(By.xpath("//*[text()='Logout']")).isDisplayed()) {
      driver.executeScript("arguments[0].scrollIntoView(true);",
          driver.findElement(By.xpath("//*[text()='Logout']")));
      sleep(1000);
      driver.findElement(By.xpath("//*[text()='Logout']")).click();
    } else if (driver.findElements(By.className("user-dropdown")).size() > 0) {
      driver.executeScript("arguments[0].scrollIntoView(true);",
          driver.findElement(By.className("user-dropdown")));
      driver.findElement(By.className("user-dropdown")).click();
      sleep(3000);
      waitUntilVisible(By.xpath("//*[text()='Logout']"));
      defaultWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[text()='Logout']")))
          .click();
    }

    return new LoginPage(driver);
  }

  /**
   * Collapses if already expanded
   */
  public void clickCollapseAllSectionsLink() {
    clickExpandOrCollapseAllSections(false);
  }

  /**
   * Expands if already collpased
   */
  public void clickExpandAllSectionsLink() {
    clickExpandOrCollapseAllSections(true);
  }

  /**
   * Method called by methods above. Contains timing, retries, error handling.
   *
   * @param isExpand true of false
   */
  private void clickExpandOrCollapseAllSections(boolean isExpand) {
    final String startingClass = isExpand ? "panelHeaderCollapsed" : "panelHeader";
    final String expandOrCollapse = isExpand ? "Expand" : "Collapse";
    List<WebElement> expandedOrCollapsedElements = driver.findElementsByClassName(startingClass);
    driver.findElement(By.xpath(new StringBuilder("//*[contains(@title,'").append(expandOrCollapse)
        .append(" All Sections')]").toString())).click();
    int numberOfTries = 1;
    while (expandedOrCollapsedElements.size() > 0 && numberOfTries <= 20) {
      sleep(250);
      expandedOrCollapsedElements = driver.findElementsByClassName(startingClass);
      numberOfTries++;
    }

    if (numberOfTries == 20) {
      throw new TimeoutException(
          new StringBuilder(expandedOrCollapsedElements.size())
              .append(" elements never changed state").toString());
    }
  }



  public void clickUserDropDown() {
    if (driver.findElements(By.className("user-dropdown")).size() > 0){
      driver.executeScript("arguments[0].scrollIntoView(true);",
              driver.findElement(By.className("user-dropdown")));
      driver.findElement(By.className("user-dropdown")).click();
      waitUntilVisible(By.id("MessageCenterNav"));
    }
    else if (driver.findElements(By.id("user-menu-button")).size() >0){
      driver.executeScript("arguments[0].scrollIntoView(true);",
              driver.findElement(By.id("user-menu-button")));
      driver.findElement(By.id("user-menu-button")).click();
      waitUntilVisible(By.id("user-menu-message-center-link"));
    }
  }

  /*---------------------------- Boolean Methods ----------------------------*/

  public boolean isElementPresent(By by) {
    try {
      driver.findElement(by);
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }

  public boolean isLinkPresent(String linkName) {
    return driver.findElementsByLinkText(linkName).size() != 0;
  }

  // wrapper method for testing expected condition
  public boolean isClickable(String elementID) {
    ExpectedCondition<WebElement> condition = ExpectedConditions
        .elementToBeClickable(driver.findElementById(elementID));

    return condition.apply(driver) != null;
  }

  public boolean isElementVisible(String elementID) {
    return driver.findElementsById(elementID).size() > 0;
  }

  public boolean isElementVisible(By by) {
    return driver.findElements(by).size() > 0;
  }

  /*--------------------------------- Helper Methods ---------------------------------*/

  public void clickAndClearField(WebElement fieldElement) {
    fieldElement.click();
    fieldElement.clear();
    if (StringUtils.isNotEmpty(fieldElement.getAttribute("value"))) {
      throw new java.lang.RuntimeException("Field was not cleared");
    }
  }

  public void clickClearTabOffField(WebElement fieldElement) {

    String inputValue = fieldElement.getAttribute("value");
    for (int k = 0; k < inputValue.length(); k++) {
      fieldElement.sendKeys(Keys.BACK_SPACE);
    }
    fieldElement.sendKeys(Keys.TAB);

    if (StringUtils.isNotEmpty(fieldElement.getAttribute("value"))) {
      throw new java.lang.RuntimeException("Field was not cleared");
    }
  }

  public void waitForSpinny() {
    WebDriverWait wait = new WebDriverWait(driver, 6, 250);
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("wait")));
  }

  public String tryGetText(By by) {
    int attempts = 0;
    String result = null;
    while (attempts < 2) {
      try {
        result = driver.findElement(by).getText();
        break;
      } catch (StaleElementReferenceException e) {
        System.err.print("Attempting to avoid StaleElementReferenceException.");
      }
      attempts++;
    }
    if (result == null) {
      throw new NoSuchElementException("Element not found.");
    }
    return result;
  }

  @Deprecated
  public String documentToApprove(boolean notGA) {
    String unparsedTripID;
    String location = "//td[3]";
    if (notGA) {
      location = "//td[2]/span[2]";
    }
    try {
      unparsedTripID = driver.findElementByXPath(location).getText();
    } catch (Exception GAAuth) {
      unparsedTripID = driver.findElementByXPath("//td[3]/span[2]").getText();
    }
    if ((driver.findElements(By.id("voucherIdValue"))).size() >= 1) {
      unparsedTripID = unparsedTripID
          .concat("(" + (driver.findElement(By.id("voucherIdValue")).getText()) + ")");
    }
    if ((driver.findElements(By.id("advanceIdValue"))).size() >= 1) {
      unparsedTripID = unparsedTripID
          .concat("(" + (driver.findElement(By.id("advanceIdValue")).getText()) + ")");
    }
    System.out.println(unparsedTripID);
    if (!notGA) {
      String[] parts = unparsedTripID.split(":");
      unparsedTripID = parts[1];
    }

    unparsedTripID = unparsedTripID.trim(); //remove white space at start or end of string

    return unparsedTripID;
  }

  /**
   * This method will replace the method above documentToApprove() The method will get the correct
   * document id to approve when on the summary page. When on the approvals page use
   * approvalsPage.approveDocumentById(idTrip) to approve the correct document
   *
   * @return document id to approve with all white space removed
   */
  public String getDocumentToApprove() {
    String documentId = "";

    try {
      if ((driver.findElements(By.id("tripIdValue"))).size() >= 1) {
        documentId = driver.findElement(By.id("tripIdValue")).getText();
      }
      if ((driver.findElements(By.id("groupAuthId"))).size() >= 1) {
        documentId = driver.findElement(By.id("groupAuthId")).getText();
      }
      if ((driver.findElements(By.id("voucherIdValue"))).size() >= 1) {
        documentId = documentId
            .concat("(" + (driver.findElement(By.id("voucherIdValue")).getText()) + ")");
      }
      if ((driver.findElements(By.id("advanceIdValue"))).size() >= 1) {
        documentId = documentId
            .concat("(" + (driver.findElement(By.id("advanceIdValue")).getText()) + ")");
      }
      if ((driver.findElements(By.id("openAuthId"))).size() >= 1) {
        documentId = driver.findElement(By.id("openAuthId")).getText();
      }
    } catch (NotFoundException e) {
      System.out.println("The correct document id was not found on the summary page");
    }

    return documentId.trim();
  }

  public String getDocumentNumber() {
    defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("customDocNbr")));
    return driver.findElementById("customDocNbr").getText();
  }

  public String getCustomMessageValue() {
    String message = driver.findElement(By.xpath("//div[@class='infoMessage']/ul/li/h1")).getText();
    return message;
  }

  public boolean isCustomMessageExist() {
    boolean exists;
    try {
      exists = driver.findElement(By.xpath("//div[@class='infoMessage']")).isDisplayed();
    } catch (Exception e) {
      exists = false;
    }

    return exists;
  }

  public boolean verifyHelpIconExist() {
    boolean exists;
    try {
      exists = driver.findElement(By.className("help-link")).isDisplayed();
    } catch (Exception e) {
      exists = false;
    }

    return exists;
  }

  public void verifyAllDropdownsHaveValues(String pageSectionID) {

    List<WebElement> allFormChildElements = driver
        .findElements(By.xpath("//*[@id='" + pageSectionID + "']//select"));
    for (WebElement items : allFormChildElements) {
      if (items.getTagName().equals("select")) {
        verifyDropdownIsNotEmpty(items.getAttribute("id"));
      }
    }
  }

  public void verifyDropdownIsNotEmpty(String dropboxElement) {
    WebElement dropdown = driver.findElement(By.id(dropboxElement));
    Select select = new Select(dropdown);
    List<WebElement> options = select.getOptions();
    if (options.size() == 0) {
      System.out.println("dropdown is empty");
    }
    return;
  }

  public String clickBubbleHelp(String helpPage) {
    defaultWait
        .until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@id='" + helpPage + "']/img")))
        .click();
    Set<String> windowHandles = driver.getWindowHandles();
    defaultLongWait.until(ExpectedConditions.numberOfWindowsToBe(2));
    switchToLastTab();
    sleep(2000);
    defaultLongWait.until(ExpectedConditions.titleIs("Profile: Update credit card information"));
    String helpId = driver.findElement(By.id("detail_answer_id")).getText().trim();
    driver.close();
    switchToLastTab();

    return helpId;
  }

  public String getHoverText(String elementId) {
    String hoverText = driver.findElementById(elementId).getAttribute("title");
    return hoverText;
  }

  public void selectValueFromDropDown(String dropdownId, String dropdownValue) {
    defaultWait.until(ExpectedConditions.elementToBeClickable
        (By.id(dropdownId)));
    new Select(driver.findElementById(dropdownId)).selectByValue(dropdownValue);
    sleep(500);
  }

  public void selectTextFromDropDown(String dropdownId, String dropdownText) {
    defaultWait.until(ExpectedConditions.elementToBeClickable
        (By.xpath("//select[@id='" + dropdownId + "']/option[text()='" + dropdownText + "']")));
    // To handle stale element exceptions
    BasePageUtils.staleSafeWait(driver.findElement(
        By.xpath("//select[@id='" + dropdownId + "']/option[text()='" + dropdownText + "']")));
    new Select(driver.findElementById(dropdownId)).selectByVisibleText(dropdownText);
    defaultLongWait.until(ExpectedConditions.elementSelectionStateToBe
        (By.xpath("//select[@id='" + dropdownId + "']/option[text()='" + dropdownText + "']"),
            true));
  }

  public String getDefaultValueFromDropdown(String selectDropdownId) {
    return driver.findElementById(selectDropdownId)
        .findElement(By.cssSelector("[selected='selected']"))
        .getAttribute("value");
  }

  public String getDefaultTextFromDropdown(String selectDropdownId) {
    try {
      return driver.findElementById(selectDropdownId)
          .findElement(By.cssSelector("[selected='selected']")).getText();
    } catch (Exception e) {
      List<WebElement> elements = driver
          .findElementsByXPath("//select[@id='" + selectDropdownId + "']/option");
      for (WebElement element : elements) {
        String value = element.getAttribute("value");
        if (value.equals("BLANK") || value.isEmpty()) {
          return element.getText();
        }
      }
      return "";
    }
  }

  public String selectedDropdownValueByID(String dropdownID) {
    defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(dropdownID)));
    Select defaultRCC = new Select(driver.findElementById(dropdownID));
    String selectedValue = defaultRCC.getFirstSelectedOption().getAttribute("value");
    return selectedValue;
  }

  /**
   * @param dropdownId - the field ID of the drop-down list
   * @return list of all items of the drop-down list
   */
  public Select getDropDownOptionSelect(String dropdownId) {
    return getSelectById(dropdownId);
  }

  public String getDefaultValueFromEditField(String editFieldId) {
    return driver.findElementById(editFieldId).getAttribute("value");
  }

  /**
   * @return true/false if clicked on a field and all data is auto-selected
   */
  public boolean isFieldAutoSelected(String editFieldId) {
    boolean selected = false;
    WebElement element = driver.findElementById(editFieldId);
    element.click();
    element.sendKeys(Keys.SPACE);
    String stringValue = element.getAttribute("value");
    if (" ".equals(stringValue)) {
      selected = true;
    }
    element.sendKeys(Keys.BACK_SPACE);
    return selected;
  }

  /**
   * Return true/false if field name is in Red
   */
  public boolean isLabelColorRed(String labelFieldId) {
    String labelColor = driver.findElementByXPath("//label[@for='" + labelFieldId + "']")
        .getCssValue("color");
    return labelColor.equals(LABEL_RED);
  }

  /**
   * Return true/false if field name is in Red
   */
  public boolean isExpirationLabelColorRed(String labelId) {
    String labelColor = driver.findElementById(labelId)
        .getCssValue("color");
    return labelColor.equals(LABEL_RED);
  }



  /**
   * Method to return the list of options connected to a Select(dropdown) WebElement as identified
   * by its Id.
   *
   * @return List<String> options
   */
  public List<String> getOptionsFromSelect(String selectDropdownId) {
    List<String> items = new ArrayList<>();
    Select dropdown = new Select(driver.findElementById(selectDropdownId));
    List<WebElement> options = dropdown.getOptions();
    for (WebElement el : options) {
      items.add(el.getText());
    }
    return items;
  }

  public String getLabelForElementById(String elementId) {
    return (driver.findElement(By.cssSelector("label[for='" + elementId + "']")).getText());
  }


  public void clickSearch() {
    WebElement element = driver.findElement(By.cssSelector("input[title='Search']"));
    defaultWait.until(ExpectedConditions.elementToBeClickable(element));
    element.click();
  }

  /**
   * Get user last name from the full name on user drop-down E2 main page
   *
   * @return the string last name when full name with/without middle name
   */
  public String getUserLastName() {
    String lastName = "";
    String userName = driver.findElement(By.className("user-dropdown")).getText();
    String[] splitName = userName.split(" ");
    if (splitName.length == 2) {
      lastName = splitName[1];
    } else {
      lastName = splitName[2];
    }
    return lastName;
  }


  public boolean isManageDependentLinkDisplayed(boolean isModern) {
    if(!isModern)
      return isElementPresent(By.id("ModernManageDependentsNav"));
    else
      return isElementPresent(By.id("user-menu-dependents-link"));
  }

  public boolean isManageDependentLinkClickable(boolean isModern) {
    if(!isModern)
      return isClickable("ModernManageDependentsNav");
    else
      return isClickable("user-menu-dependents-link");
  }

  public void clickManageDependentsLinkUserDropdown(){
    waitUntilClickableAndClick(By.id("user-menu-dependents-link"));
  }

  public boolean isManageDependentsPageDispalyed() {
    waitUntilVisible(By.xpath("//ui-title-text/h2"));
    return driver.findElementByXPath("//ui-title-text/h2").getText().equals("Manage Dependents");
  }

  public boolean isPageNotFoundDisplayed() {
    return driver.findElementByXPath("//e2-main//e2-not-found//h2").getText()
        .contains("Page not found!");
  }


  public void click50or25Link() {
    try {
      driver.findElementByLinkText("50").click();
      sleep(2000);
    } catch (NoSuchElementException ne50) {
      try {
        driver.findElementByLinkText("25").click();
        sleep(2000);
      } catch (NoSuchElementException ne25) {
      }
    }
  }
}
