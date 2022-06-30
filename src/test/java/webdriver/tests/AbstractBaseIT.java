package webdriver.tests;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.remote.RemoteWebDriver;
import webdriver.pages.e2.LoginPage;

import webdriver.utils.*;

import webdriver.utils.junit.LoggingTestWatcher;

import java.util.logging.Logger;

public abstract class AbstractBaseIT {

    public static final String E2_ENVIRONMENT = "e2.environment";
    public static Configuration config;

    static {
        //TODO: make the default properties with with localhost
        try {
            config = new PropertiesConfiguration("config.properties");

        } catch (ConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Rule
    /**
     * This is used for adding the test name to the screenshot file name.
     */

    public TestName testName = new TestName();
    /**
     * Removed the @After junit flag and replaced with the TestWatcher finished method so that
     * screenshots of only FAILED tests are captured.
     */

    // This is used in the Rule Chain below but we put it in a field so we can configure the web
    // driver later on
    public LoggingTestWatcher loggingTestWatcher = new LoggingTestWatcher(config);
    protected String screenShotName; //name of the test method
    protected RemoteWebDriver driver;
    @Rule
    public RuleChain ruleChain = RuleChain
            //Only quit the driver after everyone is done
            .outerRule(new TestWatcher() {

                @Override
                protected void finished(Description description) {
                    driver.quit();
                    ExtentWrapper.extent.flush();
                }

            })
            // set up logging contexts
            .around(loggingTestWatcher)
            // capture screen shots
            .around(new TestWatcher() {

                @Override
                protected void failed(Throwable e, Description description) {
                    System.out.println("Failed " + description);
                    //Zalenium is configured to keep video recordings of failed tests only
                    //Zelenium determines if a test fails by reading the value of the zaleniumTestPassed
                    // cookie.
                    Cookie cookie = new Cookie("zaleniumTestPassed", "false");
                    driver.manage().addCookie(cookie);
                    try {
//                        takeScreenshot();
                        ExtentWrapper.node.fail("Failed " + description);
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

            });
    protected LoginPage loginPage;
    protected Logger logger;
    protected WebDriverService webdriverService;

    @BeforeClass
    public static void loadConfiguration() throws ConfigurationException {
        ExtentWrapper.configExtentReport();
    }

    public static Configuration getConfig() {
        return config;
    }

    public static void sleep(int num) {
        try {
            Thread.sleep(num);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initalize services used by selenium tests.
     *
     * @throws Exception Generic exception.
     */
    @Before
    public void initialize() throws Exception {
        String loggerName = getClass().getSimpleName();
        logger = Logger.getLogger(loggerName);

        webdriverService = new WebDriverService();

        loadWebDriver();

        loginPage = LoginPage.open(driver);

        screenShotName = testName.getMethodName();

        if (!WebDriverService.getOS().contains("Mac")) {
            driver.manage().window().maximize();
        }

        ExtentWrapper.cleanReport();
    }

    public void loadWebDriver() throws Exception {
        WebDriverType type = getWebDriverType();
        driver = webdriverService.load(type, config, getZaleniumDashBoardName());
        loggingTestWatcher.setRemoteWebDriver(driver);
    }

    public static WebDriverType getWebDriverType() {
        String s = (String) config.getProperty("webdriver.type");
        return WebDriverType.valueOf(s);
    }

    //for setting the title in the Zalenium Dashboard.
    //returns testEnvironment.className.MethodName.
    private String getZaleniumDashBoardName() {
        Class clazz = this.getClass();
        String name = clazz.getSimpleName();
        ConfigFileReader reader = FileReaderManager.getInstance().getConfigReader();
        return ConfigFileReader.getProperty(E2_ENVIRONMENT) + "." + name + "." + testName
                .getMethodName();
    }
}
