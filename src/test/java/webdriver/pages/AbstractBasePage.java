package webdriver.pages;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import webdriver.utils.BasePageUtils;
import webdriver.utils.DateFormatUtils;
import webdriver.utils.WebDriverHelper;

public abstract class AbstractBasePage {

  public final static int NUM_SECONDS_TO_WAIT;
  public static LocalDateTime day;
  public static DateTimeFormatter formatter;
  public static ZoneId zoneId = ZoneId.of("America/Chicago");

  static {
    String waitTimeProperty = System.getProperty("WAIT_TIME");
    if (null == waitTimeProperty) {
      NUM_SECONDS_TO_WAIT = 2;
    } else {
      int parsedTime = Integer.parseInt(waitTimeProperty);
      if (0 <= parsedTime) {
        throw new RuntimeException("WAIT_TIME must be greater than " + "zero");
      }

      NUM_SECONDS_TO_WAIT = parsedTime;
    }
  }

  protected WebDriverWait defaultShortWait;
  protected WebDriverWait defaultWait;
  //   protected WebDriverWait defaultSleepWait;
  //   protected WebDriverWait defaultSleepLongWait;
  //   See note in constructor
  protected WebDriverWait defaultLongWait;
  //   protected WebDriverWait getThereLongWait used for longer waits in GetThere;
  protected WebDriverWait getThereLongWait;
  protected RemoteWebDriver driver;

  /**
   * Constructor.
   *
   * @param webdriver Selenium class.
   */
  public AbstractBasePage(RemoteWebDriver webdriver) {
    driver = webdriver;

    driver.manage().timeouts().implicitlyWait(NUM_SECONDS_TO_WAIT, TimeUnit.SECONDS);

    defaultShortWait = new WebDriverWait(driver, 3);
    defaultWait = new WebDriverWait(driver, 30);
    defaultLongWait = new WebDriverWait(driver, 120);
    getThereLongWait = new WebDriverWait(driver, 160);
    //   defaultSleepWait = new WebDriverWait(driver, 10, 500);
    //   defaultSleepLongWait = new WebDriverWait(driver, 60, 500);

    //   Now get rid of every place in the code base where a child class of AbstractBasePage
    //   is creating a new three-parameter instance of Wait with common values
  }

  /*---------------------------- Get Methods ----------------------------*/


  public static String getRandomString(int length) {
    return RandomStringUtils
        .random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
  }

  /**
   * @param valueToBeAppendedTo -
   */
  public static String leftAppendString(String valueToBeAppendedTo, String valueToAppend,
      int maxSize) {
    String myNumber = StringUtils
        .right(StringUtils.leftPad(valueToBeAppendedTo, maxSize, valueToAppend), maxSize);
    return myNumber;
  }


  //converts a string value to a float, adds an integer to it and converts back to a string with 0.00 formatting.
  public static String addFloatToString(String stringValue, float valueToAdd) {
    Float newAmountF = Float.parseFloat(stringValue) + valueToAdd;
    DecimalFormat df = new DecimalFormat("0.00");
    df.setMaximumFractionDigits(2);
    return df.format(newAmountF);
  }

  /**
   * Converts a date from Local Time to GMT Time takes a date in the format of:  24-Mar-2020 [6:59
   * AM CDT]
   */
  public static String localToUTC(String dateStr) {
    Date date = new Date(dateStr);
    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a z");
    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    return dateFormat.format(date);
  }

  public static String localToCST(String dateStr, String pattern) {
    Date date = new Date(dateStr);
    DateFormat dateFormat = new SimpleDateFormat(pattern);
    dateFormat.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
    return dateFormat.format(date);
  }

  /**
   * slh-added method to get date format to match date format in alerts, used in the overlapping
   * trips tests. Tests require exact message asserts.
   */
  public static String getDate_dd_MMM_yy(int offset) {
    day = LocalDateTime.now(zoneId).plusDays(offset);
    formatter = DateTimeFormatter.ofPattern("dd-MMM-yy");
    return formatter.format(day).toUpperCase();

  }

  /**
   * Retrieve the formatted date, in pattern of 01-Jan-2018 , needed method to support Mmm in
   * addition to MMM (eg 01-JAN-2018
   *
   * @param offset The number of days before or after today/
   */
  public static String getDate_dd_Mmm_yyyy(int offset) {
    day = LocalDateTime.now(zoneId).plusDays(offset);
    formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    return formatter.format(day);
  }

  // Returns system date of the system it is run on (I.e Zalenium GMT time)
  public static String getDate_ddMMMyyyy_system(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, offset);
    Date day = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    return dateFormat.format(day);
  }

  // returns date and time in the format: 01-Jan-2019-12-34-22
  public static String getDate_dd_Mmm_yyyy_HH_mm_ss(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, offset);
    Date day = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy-HH-mm-ss");
    return dateFormat.format(day);
  }

  // returns the local date and time in the format: 01-Jan-2019-12-34-22
  public static String getLocalDate_dd_Mmm_yyyy_HH_mm_ss(int offset) {
    day = LocalDateTime.now(zoneId).plusDays(offset);
    formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy-HH-mm-ss");
    return formatter.format(day);
  }

  /**
   * @return Date and Time format that is used in Trip History Example:  23Jul19 Tue 03:29PM
   */
  public static String getCurrentDateAndtimeForHistory() {
    Calendar cal = Calendar.getInstance();
    Date day = cal.getTime();
    DateFormat historyFormat = new SimpleDateFormat("ddMMMyy EE hh:mma");
    return historyFormat.format(day);
  }

  /**
   * @return Date and Time stamp format is displays on Remark Example:  19-Jul-2019 6:23 PM CDT
   */
  public static String getCurrentDateAndtimeForRemark() {
    Calendar cal = Calendar.getInstance();
    Date day = cal.getTime();
    DateFormat remarkFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a z");
    return remarkFormat.format(day);
  }

  /**
   * @return Date and Time stamp format displays on Printable when it's generated
   * @Example: Wed, 18 Nov 2020 22:21 - as it is always CST time
   */
  public static String getGeneratedPrintableDateTime() {
    day = LocalDateTime.now(zoneId);
    formatter = DateTimeFormatter.ofPattern("EE',' dd MMM yyyy HH:mm");
    return formatter.format(day);
  }

  public static String getDate_yyyymmdd(int offset) {
    day = LocalDateTime.now(zoneId).plusDays(offset);
    formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return formatter.format(day);
  }

  // Returns system date of the system it is run on (I.e Zalenium GMT time)
  public static String getDate_yyyymmdd_system(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, offset);
    Date day = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.format(day);
  }

  public static String getDate_mmddyyyy(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, offset);
    Date day = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    return dateFormat.format(day);
  }

  /**
   * Returns date in format of November 05, 2018. The date returned is defined by the number of days
   * before or after today.
   *
   * @param offset Number of days before or after today.
   */
  public static String getDateFull(int offset) {
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    cal.add(Calendar.DAY_OF_YEAR, offset);
    Date day = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
    return dateFormat.format(day);
  }

  /**
   * For a given date, adds/subtracts days, and returns date
   *
   * @param givenDate     date to add or subtract days from (example: 2018-12-01, 12/01/2018)
   * @param datePattern   format MATCHING givenDate (example: yyyy-MM-dd, MM/dd/yyyy)
   * @param offset        Number of days before or after givenDate (-10, 10)
   * @param returnPattern format to return the new date
   */
  public static String addSubtractDaysToDate(String givenDate, String datePattern, int offset,
      String returnPattern) {
    //Specifying date format that matches the given date
    SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
    Calendar c = Calendar.getInstance();
    try {
      //Setting the date to the given date
      c.setTime(sdf.parse(givenDate));
    } catch (Exception e) {
      e.printStackTrace();
    }

    //Number of Days to add or subtract
    c.add(Calendar.DAY_OF_YEAR, offset);
    //reformat the newDate into the provided format
    sdf = new SimpleDateFormat(returnPattern);
    String newDate = sdf.format(c.getTime());
    return newDate;
  }

  /**
   * Returns date in format dd-MMM-yyyy. The date returned is defined by the number of days before
   * or after today.
   *
   * @param offset Number of days before or after today.
   */
  public String getDate_ddMMMyyyy(int offset) {
    return DateFormatUtils.getDate_dd_MMM_yyyy(offset);
  }

  public String getDate_ddMMMyy(int offset) {
    day = LocalDateTime.now(zoneId).plusDays(offset);
    formatter = DateTimeFormatter.ofPattern("ddMMMyy");
    return formatter.format(day);
  }

  /**
   * Return a randomly generated phone number.
   */
  public String GeneratePhoneNumber() {
    int num1, num2, num3;
    int set2, set3;
    Random generator = new Random();
    num1 = generator.nextInt(7) + 1;
    num2 = generator.nextInt(8);
    num3 = generator.nextInt(8);

    set2 = generator.nextInt(643) + 100;
    set3 = generator.nextInt(8999) + 1000;
    String phoneNum = num1 + "" + num2 + "" + num3 + "-" + set2 + "-" + set3;
    return phoneNum;
  }

  /**
   * Returns date in format MM/dd/yy. The date returned is defined by the number of days before or
   * after today.
   *
   * @param offset Number of days before or after today.
   */
  public String getDate_mmddyy(int offset) {
    day = LocalDateTime.now(zoneId).plusDays(offset);
    formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
    return formatter.format(day);
  }

  /**
   * Returns time in format hh:mm a. The time returned is defined by the number of time for days
   * before or after today. Example: 06:12 AM
   *
   * @param offset Number of days before or after today.
   */
  public String getTime_hhmma(int offset) {
    Calendar cal = Calendar.getInstance();
    Date day = cal.getTime();
    DateFormat timeFormat = new SimpleDateFormat("hh:mm a");
    return timeFormat.format(day);
  }

  //Used for MIS Staging
  public String getDateTime_YYYYmmdd_HHMM_roundedToNextHalfHour() {
    Calendar cal = Calendar.getInstance();
    cal.getTime();
    int minute = cal.get(Calendar.MINUTE);
    int minutesToAdd = 0;
    if (minute == 0) {
      minutesToAdd = 30;
    } else if (minute < 30) {
      minutesToAdd = (30 - minute);
    } else if (minute == 30) {
      minutesToAdd = 30;
    } else if (minute < 60) {
      minutesToAdd = (60 - 30 - minute);
    }
    cal.add(Calendar.MINUTE, minutesToAdd);
    int newMinute = cal.get(Calendar.MINUTE);
    if (newMinute == 59 || newMinute == 29) {
      cal.add(Calendar.MINUTE, 1);
    } else if (newMinute == 1 || newMinute == 31) {
      cal.add(Calendar.MINUTE, 29);
    }
    Date dayTime = cal.getTime();
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
    return dateFormat.format(dayTime);
  }

  /**
   * @param dateToFormat string date in format: dd-Mmm-yyyy (ie. 16-Mar-2021)
   * @return string date in format: yyyy-mm-dd (ie. 2021-03-16)
   * @throws ParseException
   */
  public String formatDateTime(String dateToFormat, String fromPattern, String toPattern) {
    String date = "";
    SimpleDateFormat iformatter = new SimpleDateFormat(fromPattern);
    SimpleDateFormat oformatter = new SimpleDateFormat(toPattern);
    try {
      date = oformatter.format(iformatter.parse(dateToFormat));
    } catch (Exception e) {
      System.out.println("Cannot format: " + dateToFormat + " to pattern: " + toPattern);
    }
    return date;
  }

  /**
   * Method to check if the dateToValiate is within the date range from fromDateToValidate to
   * toDateToValidate; also check if the dateToValiate is included on last date (matchLastDate) in
   * the range.
   *
   * @param dateToValiate      (ie. "01-May-2021")
   * @param fromDateToValidate (ie. "31-May-2021")
   * @param toDateToValidate   (ie. "04-Jun-2021")
   * @param dateFormat         (ie. "dd-MMM-yyyy")
   * @param matchLastDate      (i.e false if not included "04-Jun-2021" in the date range)
   * @return true/false or ParseException if the provided dateFormat is invalid.
   */
  public boolean isThisDateWithinDateRange(String dateToValiate, String fromDateToValidate,
      String toDateToValidate, String dateFormat, boolean matchLastDate) {

    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    sdf.setLenient(false);
    try {
      // if not valid, it will throw ParseException
      Date fromDate = sdf.parse(fromDateToValidate);
      Date toDate = sdf.parse(toDateToValidate);
      Date actualDate = sdf.parse(dateToValiate);
      // Check if the dateToValidate is in the range of fromDate - toDate
      if (!matchLastDate) {
        if ((actualDate.compareTo(fromDate) >= 0) && (actualDate.compareTo(toDate) < 0)) {
          return true;
        } else {
          return false;
        }
      } else {
        if ((actualDate.compareTo(fromDate) >= 0) && (actualDate.compareTo(toDate) <= 0)) {
          return true;
        } else {
          return false;
        }
      }
    } catch (ParseException e) {
      System.out.println("Receive ParseException because the dateFormat is not correct.");
      return false;
    }
  }

  /**
   * Returns the number of days between two date ranges
   *
   * @param formatter of dates passed in    (ie. "dd-MMM-yyyy")
   * @param dateA     (ie. "31-May-2021")
   * @param dateB     (ie. "04-Jun-2021")
   * @return long of days between the date ranges
   */
  public static long getDaysBetweenTwoDates(String formatter, String dateA, String dateB) {
    long days = 0;
    SimpleDateFormat myFormat = new SimpleDateFormat(formatter);
    String inputString1 = dateA;
    String inputString2 = dateB;

    try {
      Date date1 = myFormat.parse(inputString1);
      Date date2 = myFormat.parse(inputString2);
      long diff = date2.getTime() - date1.getTime();
      days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return days;
  }

  /**
   * Returns the abbreviated Month name for month integer
   *
   * @param monthNum  (ie. 9)
   * @return returns Sep
   */
  public static String getMonthNameAbbrFromInt(int monthNum) {
    String monthString;
    switch (monthNum) {
      case 1:
        monthString = "Jan";
        break;
      case 2:
        monthString = "Feb";
        break;
      case 3:
        monthString = "Mar";
        break;
      case 4:
        monthString = "Apr";
        break;
      case 5:
        monthString = "May";
        break;
      case 6:
        monthString = "Jun";
        break;
      case 7:
        monthString = "Jul";
        break;
      case 8:
        monthString = "Aug";
        break;
      case 9:
        monthString = "Sep";
        break;
      case 10:
        monthString = "Oct";
        break;
      case 11:
        monthString = "Nov";
        break;
      case 12:
        monthString = "Dec";
        break;
      default:
        monthString = "Invalid month";
        break;
    }
    return monthString;
  }

  public void sleep(int num) {
    try {
      Thread.sleep(num);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  protected void handleAlert() {
    sleep(3000);
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until(ExpectedConditions.alertIsPresent());
    Alert alert = driver.switchTo().alert();
    alert.accept();
    sleep(2000);
  }

  protected void dismissAlert() {
    sleep(3000);
    WebDriverWait wait = new WebDriverWait(driver, 60);
    wait.until(ExpectedConditions.alertIsPresent());
    Alert alert = driver.switchTo().alert();
    alert.dismiss();
    sleep(2000);
  }

  public void waitForElement(WebElement e) {
    WebDriverWait wait = new WebDriverWait(driver, 600);
    wait.until(ExpectedConditions.visibilityOf(e));
  }

  public void switchToLastTab() {
    Set<String> windowHandles = driver.getWindowHandles();
    String lastTab = null;
    for (Iterator iter = windowHandles.iterator(); iter.hasNext(); ) {
      lastTab = (String) iter.next();
    }
    driver.switchTo().window(lastTab);
  }

  /**
   * @param tabIndexFromZero index of browser tab to switch to (usually the second tab so value of
   *                         1)
   */
  public void switchActiveBrowserTab(int tabIndexFromZero) {
    String handle = driver.getWindowHandles().toArray(new String[]{})[tabIndexFromZero];
    driver.switchTo().window(handle);
  }

  public WebElement getTableRowByIndex(String tableBodyId, int index) {
    WebElement tableBody = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.id(tableBodyId)));
    WebElement tableRow = tableBody.findElement(By.xpath("./tr[" + index + "]"));
    return tableRow;
  }

  public WebElement getTableRowByClass(String tableBodyId, String nameOfRow) {
    WebElement tableBody = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains"
            + "(@id, 'levelOneModal_form:lodgingExpenseMainTable:') and contains(@id,'tb')]")));
    WebElement tableRow = tableBody.findElement(By.className(nameOfRow));
    return tableRow;
  }

  public WebElement getTableRowByXPath(String xpath) {
    WebElement tableBody = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
    WebElement tableRow = tableBody.findElement(By.xpath(xpath));
    return tableRow;
  }

  public List<WebElement> getAllTableRows(String tableId) {
    WebElement table = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.id(tableId)));
    List<WebElement> tableRows = table.findElements(By.xpath(".//tr"));
    return tableRows;
  }

  public List<WebElement> getAllTableRowsByXpath(String xPath) {
    WebElement table = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
    List<WebElement> tableRows = table.findElements(By.xpath(xPath));
    return tableRows;
  }

  public List<WebElement> getColumnFromTable(String tableId, int columnNumber) {
    String columnXpath = String.format(".//tr/td[%s]", columnNumber);
    WebElement table = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.id(tableId)));
    return (table.findElements(By.xpath(columnXpath)));
  }

  public List<WebElement> getTableElementsByClass(String tableId, String htmlClass) {
    WebElement table = defaultWait
        .until(ExpectedConditions.visibilityOfElementLocated(By.id(tableId)));
    List<WebElement> elements = table.findElements(By.className(htmlClass));
    return elements;
  }

  public void selectDropdownOptionById(String elementId, String itemText) {
    WebElement webElement = defaultWait.until(ExpectedConditions
        .visibilityOfElementLocated(By.id(elementId)));
    new Select(webElement)
        .selectByVisibleText(itemText);
  }

  public void selectDropdownOptionByIdWithRefresh(String selectId, String idOfElementToBecomeStale,
      String itemText) {
    String xpathToOption = "//select[@id='" + selectId + "']/option[text()='" + itemText + "']";
    List<WebElement> itemToSelect = driver.findElements(By.xpath(xpathToOption));
    if (itemToSelect.size() > 0) {
      WebElement option = driver.findElement(By.xpath(xpathToOption));
      //see if already selected, if so no need to research, just return.
      if (isItemAlreadySelected(xpathToOption)) {
        return;
      }
      WebElement toBeStale = defaultWait
          .until(ExpectedConditions.visibilityOfElementLocated(By.id(idOfElementToBecomeStale)));
      defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpathToOption)));
      new Select(defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(selectId))))
          .selectByVisibleText(itemText);
      BasePageUtils.staleSafeWait(toBeStale);
    }
  }

  public boolean isItemAlreadySelected(String xpathToOption) {
    WebElement option = driver.findElement(By.xpath(xpathToOption));
    //see if already selected, if so return.
    try {
      option.getAttribute("selected").equalsIgnoreCase("selected");
      System.out.println("option already selected, return");
      return true;
    } catch (Exception e) {
      return false;
    }

  }

  public void setDropdownOptionByValue(String dropdownElementID, String optionValue) {
    new Select(defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(dropdownElementID)))))
        .selectByValue(optionValue);
    sleep(500);
  }

  //NOTE: Not for use in tables, use the table row methods instead for those cases
  public Boolean getCheckboxValueByPartialId(String checkBoxId) {
    return driver.findElement(By.id(checkBoxId)).isSelected();
  }


  public String getSelectedValueFromTableRowInContainerElementByPartialId(WebElement tableRow,
      String tableCellId) {
    WebElement containerElement = defaultWait.until(ExpectedConditions
        .visibilityOf(tableRow.findElement(By.xpath(".//*[contains(@id,'" + tableCellId + "')]"))));
    Select select = new Select(containerElement.findElement(By.tagName("select")));
    return select.getFirstSelectedOption().getText();
  }

  public List<String> getAllSelectValueChoicesFromTableRowInContainerElementByPartialId(
      WebElement tableRow, String tableCellId) {
    WebElement containerElement = defaultWait.until(ExpectedConditions
        .visibilityOf(tableRow.findElement(By.xpath(".//*[contains(@id,'" + tableCellId + "')]"))));
    Select select = new Select(containerElement.findElement(By.tagName("select")));
    List<WebElement> selectChoices = select.getOptions();
    int i = 0;
    List<String> selectChoicesStrings = new ArrayList<>();
    while (i < selectChoices.size()) {
      selectChoicesStrings.add(selectChoices.get(i).getText());
      i++;
    }
    return selectChoicesStrings;
  }

  public boolean getCheckboxValueFromTableRowInContainerElementByPartialId(WebElement tableRow,
      String tableCellId) {
    WebElement containerElement = defaultLongWait.until(ExpectedConditions
        .visibilityOf(tableRow.findElement(By.xpath(".//*[contains(@id,'" + tableCellId + "')]"))));
    return containerElement.findElement(By.tagName("input")).isSelected();
  }

  public WebElement getInputElementFromTableRowInContainerElementByPartialId(WebElement tableRow,
      String tableCellId) {
    WebElement containerElement = defaultLongWait.until(ExpectedConditions
        .visibilityOf(tableRow.findElement(By.xpath(".//*[contains(@id,'" + tableCellId + "')]"))));
    return containerElement.findElement(By.tagName("input"));
  }

  public void updateTextFieldById(String elementId, String value) {
    WebElement input = defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(elementId))));
    input.clear();
    input.sendKeys(value, Keys.TAB);
    sleep(500);
  }

  public String getElementTextFromTableRowByPartialId(WebElement tableRow, String tableCellId) {
    WebElement element = defaultWait.until(ExpectedConditions
        .visibilityOf(tableRow.findElement(By.xpath(".//*[contains(@id,'" + tableCellId + "')]"))));
    String elementText = element.getText();
    return elementText;
  }

  public String getElementTextFromTableRowByPartialId(WebElement tableRow, String tableCellId,
      int row) {
    WebElement element = defaultWait.until(ExpectedConditions
        .visibilityOf(tableRow
            .findElement(By.xpath(".//*[contains(@id,':" + row + ":" + tableCellId + "')]"))));
    String elementText = element.getText();
    return elementText;
  }

  public String getInputValueFromTableRowByColumnIndex(WebElement tableRow, int columnIndex) {
    String inputValue = "";
    try {
      WebElement input = defaultWait.until(ExpectedConditions
          .visibilityOf(tableRow.findElement(By.xpath(".//td[" + columnIndex + "]/input"))));
      inputValue = input.getAttribute("value");
    } catch (Exception e) {
      inputValue = "";
    }
    return inputValue;
  }

  public String getFirstInputValueDescendentFromTableRowByColumnIndex(WebElement tableRow,
      int columnIndex) {
    String inputValue = "";
    try {
      WebElement input = defaultWait.until(ExpectedConditions
          .visibilityOf(tableRow.findElement(By.xpath(".//td[" + columnIndex + "]//input[1]"))));
      inputValue = input.getAttribute("value");
    } catch (Exception e) {
      inputValue = "";
    }
    return inputValue;
  }


  public String getElementbyXPath(String xPath) {
    WebElement element = defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xPath))));
    String elementText = element.getText();
    return elementText;
  }

  public String getElementTextById(String elementId) {
    WebElement element = defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(elementId))));
    String elementText = element.getText();
    return elementText;
  }

  public String getElementTextFromTableByRowIndexColumnIndex(String tableId, int rowIndex,
      int columnIndex) {
    WebElement tableBody = defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(tableId))));
    WebElement tableCell = tableBody
        .findElement(By.xpath(".//tr[" + rowIndex + "]/td[" + columnIndex + "]"));
    String elementText = tableCell.getText();
    return elementText;
  }

  public WebElement getWebElementFromTableByRowIndexColumnIndex(String tableId, int rowIndex,
      int columnIndex, String tagName) {
    WebElement tableBody = defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(tableId))));
    WebElement tableElement = tableBody
        .findElement(By.xpath(".//tr[" + rowIndex + "]/td[" + columnIndex + "]/" + tagName));
    return tableElement;
  }

  public String getElementTextFromTableFooterByColumnIndex(String tableId, int columnIndex,
      String enclosureXpath) {
    sleep(1000);
    WebElement tableBody = defaultLongWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(tableId))));
    WebElement tableFooter = tableBody.findElement(By.tagName("tfoot"));
    WebElement element = tableFooter
        .findElement(By.xpath(".//tr/td[" + columnIndex + "]" + enclosureXpath));
    String elementText = element.getText();
    return elementText;
  }

  public String getImageFileNameFromDataTable(String imageElementId) {
    String fileName = "";
    String[] srcParts = driver
        .findElementByXPath("//img[contains(@id,'" + imageElementId + "')]")
        .getAttribute("src").split("/");
    if (srcParts.length > 0) {
      fileName = srcParts[srcParts.length - 1];
    }
    return fileName;
  }

  public void clickLinkById(String id) {
    WebElement link = defaultWait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
    link.click();
  }

  public void clickLinkContainingText(String text) {
    defaultWait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@text,'" + text + "')]")))
        .click();
  }

  public void clickLinkContainingTitle(String text) {
    defaultWait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@title,'" + text + "')]")))
        .click();
  }

  public void clickLinkInContainerElement(String containerId) {
    WebElement containerElement = defaultWait
        .until(ExpectedConditions.visibilityOf(driver.findElement(By.id(containerId))));
    WebElement link = containerElement.findElement(By.tagName("a"));
    link.click();
  }




  /**
   * Check to is if the message displayed after delete/disable a temple on the User Profile/Edit
   * Approval Routing
   *
   * @return True/False
   */
  public boolean isUpdateProfileRoutingMessageDisplayed(String expectedMsg) {
    boolean result = false;
    List<WebElement> displayedMsg = driver.findElements(By.xpath(
        "//td[contains(@class,'error')]"));
    for (WebElement row : displayedMsg) {
      if (row.getText().contains(expectedMsg)) {
        result = true;
        break;
      }
    }
    return result;
  }

  /**
   * Get Error Banner Messages.
   */
  public String getErrorBannerMessages() {
    String messages = "";
    if (driver.findElementsById("errorsbox").size() > 0) {
      List<WebElement> errorMessages = driver
          .findElementsByXPath("//div[@id='errorsbox']//div[@class='messages__header']");
      for (WebElement message : errorMessages) {
        messages += message.getText() + ". \n";
      }
      return messages;
    } else if ((driver.findElements(By.id("profileSettingsForm:messageBox")).size() > 0) && (
        driver.findElements(By.className("errorMessage")).size() > 0)) {
      List<WebElement> errorMessages = driver.findElementsByXPath(
          "//div[@id='profileSettingsForm:messageBox']//ul[@class='messageBox-messages']");
      for (WebElement message : errorMessages) {
        messages += message.getText() + ". \n";
      }
      return messages;
    } else if ((driver.findElements(By.id("editNonFederalSponsorGroup:messageBox")).size() > 0) && (
        driver.findElements(By.className("errorMessage")).size() > 0)) {
      List<WebElement> errorMessages = driver.findElementsByXPath(
          "//div[@id='editNonFederalSponsorGroup:messageBox']//ul[@class='messageBox-messages']");
      for (WebElement message : errorMessages) {
        messages += message.getText() + ". \n";
      }
      return messages;
    } else if ((driver.findElements(By.id("messageBox")).size() > 0) && (
        driver.findElements(By.className("errorMessage")).size() > 0)) {
      List<WebElement> errorMessages = driver.findElementsByXPath(
          "//div[@id='messageBox']//ul[@class='messageBox-messages']");
      for (WebElement message : errorMessages) {
        messages += message.getText() + ". \n";
      }
      return messages;
    }
    return null;
  }

  public boolean isWebElementVisible(String elementId) {
    boolean isVisible;
    try {
      driver.findElementById(elementId);
      isVisible = true;
    } catch (Exception e) {
      isVisible = false;
    }
    return isVisible;
  }

  public boolean isWebElementEnabled(String elementId) {
    boolean isEnabled;
    try {
      isEnabled = driver.findElementById(elementId).isEnabled();
    } catch (Exception e) {
      isEnabled = false;
    }
    return isEnabled;
  }

  public void pageUp() {
    JavascriptExecutor js = driver;
    try {
      js.executeScript("window.scrollTo(document.body.scrollHeight,0)");
    } catch (Exception e) {
    }

    return;
  }

  /**
   * Function to scroll page down.
   */
  public void pageDown() {
    JavascriptExecutor js = driver;
    js.executeScript("window.scrollTo(0,document.body.scrollHeight)");
    return;
  }

  public Select getSelectById(String id) {
    return new Select(defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(id))));

  }

  /**
   * Method creates a wait until user defined string appears on a page.
   *
   * @param message The string.
   */
  public void waitUntilMessageIsDisplayed(String message) {
    try {
      WebDriverWait wait = new WebDriverWait(driver, 30);
      By element = By.xpath("//*[contains(text(),'" + message + "')]");
      wait.until(ExpectedConditions.presenceOfElementLocated(element));
    } catch (Exception e) {
      System.out.println("abstractBasePage.hasPageLoadedByWords() has failed to find" + message);
    }
  }

  /**
   * This is to get a future year - mostly for selecting CC expiration year.
   *
   * @return 4 digit of year string (ex:  2028)
   */
  public static String getYearInFuture(int yearsFromNow) {
    return String.valueOf(LocalDate.now().plusYears(yearsFromNow).getYear());
  }

  /**
   * Retrieve the capitalized name of the current month.
   *
   * @return current Month in Camel case (ex:  May)
   */
  public String getCurrentMonth() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
    return formatter.format(LocalDate.now());
  }

  /**
   * Get the current Month in number format.
   *
   * @return current Month in number format (ex:  5)
   */
  public static String getCurrentMonthValue() {
    return String.valueOf(LocalDate.now().getMonthValue());
  }

  public static String getFiscalYear(int offset) {
    int fiscalYear = 0;
    LocalDate date = LocalDate.now().plusDays(offset);
    fiscalYear = date.getYear();
    int currentMonth = date.getMonthValue();
    if (currentMonth > 9) {
      fiscalYear = fiscalYear + 1;
    }
    return (String.valueOf(fiscalYear));
  }


  public WebElement shortWaitUntilVisible(String elementId) {
    return defaultShortWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
  }

  public WebElement shortWaitUntilVisible(By by) {
    return defaultShortWait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public WebElement waitUntilVisible(String elementId) {
    return defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
  }

  public WebElement waitUntilVisible(By by) {
    return defaultWait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public boolean waitUntilInvisible(By by) {
    return defaultWait.until(ExpectedConditions.invisibilityOfElementLocated(by));
  }

  public WebElement longWaitUntilVisible(By by) {
    return defaultLongWait.until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public WebElement longWaitUntilVisible(String elementId) {
    return defaultLongWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
  }

  public WebElement waitUntilClickable(By by) {
    (driver).executeScript("arguments[0].scrollIntoView(true);",
        driver.findElement(by));
    waitUntilVisible(by);
    return defaultWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(by)));
  }

  public void waitUntilClickableAndClick(String elementId) {
    waitUntilClickableAndClick(By.id(elementId));
  }

  public void longWaitUntilClickableAndClick(String elementId) {
    longWaitUntilClickableAndClick(By.id(elementId));
  }

  public void waitUntilClickableAndClick(By by) {
    (driver).executeScript("arguments[0].scrollIntoView(true);",
        driver.findElement(by));
    waitUntilVisible(by);
    try {
      defaultWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(by))).click();
    } catch (Exception e) {
      WebDriverHelper webDriverHelper = new WebDriverHelper(driver);
      webDriverHelper.javascriptClick(driver.findElement(by));
    }
  }

  public void longWaitUntilClickableAndClick(By by) {
    defaultLongWait.until(ExpectedConditions.elementToBeClickable(driver.findElement(by))).click();
  }

  /*---------------------------- Verify Methods ----------------------------*/

  /**
   * Compares a list of web elements to an List of Strings representing the text expected to be in
   * the list of web elements. If the lists differ in size, the method will return an
   * IllegalArgumentException. If a web element's text couldn't find its corresponding verification,
   * the method will print the expected values to the server output.
   *
   * @param expectedTextValues A List of Strings representing the expected values
   * @param headerElements     A List of Web Elements to inspect
   * @return The number of errors (no matched result) encountered during the verification
   */
  public int verifyTextInListOfWebElements(List<String> expectedTextValues,
      List<WebElement> headerElements) throws IllegalArgumentException {
    if (expectedTextValues.size() != headerElements.size()) {
      throw new IllegalArgumentException("List sizes do not match");
    }
    return headerElements.stream().filter(item1 -> {
      String searchTxt = item1.getText();
      boolean findMatch = expectedTextValues.stream().anyMatch(searchTxt::equalsIgnoreCase);
      if (!findMatch) {
        System.out.println(String.format("Expected to see: '%s', but not found.", searchTxt));
      }
      return !findMatch;
    }).collect(Collectors.toList()).size();
  }

  /**
   * Determines if a user defined string is in a list of WebELements.
   *
   * @param expected    The string expected.
   * @param webElements The list of WebElements that should contain the expected string.
   */
  public boolean isContainedWithinWebElementList(String expected, List<WebElement> webElements) {
    boolean result = false;
    String getElementText = "";
    for (WebElement webElement : webElements) {
      getElementText = webElement.getText();
      if (getElementText.contains(expected)) {
        result = true;
        break;
      }
    }
    if (result == false) {
      System.out
          .println("Text: '" + getElementText + "' does not contain expected text: " + expected);
    }
    return result;
  }

  public void clickClearAndInputText(String elementId, String inputText) {
    clickClearAndInputText(By.id(elementId), inputText);
  }

  /**
   * Click clear and enter defined text.
   *
   * @param by        The element to be manipulated by this method.
   * @param inputText The text to input into the field.
   */
  public void clickClearAndInputText(By by, String inputText) {
    try {
      waitUntilVisible(by);
      waitUntilClickableAndClick(by);
    } catch (WebDriverException e) {
      if (e.getMessage().contains("Element is not clickable at point")) {
        throw new RuntimeException("Original error: " + e.getMessage()
            + "\nLikely caused by element not present on screen from a moved modal or equivalent "
            + "circumstance.");
      }
      throw e;
    }

    WebElement inputElement = driver.findElement(by);
    inputElement.clear();
    if (StringUtils.isNotEmpty(inputElement.getAttribute("value"))) {
      throw new java.lang.RuntimeException("Field was not cleared");
    }
    inputElement.sendKeys(inputText);
    inputElement.sendKeys(Keys.TAB);
    String value = driver.findElement(by).getAttribute("value");
    if (!value.equals(inputText)) {
      throw new java.lang.RuntimeException(
          "Set value (" + value + ") entered does not match expected input text. (" + inputText
              + ")");
    }
  }

  /**
   * Given a list of WebElements, determine if they are ordered by date.
   *
   * @param webElements List of web elements to analyze.
   * @param isAscending If true, check sort by ascending date.
   */
  public boolean isWebElementListOrderedByDate(List<WebElement> webElements, boolean isAscending) {
    boolean result = true;

    Date previousRecordDate = null;
    for (WebElement webElement : webElements) {
      try {
        Date currentRecordDate = new SimpleDateFormat("ddMMMyy EEE h:mma")
            .parse(webElement.getText().split("-")[0].trim());
        if (previousRecordDate != null && isProperlyOrdered(isAscending, currentRecordDate,
            previousRecordDate)) {
          result = false;
          break;
        } else {
          previousRecordDate = currentRecordDate;
        }
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  private boolean isProperlyOrdered(boolean isAscending, Date currentRecordDate,
      Date previousRecordDate) {
    return (isAscending ? currentRecordDate.before(previousRecordDate)
        : currentRecordDate.after(previousRecordDate));
  }

  /**
   * Retrieve the alert text from browser popup.
   */
  public String getBrowserPopupWindowText() {
    try {
      defaultWait.until(ExpectedConditions.alertIsPresent());
      return driver.switchTo().alert().getText();
    } catch (Throwable e) {
      throw new RuntimeException("Error came while waiting for the alert popup. " + e.getMessage());
    }
  }

  /**
   * Close the browser popup window.
   */
  public void closeBrowserPopupWindow() {
    try {
      defaultWait.until(ExpectedConditions.alertIsPresent());
      //Accepting alert.
      driver.switchTo().alert().accept();
      System.out.println("Accepted the alert successfully.");
    } catch (Throwable e) {
      throw new RuntimeException("Error came while waiting for the alert popup. " + e.getMessage());
    }
  }

  public void clickNumberOfItemsPerPageLink(Integer numberOfItemsToShow) {
    String id = String.format("show%sItemsLinkId", numberOfItemsToShow);
    WebElement elementToCheck = driver.findElementById(id);
    clickLinkById(id);

    BasePageUtils.staleSafeWait(elementToCheck);
  }

  public boolean clickOnSpecificErrorLink(String messageText) {
    List<WebElement> errorLink = driver
        .findElements(By.xpath("//span[@class='error-message-link']"));
    for (WebElement row : errorLink) {
      try {
        if (row.getText().equals(messageText)) {
          row.click();
          sleep(1000);
          return true;
        }
      } catch (WebDriverException e) {
        // Element was likely not clickable
        continue;
      }
    }
    return false;
  }

  public boolean clickOnSpecificErrorLinkLegacy(String messageText) {
    List<WebElement> errorLink = driver
        .findElements(By.xpath("//div[@class='errorMessage']/ul/li/h1/a"));
    for (WebElement row : errorLink) {
      try {
        if (row.getText().equals(messageText)) {
          row.click();
          sleep(1000);
          return true;
        }
      } catch (WebDriverException e) {
        // Element was likely not clickable
        continue;
      }
    }
    return false;
  }

  /**
   * @return true/false if cursor is on specific field
   */
  public boolean isFieldFocused(String editFieldId) {
    WebElement someElement = driver.findElementById(editFieldId);
    boolean hasFocus = someElement.equals(driver.switchTo().activeElement());
    return hasFocus;
  }

  public void clickBodyElement() {
    driver.findElement(By.tagName("body")).click();
  }

  /**
   * Method to determine whether expected display text is there on the element of the page.
   *
   * @return boolean (is it there)
   */
  public boolean isAcceptancePolicyDisplayed(String elementId, String displayText) {
    defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
    Boolean displays;
    try {
      defaultWait.until(ExpectedConditions.visibilityOfElementLocated(By.id(elementId)));
      WebElement someElement = driver.findElementById(elementId);
      displays = someElement.getText().equals(displayText);
    } catch (Exception ex) {
      displays = false;
    }
    return displays.booleanValue();
  }

  // WIP - THESE ORIGINALLY CREATED FOR AGENCY SETTINGS - GENERAL SETTINGS. NEEDS WORK

  /**
   * Returns a string representation of the label for a given field.
   *
   * @param labelFor - the id of the element used to find the element you want the label for.
   * @return - a string that represents the label's text.
   * @note: this is NOT the id of the label itself.
   */
  public String getLabel(String labelFor) {
    return driver.findElementByXPath("//label[@for='" + labelFor + "']").getText();
  }

  /**
   * Returns the string representation of the value given an element id and an element type. This
   * needs further work such as setting up an enum for all the pottential elements you could have on
   * a page. Maybe that's too much!
   *
   * @param elementId   - the id of the web element you want to get the value for.
   * @param elementType - the type of element you are looking to get the value from.
   * @return String representing the value.
   */
  public String getValue(String elementId, String elementType) {
    String value;
    if (elementType.contentEquals("input")) {
      value = driver.findElementById(elementId).getAttribute("value");
    } else if (elementType.contentEquals("select")) {
      value = new Select(driver.findElementById(elementId)).getFirstSelectedOption().getText();
    } else {
      value = driver.findElementById(elementId).getText();
    }
    return value;
  }

  /**
   * Sets the value of a given element id and an element type. This needs further work such as
   * setting up an enum for all the pottential elements you could have on a page.
   *
   * @param elementId   - the id of the web element you want to get the value for.
   * @param elementType - the type of element you are looking to get the value from.
   * @param value       representing the value top set.
   */
  public void setValue(String elementId, String elementType, String value) {
    WebElement webElement = driver.findElementById(elementId);
    if (elementType.contentEquals("input")) {
      webElement.clear();
      webElement.sendKeys(value);
    } else {
      Select select = new Select(webElement);
      select.selectByVisibleText(value);
    }
  }

  /**
   * Validates that every entry in the array of strings is in the same order as the LinkedHashMap.
   *
   * @param expectedValue - Array of strings representing the expected values in order.
   * @param actualValue   - Key/Value pairs in the order in which they were placed on the hash map.
   * @param checkLabels   - whether we should check the keys or values of the hash map.
   * @return true/false
   */
  public boolean isInOrder(String[] expectedValue, LinkedHashMap<String, String> actualValue,
      boolean checkLabels) {
    boolean isInOrder = true;
    if (expectedValue.length == actualValue.size()) {
      int i = 0; //index for looping over the string array
      for (Map.Entry<String, String> entry : actualValue.entrySet()) {
        String valueToCheck = checkLabels ? entry.getKey() : entry.getValue();
        if (!valueToCheck.contentEquals(expectedValue[i])) {
          isInOrder = false;
          break;
        }
        i++;
      }
    } else {
      isInOrder = false;
    }
    return isInOrder;
  }

  public String getMessageTitle() {
    return shortWaitUntilVisible(By.className("toast-title")).getText();
  }

  public String getMessage() {
    return shortWaitUntilVisible(By.className("toast-message")).getText();
  }

  /**
   * Get Message from modern stack
   */
  public String getSuccessMessage() {
    String message = "";
    try {
      WebDriverWait wait = new WebDriverWait(driver, 10);
      wait.until(
          ExpectedConditions.visibilityOf(driver.findElement(By.className("toast-success"))));
      message = getMessage();
      sleep(1000);
    } catch (NoSuchElementException e) {
      message = "Nothing found.";
    }
    return message;
  }

  public boolean isErrorMessageTextDisplayed(String errorMsg) {
    boolean found = false;
    List<WebElement> messageBox = driver.findElementsByClassName("message-text");
    for (int i = 0; i < messageBox.size(); i++) {
      String getMessage = messageBox.get(i).getText();
      if (getMessage.contains(errorMsg)) {
        found = true;
        break;
      }
    }
    return found;
  }

  /**
   * Check if the 'No match found.' message return from search for modern stack
   */
  public boolean verifyNoMatchfound() {
    waitUntilVisible(By.className("no-results-message"));
    sleep(1000);
    String message = driver.findElement(By.xpath(
        "//div[contains(@class,'no-results-message')]")).getText();
    if (message.equals("No match found.")) {
      return true;
    } else {
      return false;
    }
  }
}
