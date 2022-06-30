package webdriver.utils.junit;

import org.apache.commons.configuration.Configuration;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.ThreadContext;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LoggingTestWatcher extends TestWatcher {


    @Override
    protected void succeeded(Description description) {
        super.succeeded(description);

        ThreadContext.put("result", "pass");
        updateMemoryInfo();

        statsLog.info("Test Passed");
    }

    @Override
    protected void failed(Throwable e, Description description) {
        super.failed(e, description);

        ThreadContext.put("result", "fail");
        updateMemoryInfo();
        statsLog.info("Test Failed");

        if ("onFailure".equalsIgnoreCase(config.getProperty("performance.debugging").toString())) {

            logChromeDebugEvents();
        }

    }

    private static final org.apache.logging.log4j.Logger log = org.apache.logging.log4j.LogManager.getLogger(LoggingTestWatcher.class);
    private static final org.apache.logging.log4j.Logger statsLog = org.apache.logging.log4j.LogManager.getLogger("StatsLogger");


    protected Configuration config;

    public LoggingTestWatcher(Configuration config) {
        this.config = config;
    }

    public RemoteWebDriver getRemoteWebDriver() {
        return remoteWebDriver;
    }

    public void setRemoteWebDriver(RemoteWebDriver remoteWebDriver) {
        this.remoteWebDriver = remoteWebDriver;
    }

    private RemoteWebDriver remoteWebDriver;

    private Runtime runtime = Runtime.getRuntime();

    @Override
    protected void starting(Description description) {
        super.starting(description);

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        List<String> arguments = runtimeMxBean.getInputArguments();

        updateMemoryInfo();

        ThreadContext.put("jvmargs", String.join(" ", arguments));

        String testId = UUID.randomUUID().toString();
        System.out.println("Performance TestId:  " + testId);

        ThreadContext.put("id", testId);
        ThreadContext.put("testClass", description.getClassName());
        ThreadContext.put("testMethod", description.getMethodName());
        ThreadContext.put("testDisplayName", description.getDisplayName());

        ThreadContext.put("e2env", (String) config.getProperty("e2.environment"));
        ThreadContext.put("e2url", (String) config.getProperty("e2.url"));
        ThreadContext.put("chromeDebugging", (String) config.getProperty("performance.debugging"));

        ThreadContext.put("testStart", LocalDateTime.now().toString());

        statsLog.info("Test Started");

    }

    private void updateMemoryInfo() {
        ThreadContext.put("totalMemory", String.valueOf(runtime.totalMemory()));
        ThreadContext.put("freeMemory", String.valueOf(runtime.freeMemory()));
        ThreadContext.put("maxMemory", String.valueOf(runtime.maxMemory()));
    }

    @Override
    protected void finished(Description description) {
        super.finished(description);

        ThreadContext.put("testEnd", LocalDateTime.now().toString());
        updateMemoryInfo();
        statsLog.info("Test Finished");

        if ("on".equalsIgnoreCase(config.getProperty("performance.debugging").toString())) {

            logChromeDebugEvents();
        }

        ThreadContext.clearMap();
    }

    private void logChromeDebugEvents() {
        LogEntries entries = remoteWebDriver.manage().logs().get(LogType.PERFORMANCE);

        Integer eventNumber = 0;
        for (LogEntry entry : entries) {
            try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.put("eventNumber", (eventNumber++).toString())) {
                log.info(entry.getMessage());
            }
        }
    }
}
