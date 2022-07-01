
package webdriver.utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.*;
import webdriver.utils.grid.GridHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public class WebDriverService {

  //new zalenium grid ip and port address.
  //zalenium runs tests using xqa1 and xqa3
  private static ConfigFileReader reader = FileReaderManager.getInstance().getConfigReader();
  //ip or DNS e.g. 10.213.138.145 or zalenium.cs1.dev.e2.cloudcwt.com
  private static final String gridIP = reader.getProperty("grid.domain");
  private static final int gridPort = Integer.parseInt(reader.getProperty("grid.port"));
	private GridHelper gridHelper = new GridHelper(gridIP, gridPort);

	public static String getOS(){
		String osName = null;
		try {
			osName = System.getProperty("os.name");
		} catch (Exception ex) {
			System.err.println("unable to get os.name" + ex.getMessage());
		}
		return osName;
	}


  public RemoteWebDriver load(WebDriverType type, Configuration config, String testName)
      throws ConfigurationException, MalformedURLException {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		ChromeOptions chromeOptions = new ChromeOptions(); //US14843
		FirefoxOptions firefoxOptions = new FirefoxOptions(); //US14843
		InternetExplorerOptions IeOptions = new InternetExplorerOptions(); //US14843
		EdgeOptions edgeOptions = new EdgeOptions();
    String dockerHubEndpoint = "https://" + gridIP + ":" + gridPort + "/wd/hub";
		LoggingPreferences logPrefs = new LoggingPreferences();
		RemoteWebDriver driver;

		switch (type) {

            case BROWSER_STACK_CHROME: {
                driver = loadBrowserStack(config, capabilities);

                break;
            }
			case CHROME: {
				WebDriverManager.chromedriver().version("2.44").setup();
				chromeOptions.addArguments("enable-automation");
				chromeOptions.addArguments("--start-maximized");
				//surrounding logging in a Try/Catch for errors found during logging attempts
				try {
					// Turn on performance logging
					if ("on".equalsIgnoreCase(config.getProperty("performance.debugging").toString())) {
						logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
						capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
					}
				}finally{
					System.out.println("Unable to turn on performance logging");
				}

				chromeOptions.merge(capabilities);
		    driver = new ChromeDriver(chromeOptions);
				break;
			}
			case FIREFOX: {
        		System.setProperty("webdriver.gecko.driver", reader.getProperty("geckodriver.path"));
				capabilities = new DesiredCapabilities();
				capabilities.setBrowserName("firefox");
				capabilities.setCapability("acceptInsecureCerts", true);
				capabilities.setJavascriptEnabled(true);

				firefoxOptions.merge(capabilities);
				driver = new FirefoxDriver(firefoxOptions);
				break;
			}
			case GRID_CHROME: {
				capabilities.setPlatform(Platform.WINDOWS);
				capabilities.setBrowserName(BrowserType.CHROME);
				chromeOptions.addArguments("enable-automation");
				chromeOptions.addArguments("--start-maximized");

				//surrounding logging in a Try/Catch for errors found during logging attempts
				try{
					// Turn on performance logging
					if ("on".equalsIgnoreCase(config.getProperty("performance.debugging").toString())) {
						logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
						capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
					}
				}finally {
					System.out.println("Unable to turn on performance logging");
				}

				chromeOptions.merge(capabilities);
				driver = loadGrid(chromeOptions);
				break;
			}

            case DOCKER_GRID_CHROME_HEADLESS: {
                capabilities.setPlatform(Platform.LINUX);
                capabilities.setBrowserName(BrowserType.CHROME);
                //setting the capability "name" value to the test run name so that the Zalenium Dashboard displays videos by
                //name rather than id.
                capabilities.setCapability("name", testName);
                chromeOptions.addArguments("--headless");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--start-maximized");
                chromeOptions.addArguments("--disable-setuid-sandbox");
                capabilities.setCapability("idleTimeout", 300); //added to troubleshoot zalenium

                chromeOptions.merge(capabilities);
                driver = loadGrid(dockerHubEndpoint, chromeOptions);
                break;
            }

			case DOCKER_GRID_CHROME: {
				capabilities.setPlatform(Platform.LINUX);
				capabilities.setBrowserName(BrowserType.CHROME);
        		//setting the capability "name" value to the test run name so that the Zalenium Dashboard displays videos by
				//name rather than id.
        		capabilities.setCapability("name", testName);
				//chromeOptions.addArguments("--headless");
				//chromeOptions.addArguments("--disable-gpu");
        		chromeOptions.addArguments("--no-sandbox");
        		//troubleshooting trying below argument for zelnium.
				chromeOptions.addArguments("--start-maximized");
        		chromeOptions.addArguments("--disable-setuid-sandbox");
				Map<String, Object> prefs = new HashMap<>();
				prefs.put("download.default_directory", "/tmp");
				prefs.put("download.extensions_to_open", "application/pdf");
				prefs.put("plugins.always_open_pdf_externally", true);
				chromeOptions.setExperimentalOption("prefs", prefs);
        		capabilities.setCapability("idleTimeout", 300); //added to troubleshoot zalenium

				chromeOptions.merge(capabilities);
				driver = loadGrid(dockerHubEndpoint, chromeOptions);
				break;
			}

			default: {
				driver = null;
				System.err.println("the Web driver was not initialized; type: " + type);
				break;
			}
		}

		//ensure driver is threadsafe
		synchronized (RemoteWebDriver.class) {
			return driver;
		    }
		}

	/**
	 * one place to concat url
	 * @return url
	 */
	private String getGridURL() {
		System.out.println("GridURL: " + "http://" + gridIP + ":" + gridPort + "/wd/hub");
		return "http://" + gridIP + ":" + gridPort + "/wd/hub";
	}

	/**
	 * one place to add extra options for driver
	 * @param driver
	 * @return RemoteWebDriver
	 */
	private RemoteWebDriver decorateDriver(RemoteWebDriver driver) {
		driver.setFileDetector(new LocalFileDetector());
		// emit view grid node-level information
		gridHelper.getNodeSessionInformation(driver.getSessionId());
		//maximize window for all tests
		//ensure driver is threadsafe
		synchronized (RemoteWebDriver.class) {
			driver.setFileDetector(new LocalFileDetector());
			driver.manage().timeouts().implicitlyWait(WaitTimeManager.getImplicitlyWait(), TimeUnit.SECONDS);
			if (driver.getCapabilities().getPlatform().is(Platform.MAC)) {
				driver.manage().window().fullscreen();
			} else {

			}
			return driver;
		}
	}

	//TODO: should refactor loadGrid out into its own class :)
	/**
	 * overload loadGrid to use new ChromeOptions
	 * @param capabilities
	 * @return RemoteWebDriver
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadGrid(ChromeOptions capabilities) throws MalformedURLException {
		RemoteWebDriver driver = new RemoteWebDriver( new URL(getGridURL() ), capabilities);
		return decorateDriver(driver);
	}

	/**
	 * overload loadGrid to use new FirefoxOptions
	 * @param capabilities
	 * @return RemoteWebDriver
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadGrid(FirefoxOptions capabilities) throws MalformedURLException {
		RemoteWebDriver driver = new RemoteWebDriver( new URL( getGridURL() ), capabilities);
		return decorateDriver(driver);
	}

	/**
	 * overload loadGrid to use new IE options
	 * @param capabilities
	 * @return
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadGrid(InternetExplorerOptions capabilities) throws MalformedURLException {
		RemoteWebDriver driver = new RemoteWebDriver( new URL( getGridURL() ), capabilities);
		return decorateDriver(driver);
	}

	/**
	 * overload loadGrid to use new Edge options
	 * @param capabilities
	 * @return
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadGrid(EdgeOptions capabilities) throws MalformedURLException {
		RemoteWebDriver driver = new RemoteWebDriver( new URL( getGridURL() ), capabilities);
		return decorateDriver(driver);
	}

	/**
	 * Loads the Selenium grid at a specific hub endpoint w/Chrome options
	 * @param hubEndpoint The specific URL of the hub
	 * @param capabilities The desired capabilities of the RemoteWebdriver
	 * @return An instance of a RemoteWebdriver
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadGrid(String hubEndpoint, ChromeOptions capabilities) throws MalformedURLException {
    System.out.println("loadGrid URL: " + hubEndpoint);
		RemoteWebDriver driver = new RemoteWebDriver(new URL(hubEndpoint), capabilities);
		return driver;
	}

	/**
	 * Loads the Selenium grid at a specific hub endpoint w/Firefox options
	 * @param hubEndpoint
	 * @param capabilities
	 * @return
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadGrid(String hubEndpoint, FirefoxOptions capabilities) throws MalformedURLException {
		System.out.println("loadGrid URL: " + hubEndpoint);
		RemoteWebDriver driver = new RemoteWebDriver(new URL(hubEndpoint), capabilities);
		return driver;
	}

	/**
	 * This has the webdriver.tests through browserstack
	 *
	 * @return a WebDriver object for BrowserStack
	 * @throws MalformedURLException
	 */
	private RemoteWebDriver loadBrowserStack(Configuration config, DesiredCapabilities capabilities) throws MalformedURLException
	{
		String browserName = config.getString("browser.name", "chrome"); //will default to chrome if config.properties doesn't specify
		String browserVersion = config.getString("browser.version", null);
		String os = config.getString("os", "Windows");
		String osVersion = config.getString("os.version", "7");
		capabilities.setCapability("os", os);
		capabilities.setCapability("os_version", osVersion);
		capabilities.setCapability("browser", browserName);
		capabilities.setCapability("browserVersion", browserVersion);
		capabilities.setCapability("resolution", "1600x1200");

		RemoteWebDriver driver = new RemoteWebDriver(new URL("https://cwt1:yw8WTEeeYZdE6czxzJNx@hub-cloud.browserstack.com/wd/hub"), capabilities);
		driver.setFileDetector(new LocalFileDetector());

		return driver;
	}

}
