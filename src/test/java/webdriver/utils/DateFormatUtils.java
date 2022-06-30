package webdriver.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateFormatUtils {

  public static LocalDateTime day;
  public static DateTimeFormatter formatter;
  public static ZoneId zoneId = ZoneId.of("America/Chicago");

  public static String getDate_dd_MMM_yyyy(int offset) {
    return getDate(offset, "dd-MMM-yyyy");
  }

  public static String getDate_d_MMM_yyyy(int offset) {
    return getDate(offset, "d-MMM-yyyy");
  }

  public static String getDate_dd_MMM_yy(int offset) {
    return getDate(offset, "dd-MMM-yy");
  }

  public static String getDate(int offset, String pattern) {
      day = LocalDateTime.now(zoneId).plusDays(offset);
      formatter = DateTimeFormatter.ofPattern(pattern);
      return formatter.format(day);
  }
}
