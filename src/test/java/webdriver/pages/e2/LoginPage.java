package webdriver.pages.e2;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import webdriver.utils.BasePageUtils;

public class LoginPage extends AbstractE2BasePage {

  /**
   * set no default value and rely on the Maven resource filtering to set a value.
   */
  public static String url = null;//"https://e2.qa.cwtsatotravel.com/";
  private static String PRIVACY_POLICY_LOGIN_FOOTER_MODAL_ID = "privacyActInfoModalCDiv";
  private static String PRIVACY_POLICY_LOGIN_MODAL_ID = "privacyActInnerContent";

  /**
   * Creating a model of the login page.
   *
   * @param webdriver passed to method to make a login page.
   */
  public LoginPage(RemoteWebDriver webdriver) {
    super(webdriver);
    try {
      Configuration config = new PropertiesConfiguration("config.properties");
      String maybeUrl = config.getString("e2.url");
      if (maybeUrl != null) {
        //ensure config is threadsafe
        synchronized (config) {
          url = maybeUrl;
        }
      }
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    webdriver.get(url);

    //use PageFactory to streamline getting elements
    PageFactory.initElements(webdriver, this);
  }

  /*----------------perform functions----------------*/
  public static LoginPage open(RemoteWebDriver driver) {
    return new LoginPage(driver);
  }

  /*-----------Selenium PageFactory Elements----------*/
  @FindBy(how = How.ID, using = "UserId")
  public WebElement userId;

  @FindBy(how = How.ID, using = "password")
  public WebElement password;

  @FindBy(how = How.ID, using = "okta-signin-username")
  public WebElement oktaUserId;

  @FindBy(how = How.ID, using = "okta-signin-password")
  public WebElement oktaUserPassword;

  @FindBy(how = How.LINK_TEXT, using = "Self Registration")
  public WebElement selfRegLink;

  @FindBy(how = How.ID, using = "Submit")
  public WebElement submitBtn;

  @FindBy(how = How.ID, using = "messageBox")
  public WebElement msgBox;

  @FindBy(how = How.ID, using = "privacyActAccept")
  public WebElement privacyActAccept;

  @FindBy(how = How.ID, using = "MyE2NavLink")
  public WebElement myE2NavLink;

  @FindBy(how = How.ID, using = "mainMssg")
  public WebElement mainMssg; //errorMessagePanel = driver.findElementById("mainMssg");

  @FindBy(how=How.CSS,using = "icon.error-16")
  public WebElement oktaAlertError;

  @FindBy(how = How.XPATH, using =
      "//table[@id='privacyActInfoModalContentTable']//input[@value='Exit Window']" )
  public WebElement exitWindow;

  @FindBy(how = How.ID, using = "top-navigation")
  public WebElement topNav;

  @FindBy(how = How.ID, using = "privacyActInfoModalCDiv")
  public WebElement footerModalId;

  @FindBy(how = How.XPATH, using = ".//tbody/tr[2]/td/div" )
  public WebElement tbody2;

  @FindBy(how = How.ID, using = "privacyActInnerContent" )
  public WebElement privacyPolicyLoginModal;



  public LoginPage loginStopAtModal(String user, String password) {
    setUsernameAndPassword(user, password);
    waitUntilClickableAndClick( "Submit" );
    waitUntilVisible( "privacyActAccept" );
    return this;
  }


  public boolean isUserNameFieldPresent() {
    return userId.isDisplayed();
        //driver.findElement(By.id("UserId")).isDisplayed();
  }

  public boolean isSelfRegistrationLinkPresent() {
    return //!this.selfRegLink.getText().isEmpty(); //Using Webelement object here doesnt work
        !driver.findElements(By.linkText("Self Registration")).isEmpty();
  }

  public String getUserNameInput() {
    return this.userId.getAttribute("value");
        //driver.findElement(By.id("UserId")).getAttribute("value");
  }

  public boolean isPasswordFieldPresent() {
    return this.password.isDisplayed();
        //driver.findElement(By.id("password")) .isDisplayed();
  }

  public String getLoginInput() {
    return this.password.getAttribute("value");
    //return driver.findElement(By.id("password")) .getAttribute("value");
  }

  public boolean isLoginButtonPresent() {
    return this.submitBtn.isDisplayed();
    //return driver.findElement(By.id("Submit")).isDisplayed();
  }

  public boolean isLoginButtonClickable() {
    return isClickable("Submit");
  }

  /**
   * Used when need to click on  the Login.
   */
  public void clickLogin() {
    WebElement errorMessagePanel = this.mainMssg; //driver.findElementById("mainMssg");
    defaultWait.until(ExpectedConditions.elementToBeClickable( By.id("Submit") )).click();
    BasePageUtils.staleSafeWait(errorMessagePanel);
  }

  public LoginPage clickPrivacyActNoticeLink() {
    waitUntilClickableAndClick("privacyActLink");
    return this;
  }


  public String getPrivacyPolicyTextFromLoginFooter() {
    return driver.findElementById(PRIVACY_POLICY_LOGIN_FOOTER_MODAL_ID)
        .findElement(By.xpath(".//tbody/tr[2]/td/div")).getText();
        //footerModalId.findElement( (By) this.tbody2).getText();
  }

  public String getPrivacyPolicyTextFromLoginModal() {
    return this.privacyPolicyLoginModal.getText();
        //driver.findElementById(PRIVACY_POLICY_LOGIN_MODAL_ID).getText();
  }
  /*----------------set functions----------------*/
  public void setUsername(String username) {
    this.userId.clear();
    this.userId.sendKeys(username);
    defaultWait.until(ExpectedConditions.textToBePresentInElementValue(userId,username));
  }

  public void setPassword(String password) {
    this.password.clear();
    this.password.sendKeys(password);
    defaultWait.until(ExpectedConditions.textToBePresentInElementValue(this.password,password));
  }

  public void setOktaUsername(String username) {
    this.oktaUserId.clear();
    this.oktaUserId.sendKeys(username);
    defaultWait.until(ExpectedConditions.textToBePresentInElementValue(oktaUserId,username));
  }

  public void setOktaPassword(String password) {
    this.oktaUserPassword.clear();
    this.oktaUserPassword.sendKeys(password);
    defaultWait.until(ExpectedConditions.textToBePresentInElementValue(this.oktaUserPassword,password));
  }

  /**
   * Sets the username and password on the Login page.
   *
   * @param username the username
   * @param password the password
   */
  public void setUsernameAndPassword(String username, String password) {
    setUsername(username);
    setPassword(password);
  }

  public void setOktaUsernameAndPassword(String username, String password) {
    setOktaUsername(username);
    setOktaPassword(password);
  }


  public void clickNeedHelpSigningInLink(){
//    driver.findElement(By.linkText("Need help signing in?")).click();
    waitUntilClickableAndClick(By.linkText("Need help signing in?"));

  }

  public void clickOktaForgotPasswordLink(){

    driver.findElement(By.linkText("Forgot password?")).click();
  }

  public void enterUserEmailOkta(String email){

    driver.findElement(By.id("account-recovery-username")).sendKeys(email);
  }

  public void clickResetViaEmailButton(){

//    driver.findElement(By.cssSelector("a.button.button-primary.button-wide.email-button.link-button")).click();
    waitUntilClickableAndClick(By.cssSelector("a.button.button-primary.button-wide.email-button.link-button"));
    sleep(1000);
  }

  public void clickBackToSignInButton(){

//    driver.findElement(By.cssSelector("a.button.button-primary.button-wide.link-button")).click();
    waitUntilClickableAndClick(By.cssSelector("a.button.button-primary.button-wide.link-button"));
    longWaitUntilVisible("okta-signin-username");
  }

  public String getTitle() {
    return driver.getTitle();
  }

  public boolean messageContains(String txt) {
    return msgBox.getText().contains(txt);
  }

  public WebElement oktaLoginAlertMessage() {
    return oktaAlertError;
  }

}
