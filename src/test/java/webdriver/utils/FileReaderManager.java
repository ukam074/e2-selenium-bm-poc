package webdriver.utils;

public class FileReaderManager {

  private static FileReaderManager fileReaderManager = new FileReaderManager();
  private static ConfigFileReader reader;

  private FileReaderManager() {
    try {
      reader = new ConfigFileReader();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  public static FileReaderManager getInstance( ) {
    return fileReaderManager;
  }

  public ConfigFileReader getConfigReader() {
    return (reader == null) ? new ConfigFileReader() : reader;
  }

}
