package webdriver.tests;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestName;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;
import webdriver.pages.e2.LoginPage;
import webdriver.user.E2User;

import webdriver.user.service.JdbcUserService;
import webdriver.user.service.UserService;
import webdriver.utils.*;

import webdriver.utils.junit.LoggingTestWatcher;


import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

public abstract class AbstractBaseIT {

    //TODO: rename all these to all uppercase
    public static final String E2_Create_User_Admin_Username_Key = "e2.create.user.admin.username";
    //TODO: rename all these to all uppercase
    public static final String E2_Create_User_Admin_Password_Key = "e2.create.user.admin.password";
    //TODO: rename all these to all uppercase
    public static final String E2_Create_User_GetThere_Minor_Key = "e2.create.user.getthere.minor";
    public static final String E2_ENVIRONMENT = "e2.environment";
    public static final String E2_PROFILE = "e2.profile";
    public static final String USERNAME = "dinaestrada1";
    public static final String AUTOMATE_KEY = "msPofztHtCcDqALoyScc";
    public static final String URL =
            "http://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub";
    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager
            .getLogger(AbstractBaseIT.class);
    private static final org.apache.logging.log4j.Logger statsLog =
            org.apache.logging.log4j.LogManager
                    .getLogger("StatsLogger");
    public static Configuration config;
    protected static E2User originalTraveler;
    protected static String clonedUserName;

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
    protected static UserService userService;
//    protected SampleDataService sampleDataService;
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

//        userService = new JdbcUserService();

//        sampleDataService = new FlatfileSampleDataService();

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

    protected String getRandomString(int length) {
        return RandomStringUtils
                .random(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
    }

    public int getRandomInt(int minimum, int maximum) {
        int min = minimum;
        int max = maximum;

        //Generate random int value from minimum to maximum
        int random_int = (int) Math.floor(Math.random() * (max - min + 1) + min);
        return random_int;
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

//    /**
//     * Take a screenshot, user passes in the name of the screenshot. appends date and time of
//     * screenshot to name.
//     *
//     * @throws ScreenshotException Exception thrown when attepmting to take a screenshot.
//     * @throws IOException         General IOException.
//     */
//    public void takeScreenshot() throws ScreenshotException, IOException {
//        screenShotName = screenShotName.replaceAll("[^a-zA-Z0-9]", "");
//        screenShotName += "-" + AbstractDocumentsPage.getDate_dd_Mmm_yyyy_HH_mm_ss(0);
//
//        String workspace =  System.getenv("WORKSPACE");
//
//        // take the screenshot
//        try {
//            TakesScreenshot scrShot = (driver);
//            File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
//            System.out.println("path to srcFile: " + srcFile.getAbsolutePath());
//
//            //make the target directory
//            String outputPath = workspace + "/target/screenshots";
//            File outdir = new File(outputPath);
//
//            outdir.mkdirs();
//
//            //create a file in the directory
//            File destFile = new File(outdir + "/" + screenShotName + ".png");
//            System.out.println("created destfile " + destFile.getAbsolutePath());
//
//            //copy the file to the new filename and location.
//            FileUtils.copyFile(srcFile, destFile);
//
//            //get the path to the screenshot so we can include the URL in the log.
//            final String jenkinsPathToScreenShot = getJenkinsPathToScreenShot();
//         //   System.out.println("path to screenshot, if link is broken try archived location above: " + jenkinsPathToScreenShot);
//        } catch (
//                Exception e) {
//            System.out.println("Exception occurred when attempting to take a screenshot. " + e.getMessage());
//        }
//
//    }

    /**
     * Create the path to the screenshot file in Jenkins.
     */
    public String getJenkinsPathToScreenShot() {
        final String jenkinsBuildURL = System.getenv("BUILD_URL");
        final String jenkinsArchiveLocator = "artifact/target/screenshots/" + screenShotName + ".png";
        String node = "3"; //see how to get this dynamically...
        System.out.println("On old Jenkins, Screenshots are located here: " + System.getenv("JOB_URL") + "ws/target/screenshots");

/*
        System.out.println("BUILD_URL " + System.getenv("BUILD_URL"));
        System.out.println("JENKINS_URL " + System.getenv("JENKINS_URL"));
        System.out.println("WORKSPACE " + System.getenv("WORKSPACE"));
        System.out.println("NODE_NAME " + System.getenv("NODE_NAME"));
        System.out.println("DBUS_SESSION_BUS_ADDRESS " + System.getenv("DBUS_SESSION_BUS_ADDRESS"));
        System.out.println("EXECUTOR_NUMBER " + System.getenv("EXECUTOR_NUMBER"));
        System.out.println("HOME " + System.getenv("HOME"));
        System.out.println("HUDSON_HOME " + System.getenv("HUDSON_HOME"));
        System.out.println("JOB_DISPLAY_URL " + System.getenv("JOB_DISPLAY_URL"));
        System.out.println("JOB_URL " + System.getenv("JOB_URL"));
        System.out.println("PATH " + System.getenv("PATH"));
        System.out.println("PWD " + System.getenv("PWD"));
        System.out.println("RUN_DISPLAY_URL " + System.getenv("RUN_DISPLAY_URL"));
        System.out.println("XDG_DATA_DIRS " + System.getenv("XDG_DATA_DIRS"));
        System.out.println("XDG_RUNTIME_DIR " + System.getenv("XDG_RUNTIME_DIR"));
        System.out.println("XDG_SESSION_ID " + System.getenv("XDG_SESSION_ID"));
*/

        String path = jenkinsBuildURL + "execution/node/" + node + "/ws/target/screenshots/" + screenShotName + ".png";
        System.out.println("On shared-services Jenkins screenshots will be here: ");
        System.out.println(jenkinsBuildURL + jenkinsArchiveLocator);


        return path;
    }

    /**
     * Clones the specified user.
     *
     * @param travelerUserName the E2 logname for the source user to be cloned
     */
    public void setUpClonedUser(String travelerUserName) throws Exception {
        // Clone Traveler
        clonedUserName = userService.uniqueUsername();
        originalTraveler = userService.retrieveUser(travelerUserName);
        String result = userService.cloneUser(originalTraveler, clonedUserName);
        assertEquals("Expected to create a cloned user", "OK", result);
    }

    /**
     * Delete the specified user.
     */
    public static void deleteClonedUser(String clonedUserName) {
        String result = userService.deleteUser(clonedUserName);
        originalTraveler = null;
        clonedUserName = null;
    }



    /*
    original screenshot creation/url creator methods. use to store screenshots under the workspace
    of each job. Now screenshots for all jobs are stored in the
    e2-selenium-screenshot-repository-and-maintenance job.
    */

//    public void takeScreenshot_localPathStore() throws ScreenshotException, IOException {
//        screenShotName = screenShotName.replaceAll("[^a-zA-Z0-9]", "");
//        screenShotName += "-" + AbstractDocumentsPage.getDate_dd_Mmm_yyyy_HH_mm_ss(0);
//
//        TakesScreenshot scrShot = (driver);
//        File srcFile = scrShot.getScreenshotAs(OutputType.FILE);
//        File destFile = new File("./target/screenshots/" + screenShotName + ".png");
//        String outpath = "./target/screenshots/";
//        File outdir = new File(outpath);
//        outdir.mkdirs();
//        FileUtils.copyFile(srcFile, destFile);
//        System.out.println("Path to screenshot of E2 upon test failure:");
//        final String jenkinsPathToScreenShot = getJenkinsPathToScreenShot_StoreInLocal(destFile);
//
//      //statsLog.info("Screenshot: {}", jenkinsPathToScreenShot);
//
//        System.out.println(jenkinsPathToScreenShot);
//    }

    public String getJenkinsPathToScreenShot_StoreInLocal(File destFile) {
        String path = null;
        try {
            path = destFile.getCanonicalPath();
            System.out.println("get cononical path is: " + path);
            String part1_jenkinsURL = "https://jenkins.shared-services.cloudcwt.com/job/Teams/job/E2/job/Selenium%20Test/job/qa1/job";
            String part2_getTestJobName = path.substring(path.indexOf("/Selenium Test/qa1/") + 18, path.indexOf("/target/"));
            System.out.println("test job is " + part2_getTestJobName);
            String part3_pathToScreenShot = "/ws/target/screenshots/" + screenShotName + ".png";
            path = part1_jenkinsURL + part2_getTestJobName + part3_pathToScreenShot;
        } catch (Exception e) {
            System.out.println("failed in getting path to screenshot url" + e.getMessage());
        }
        return path;
    }

}
