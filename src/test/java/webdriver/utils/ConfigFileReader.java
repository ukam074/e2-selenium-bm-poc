package webdriver.utils;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

import java.io.IOException;
import java.util.Properties;

public class ConfigFileReader {

  private static Properties properties = null;

  /**
   * Read the config.properties file and make the information available to the application.
   */
  public ConfigFileReader() {
    try {
      properties = new Properties();
      //ensure properties is threadsafe
      synchronized (Properties.class) {
        properties
            .load(ConfigFileReader.class.getClassLoader().getResourceAsStream("config.properties"));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static String getProperty(String key) {
    return properties == null ? null : properties.getProperty(key, "");
  }

  /**
   * Check if running locally or on Jenkins.  Return true if running locally.
   *
   * @return Boolean
   * @UserStory US15002
   */
  public static boolean isRunningLocally() {
    String environment = "";

    try {
      environment = getProperty("e2.profile");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (environment.contains("LOCAL-") ? TRUE : FALSE);
  }

  /**
   * Determines if test is running in a docker grid.
   *
   * @return boolean Is test running in docker grid?
   * @UserStory US15002
   */
  public static boolean isRunningInADockerGrid() {
    String environment = "";
    try {
      environment = getProperty("e2.profile");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (environment.contains("DOCKER_GRID_") ? TRUE : FALSE);
  }

  /**
   * Determines if test is running in a docker grid on the developers local machine.
   *
   * @return boolean Is test running in docker grid on the local machine?
   * @UserStory US15002
   */
  public static boolean isRunningInALocalDockerGrid() {
    String environment = "";
    try {
      environment = getProperty("e2.profile");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (environment.equalsIgnoreCase("DOCKER_GRID_LOCAL") ? TRUE : FALSE);
  }

  /**
   * Determines if test is running on Jenkins. Jenkins uses a specific profile.
   *
   * @return boolean Is test running on Jenkins?
   * @UserStory US15002
   */
  public static boolean isRunningInJenkinsGrid() {
    String environment = "";
    try {
      environment = getProperty("e2.profile");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return (environment.equalsIgnoreCase("GRID_CHROME") ? TRUE : FALSE);
  }
}
