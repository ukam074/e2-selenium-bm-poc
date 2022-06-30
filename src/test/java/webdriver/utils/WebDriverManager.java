package webdriver.utils;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

public class WebDriverManager {
  private static RemoteWebDriver driver = null;
  private static ConfigFileReader reader;

  public WebDriverManager(){
    reader = FileReaderManager.getInstance().getConfigReader();
  }
  /*
  This is very similar to WebDriverService class, but doesnt require config or webdriver passed in to initalize

   */
  public static RemoteWebDriver createDriver() {
      try {
        final DesiredCapabilities capabilities = new DesiredCapabilities();

        switch (reader.getProperty("webdriver.type")) {
          case "CHROME":
            System.setProperty("webdriver.chrome.driver",
                System.getProperty("os.name").equalsIgnoreCase("MAC OS X")
                    ? reader.getProperty("chromedriver.osx.path")
                    : reader.getProperty("chromedriver.win.path")
            );

            ChromeOptions options = new ChromeOptions();
            options.addArguments("enable-automation");
            options.addArguments("--start-maximized");
            capabilities.setCapability(ChromeOptions.CAPABILITY, options);

            // Turn on performance logging
            if ("on".equalsIgnoreCase(reader.getProperty("performance.debugging"))) {
              LoggingPreferences logPrefs = new LoggingPreferences();
              logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
              capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
            }

            //ensure driver is threadsafe
            synchronized (RemoteWebDriver.class) {
              driver = new ChromeDriver(capabilities);
            }
            break;
          case "FIREFOX":
            System.setProperty("webdriver.gecko.driver", reader.getProperty("geckodriver.path"));
            capabilities.setBrowserName("firefox");
            capabilities.setCapability("acceptInsecureCerts", true);

            //ensure driver is threadsafe
            synchronized (RemoteWebDriver.class) {
              driver = new FirefoxDriver(capabilities);
            }
            break;
          default:
            System.err.println("the Web driver was not initialized; type: " + reader.getProperty("webdriver.type"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }

    synchronized(RemoteWebDriver.class){
       return driver;
      }

  }

  public static RemoteWebDriver getDriver() {
    //TODO: add things like testwatcher, screenshot, etc by default
    //ensure driver is threadsafe
    synchronized (RemoteWebDriver.class) {
      if(driver == null) driver = createDriver();
      driver.manage().deleteAllCookies();
      driver.manage().timeouts().implicitlyWait(WaitTimeManager.getImplicitlyWait(), TimeUnit.SECONDS);
      return driver;
    }
  }

  public static void closeDriver(){
    synchronized (RemoteWebDriver.class) {
      if (driver != null) {
        driver.close();
        driver.quit();
      }
    }
  }



}
