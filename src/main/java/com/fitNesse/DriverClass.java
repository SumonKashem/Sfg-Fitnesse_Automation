package com.fitNesse;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.xml.internal.ws.policy.spi.PolicyAssertionValidator;
import com.thoughtworks.selenium.CommandProcessor;
import javafx.scene.paint.Stop;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import sun.awt.geom.AreaOp;
import sun.rmi.runtime.Log;

import javax.swing.text.Utilities;
import java.beans.Visibility;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.rmi.server.UID;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.awt.SystemColor.text;
import static java.util.Calendar.MONTH;
import static org.apache.commons.lang.StringUtils.contains;
import static org.apache.commons.lang.StringUtils.split;

public class DriverClass extends Object{

    static WebDriver driver;
    static String getUrl = "Staging";
    static SoftAssert softAssert = new SoftAssert();
    static long suiteStartTime;
    static long suiteEndTime;
    static long testStartTime;
    static long testEndTime;
    static String Path = null;
    private boolean stopBrowserOnAssertion = true;
    private CommandProcessor commandProcessor;
    static ExtentReports extentReport;
    static ExtentTest extentTest;
    public void stopBrowser() {
        commandProcessor.stop();
        commandProcessor = null;
        System.out.println("Command processor stopped");
    }
    public void setStopBrowserOnAssertion(boolean stopBrowserOnAssertion) {
        this.stopBrowserOnAssertion = stopBrowserOnAssertion;
    }


    public static void startExtentReport(String testName,String ScenarioDescrtiption) throws InterruptedException {
        Thread.sleep(1300);
        extentReport = ExtentReports(testName);
        extentReport.addSystemInfo("Test Environment", geturl);
        extentReport.addSystemInfo("Browser Name", "Chrome");
        extentTest = extentReport.startTest(testName);
        testStartTime = System.currentTimeMillis();
        extentTest.log(LogStatus.INFO, "TEST STARTED:");
        extentTest.assignCategory(ScenarioDescrtiption);
    }



    public static void setup(String port, String browser)throws Exception {
        System.out.println(":::::::::::::::::: CLOSING ALL INSTANCES OF CHROME BROWSER AND DRIVER ::::::::::::::::::");
        //Kill ALL instances and services of Chrome and ChromeDriver then free memory
        // Runtime.getRuntime().exec("C:\\Users\\sumon.kashem\\Documents\\FitNesse\\src\\main\\resources\\CleanUp.cmd");
        //Runtime.getRuntime().exec("taskkill /f /im chrome.exe");
        Runtime.getRuntime().exec("taskkill /f /im chromedriver.exe");
        Runtime.getRuntime().exec("taskkill /f /im chrome.exe");

        Thread.sleep(1700);

        System.out.println("================= STARTING AUTOMATION TEST ON ENVIRONMENT: " + getUrl.toUpperCase() + " =================");

        if (browser.equalsIgnoreCase("FireFox")) {
            System.out.println("Firefox Browser is used");
            System.setProperty("webdriver.gecko.driver", "src\\main\\resources\\geckodriver.exe");
            driver = new FirefoxDriver();
            driver.manage().window().maximize();
        }
        else if (browser.equalsIgnoreCase("IE")) {
            System.out.println("IE Browser is used");
            System.setProperty("webdriver.ie.driver", "browsers\\IEDriverServer.exe");
            DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
            capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
            driver = new InternetExplorerDriver(capabilities);
            driver.manage().window().maximize();
        } else if (browser.equalsIgnoreCase("Chrome")) {
            System.out.println("Chrome Browser is used");
            System.setProperty("webdriver.chrome.driver", "src\\main\\resources\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("port="+ port +"","--incognito", "--start-maximized", "--disable-extensions");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(3,TimeUnit.SECONDS);
        }

    }


    static String geturl = "Staging";
    static DateFormat dateFormat1 = new SimpleDateFormat("yyMMdd-hhmm");
    public static String today1 =  dateFormat1.format(new Date());
    static String strFrameworkLocation =System.getProperty("user.dir");
    public static String reportsPath = strFrameworkLocation + "\\ExtentReport\\" + geturl.toUpperCase() + "//";
    static String filePath = reportsPath + "TestScenarios"+ today1 +"//" + today1;

    public boolean waitForElementToLoad(String Locator, String elementName) throws InterruptedException {
        int i = 0;
        try{
            extentTest.log(LogStatus.INFO,"Waiting for element " + elementName + " to load");
            Thread.sleep(1400);
            Long startTime = System.currentTimeMillis();
            while(driver.findElement(By.xpath(Locator)).isDisplayed() && driver.findElement(By.xpath(Locator)).isEnabled() && driver.findElements(By.xpath(Locator)).size() > 0){
                Thread.sleep(1400);
                if(driver.findElements(By.xpath(Locator)).size() > 0){
                    //System.out.println("Element - - " + Locator + " - - is found on iteration " + i);
                    Long endTime = System.currentTimeMillis();
                    extentTest.log(LogStatus.PASS,"Element " + elementName + " is found");
                    //extentTest.log(LogStatus.INFO,"Element is found in " + convertSecondsToHMmSs(endTIme - startTime));
                    break;
                }
                // i = i + 1;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Fail - element " + elementName + " is not found - " + e);
            extentTest.log(LogStatus.FAIL,"Fail - element " + elementName + " is not found - " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Fail - element " + elementName + "  is not found - " + e);
            }
            return false;
        }
    }

    public boolean waitForElementToLoadByRow(String locator, String rowNumber, String elementName) throws InterruptedException {
        int i = 0; String loc = null;
        try{
            extentTest.log(LogStatus.INFO,"Waiting for element " + elementName + " to load");
            Thread.sleep(1400);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            Long startTime = System.currentTimeMillis();
            while(driver.findElement(By.xpath(loc)).isDisplayed() && driver.findElement(By.xpath(loc)).isEnabled() && driver.findElements(By.xpath(loc)).size() > 0){
                if(driver.findElements(By.xpath(loc)).size() > 0){
                    //System.out.println("Element - - " + Locator + " - - is found on iteration " + i);
                    Long endTime = System.currentTimeMillis();
                    extentTest.log(LogStatus.PASS,"Element " + elementName + " is found");
                    //extentTest.log(LogStatus.INFO,"Element is found in " + convertSecondsToHMmSs(endTIme - startTime));
                    break;
                }
                // i = i + 1;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Fail - element " + elementName + " is not found - " + e);
            extentTest.log(LogStatus.FAIL,"Fail - element " + elementName + " is not found - " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Fail - element " + elementName + "  is not found - " + e);
            }
            return false;
        }
    }


    public boolean waitForElementToLoadByRowAndIndex(String Locator, String rowNumber, int indexNumber, String elementName) throws InterruptedException {
        int i = 0;
        try{
            extentTest.log(LogStatus.INFO,"Waiting for element " + elementName + " to load");
            Thread.sleep(1400);
            Long startTime = System.currentTimeMillis();
            String loc = null;
            if(Locator.startsWith("//*[")) {
                loc = Locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(Locator.startsWith("[@data")){
                loc = Locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            WebDriverWait wait = new WebDriverWait(driver, 20);
            if(wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy((By.xpath(loc)))).get(indexNumber).isDisplayed()){;
                    extentTest.log(LogStatus.PASS,"Element " + elementName + " is found");
                    //extentTest.log(LogStatus.INFO,"Element is found in " + convertSecondsToHMmSs(endTIme - startTime));
                // i = i + 1;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Fail - element " + elementName + " is not found - " + e);
            extentTest.log(LogStatus.FAIL,"Fail - element " + elementName + " is not found - " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Fail - element " + elementName + "  is not found - " + e);
            }
            return false;
        }
    }


    public static void deleteLineByRow(String locator, String rowNumber){
        try{
            extentTest.log(LogStatus.INFO,"Deleting existing Sell/Buy Line if exist");
            WebDriverWait wait = new WebDriverWait(driver, 8);
            Actions mouse = new Actions(driver);
            String loc = null;
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            //for(int i = 0; i <= 10; i++ ) {
            while(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(loc))).isDisplayed()){
                //if (wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(loc))).isDisplayed()) {
                    mouse.moveToElement(driver.findElement(By.xpath(loc))).build().perform();
                    mouse.click().build().perform();
                    driver.findElement(By.xpath("//*[@id='deleteLine']")).click();
                    Thread.sleep(1300);
                    driver.findElement(By.xpath("//*[@type='button' and @id='btn-delete-confirm' and text()='Yes']")).click();
                    Thread.sleep(500);
               // } else {
                 //   break;
              //  }
            }

            Thread.sleep(3000);

        } catch (Exception e){

        }
    }


    public boolean waitForElementToLoadByIndex(String Locator, int indexNumber, String elementName) throws InterruptedException {
        int i = 0;
        try{
            extentTest.log(LogStatus.INFO,"Waiting for element " + elementName + " to load");
            Thread.sleep(1400);
            Long startTime = System.currentTimeMillis();
            while(driver.findElements(By.xpath(Locator)).get(indexNumber).isDisplayed()){
                if(driver.findElements(By.xpath(Locator)).get(indexNumber).isDisplayed()){
                    //System.out.println("Element - - " + Locator + " - - is found on iteration " + i);
                    Long endTime = System.currentTimeMillis();
                    extentTest.log(LogStatus.PASS,"Element " + elementName + " is found");
                    //extentTest.log(LogStatus.INFO,"Element is found in " + convertSecondsToHMmSs(endTIme - startTime));
                    break;
                }
                // i = i + 1;
            }
            return true;
        } catch (Exception e) {
            System.out.println("Fail - element " + elementName + " is not found - " + e);
            extentTest.log(LogStatus.FAIL,"Fail - element " + elementName + " is not found - " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Fail - element " + elementName + "  is not found - " + e);
            }
            return false;
        }
    }



    public boolean open(String URL) {
        System.out.println("Navigating to url " + URL);
        extentTest.log(LogStatus.INFO,"Navigating to url " + URL);
        driver.navigate().to(URL);
        return true;
    }

    public boolean closeTab() {
        extentTest.log(LogStatus.INFO,"Closing current tab");
        driver.close();
        return true;
    }


    public boolean clear(String locator, String pageName) throws InterruptedException, IOException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            System.out.println("clearing field");
            extentTest.log(LogStatus.INFO,"clearing field");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).clear();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to clear keyword(s) on input field");
            extentTest.log(LogStatus.FAIL,"Unable to clear keyword(s) on input field");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to clear now failing the test - " + e);
            }
            return false;
        }
    }




    public boolean type(String locator, String value, String elementName) throws InterruptedException, IOException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            if(elementName.toLowerCase().contains("password")) {
                extentTest.log(LogStatus.INFO, "Entering key word on " + elementName);
            } else {
                extentTest.log(LogStatus.INFO, "Entering key word " + value + " on " + elementName);
            }
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).sendKeys(value);
            return true;
        } catch (Exception e) {
            Thread.sleep(800);
            System.out.println("Fail - Unable to enter keyword on input field");
            extentTest.log(LogStatus.FAIL,"Unable to enter keyword on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to type now failing the test - " + e);
            }
            return false;

        }
    }
    public boolean click(String locator, String elementName) throws IOException, InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            System.out.println("Clicking on " + elementName);
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName);
            //Thread.sleep(1800);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).click();
            return true;
        } catch (Exception e) {
            Thread.sleep(800);
            System.out.println("Fail - Unable to click on button/link");
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click now failing the test");
            }
            return false;
        }
    }

    public static String shortUUID() {
        UUID uuid = UUID.randomUUID();
        long l = ByteBuffer.wrap(uuid.toString().getBytes()).getLong();
        return Long.toString(l, Character.MAX_RADIX);
    }

    public boolean switchToFrame(String id, String elementName) throws InterruptedException, IOException {
        try {
            Thread.sleep(1000);
            extentTest.log(LogStatus.INFO,"Switching to " + elementName + " Iframe");
            driver.switchTo().frame(id);
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Can't switch to frame");
            extentTest.log(LogStatus.FAIL,"Can't switch " + elementName + " Iframe");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to Switch to Frame now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean switchToDefaultContent() throws InterruptedException, IOException {
        try {
            Thread.sleep(800);
            extentTest.log(LogStatus.INFO,"Switching to default content");
            driver.switchTo().defaultContent();
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Can't switch to default content");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to Switch to default content... Now failing the test - " + e);
            }
            return false;
        }
    }



    public String getValue(String locator, String elementName) throws IOException, InterruptedException {
        String value = null;
        WebDriverWait wait = new WebDriverWait(driver, 7);
        try {
            System.out.println("Capturing value on " + elementName);
            extentTest.log(LogStatus.INFO,"Capturing value on " + elementName);
            Thread.sleep(800);
            value = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).getText();
        } catch (Exception e) {
            System.out.println("Fail - Can't capture/retrieve value");
            extentTest.log(LogStatus.FAIL,"Can't capture/retrieve value on " + elementName);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to get text now failing the test - " + e);
            }        }
        return value;
    }

    public boolean clickByJQuery(String name, String elementName) throws InterruptedException {
        try{
            extentTest.log(LogStatus.INFO,"Clicking on element '" + name + "' by using JQuery");
            WebDriverWait wait = new WebDriverWait(driver, 7);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(name)));
            JavascriptExecutor jse = (JavascriptExecutor)driver;
            jse.executeScript("$('#"+ name +"').click();");
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by using JQuery");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click by using JQuery now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickByJavaScript(String locator, String elementName) throws InterruptedException {

        try{
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " by using JavaScript");
            WebDriverWait wait = new WebDriverWait(driver, 7);
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))));
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by using script");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click by using java script now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickByIndexJavaScript(String locator, int indexNubmer, String elementName) throws InterruptedException {

        try{
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " with index number " + indexNubmer + " by using JavaScript");
            WebDriverWait wait = new WebDriverWait(driver, 7);
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].click();", driver.findElements(By.xpath(locator)).get(indexNubmer));
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " with index number " + indexNubmer + " by using script");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click by using java script  with index number " + indexNubmer + " now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickLinkByText(String Name, String elementName) throws InterruptedException, IOException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            System.out.println("Clicking on " + elementName);
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[text()='" + Name +"']"))).click();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to click on Link by text");
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on line by text now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean waitStatement(int milisec) throws InterruptedException {
        try{
            Thread.sleep(milisec);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public boolean clickElementbyIndex(String locator, int index){
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            System.out.println("Clicking on button/link by index");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
            driver.findElements(By.xpath(locator)).get(index).click();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to click on button/link by index");
            softAssert.fail("Unable to click on button/link by index - " + e);
            return false;
        }
    }

    public boolean waitForElementToBeLoaded(String locator){
        WebDriverWait wait = new WebDriverWait(driver, 7);
        try{
            System.out.println("Waiting for element to be loaded");
            wait.until(ExpectedConditions.stalenessOf(driver.findElement(By.xpath(locator))));
            return true;
        } catch(Exception e) {
            System.out.println("Fail - Element was not loaded - " + e);
            softAssert.fail("Fail - Element was not loaded - " + e);
            return false;
        }
    }

    public boolean waitForManageUiToLoad() throws InterruptedException {
            extentTest.log(LogStatus.INFO, "Waiting for element Manage UI to load");
            //Thread.sleep(1400);
            Long startTime = System.currentTimeMillis();
            boolean trueFalse = true ;
            int i = 1;
            while(driver.findElements(By.xpath("//*[contains(@class,'blockUI')]")).size() > 0) {
                Thread.sleep(4000);
                if (i >= 40) {
                    extentTest.log(LogStatus.FAIL, "Manage UI is taking too long to load... " + i + " seconds...");
                    getScreenShot(driver);
                    stopExtentReport();
                    if (stopBrowserOnAssertion) {
                        throw new AssertionAndStopTestError("Manage UI is taking too long to load... " + i + " seconds...");
                    }
                    trueFalse = false;
                    break;
                } else {
                    trueFalse = true;
                }
                i = i + 1;
            }
            extentTest.log(LogStatus.INFO, "Manage UI loaded successfully...");
        return trueFalse;
    }

    public boolean clickUsingMouseMovement(String locator, String elementName) throws InterruptedException {
        Thread.sleep(1200);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).build().perform();
            mouse.click().build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickOnMonthlyTabUsingMouseMovement(String month, String year, String scrollBy) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 15);
            //String mnt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'schedule-group') and @data-totalline='2']"))).getText();
            //extentTest.log(LogStatus.INFO,"all months text are " + mnt);
            Thread.sleep(1500);
            JavascriptExecutor js =  (JavascriptExecutor) driver;
            js.executeScript("scroll("+ scrollBy +",0)");
            if(wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'schedule-group') and @data-group='"+ month.toLowerCase() +"_"+ year +"']"))).get(0).isDisplayed()) {
                scrollIntoView("//*[contains(@class,'schedule-group') and @data-group='"+ month.toLowerCase() +"_"+ year +"']");
                extentTest.log(LogStatus.INFO, "Clicking on " + month + " monthly tab using mouse movement");
                Thread.sleep(900);
                Actions mouse = new Actions(driver);
                mouse.moveToElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'schedule-group') and @data-group='"+ month.toLowerCase() +"_"+ year +"']")))).build().perform();
                mouse.click().build().perform();
            } else if(wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'schedule-group') and @data-group='" + month.toUpperCase() + "_" + year + "']"))).get(0).isDisplayed()) {
                scrollIntoView("//*[contains(@class,'schedule-group') and @data-group='" + month.toUpperCase() + "_" + year + "']");
                extentTest.log(LogStatus.INFO, "Clicking on " + month + " monthly tab using mouse movement");
                Thread.sleep(900);
                Actions mouse = new Actions(driver);
                mouse.moveToElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'schedule-group') and @data-group='" + month.toUpperCase() + "_" + year + "']")))).build().perform();
                mouse.click().build().perform();
            }
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to click on " + month + " monthly tab using mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on " + month + " monthly tab using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickOnMonthlyTabUsingMouseMovement(String month, String year, String scrollBy, String viewType) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 15);
            //String mnt = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'schedule-group') and @data-totalline='2']"))).getText();
            //extentTest.log(LogStatus.INFO,"all months text are " + mnt);
            Thread.sleep(1500);
            JavascriptExecutor js =  (JavascriptExecutor) driver;
            js.executeScript("scroll("+ scrollBy +",0)");
            if(viewType.equalsIgnoreCase("Planning")) {
                if(wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'schedule-group') and @data-group='" + month.toUpperCase() + "_" + year + "' and contains(text(),'"+ month.toUpperCase() +"') and contains(text(),'GROSS & NET')]"))).get(0).isDisplayed()) {
                    scrollIntoView("//*[contains(@class,'schedule-group') and @data-group='" + month.toUpperCase() + "_" + year + "' and contains(text(),'"+ month.toUpperCase() +"') and contains(text(),'GROSS & NET')]");
                    extentTest.log(LogStatus.INFO, "Clicking on " + month + " monthly tab using mouse movement");
                    Thread.sleep(900);
                    Actions mouse = new Actions(driver);
                    mouse.moveToElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'schedule-group') and @data-group='" + month.toUpperCase() + "_" + year + "' and contains(text(),'"+ month.toUpperCase() +"') and contains(text(),'GROSS & NET')]")))).build().perform();
                    mouse.click().build().perform();
                } else if (wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath("//*[contains(@class,'schedule-group') and @data-group='" + month.toLowerCase() + "_" + year + "']"))).get(0).isDisplayed()) {
                    scrollIntoView("//*[contains(@class,'schedule-group') and @data-group='" + month.toLowerCase() + "_" + year + "']");
                    extentTest.log(LogStatus.INFO, "Clicking on " + month + " monthly tab using mouse movement");
                    Thread.sleep(900);
                    Actions mouse = new Actions(driver);
                    mouse.moveToElement(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'schedule-group') and @data-group='" + month.toLowerCase() + "_" + year + "']")))).build().perform();
                    mouse.click().build().perform();

                }
            }
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to click on " + month + " monthly tab using mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on " + month + " monthly tab using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean scroll(String scrollType, String scrollBy) throws InterruptedException {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            if(scrollType.equalsIgnoreCase("horizontal")){
                js.executeScript("scroll(" + scrollBy + ",0)");
            } else {
                js.executeScript("scroll(0," + scrollBy + ")");
            }
            return true;
        } catch (Exception e) {

            extentTest.log(LogStatus.FAIL,"Unable to scroll - " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to scroll - " + e);
            }
            return false;

        }
    }


    public boolean clickByRowUsingMouseMovement(String locator, String rowNumber, String elementName) throws InterruptedException {
        String loc = null;
        Thread.sleep(1200);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " by using mouse movement from row " + rowNumber);
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(loc))).build().perform();
            mouse.click().build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean doubleClickByRowUsingMouseMovement(String locator, String rowNumber, String elementName) throws InterruptedException {
        String loc = null;
        Thread.sleep(1200);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            extentTest.log(LogStatus.INFO,"Double clicking on " + elementName + " by using mouse movement from row " + rowNumber);
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(loc))).build().perform();
            mouse.doubleClick(driver.findElement(By.xpath(loc))).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to doubl click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to double click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickByRowAndScheduleUsingMouseMovement(String locator, String rowNumber, String mntlySchedule, String elementName) throws InterruptedException {
        Thread.sleep(1200);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " by using mouse movement from row " + rowNumber + " and monthly schedule of " + mntlySchedule);
            Actions mouse = new Actions(driver);
            String loc = locator.replace("[","");
            //mouse.moveToElement(driver.findElement(By.xpath("//*[@data-row ='"+ rowNumber +"' and @data-monthly-schedule='"+ mntlySchedule.toLowerCase() +"' and " + loc))).build().perform();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@data-row ='"+ rowNumber +"' and @data-monthly-schedule='"+ mntlySchedule.toLowerCase() +"' and " + loc))).click();
            //mouse.click().build().perform();
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on element using mouse movement with row... Now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean clickByRowAndIndexUsingMouseMovement(String locator, String rowNumber, int indexNumber, String elementName) throws InterruptedException {
        Thread.sleep(800);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " by using mouse movement from row " + rowNumber + " and index number " + indexNumber);
            scrollIntoViewByIndex("//*[@data-row ='"+ rowNumber +"']" + locator,indexNumber);
            Thread.sleep(800);
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElements(By.xpath("//*[@data-row ='"+ rowNumber +"']" + locator)).get(indexNumber)).build().perform();
            mouse.click().build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean waitForBarToLoad(String locator) throws InterruptedException {
        //  NEW LOGIN TESTING
        int boardTimeout = 20;

        // PREVIOUS WORKING CODE
        Thread.sleep(500);

        int i = 0;
        int j = 0;
        extentTest.log(LogStatus.INFO, "Waiting for page to load");
        //WebDriverWait wait = new WebDriverWait(driver, 15);
            while(!driver.findElements(By.xpath(locator)).get(0).isDisplayed()) {

                Thread.sleep(1000);

                 if(driver.findElements(By.xpath(locator)).size() == 0){
                        extentTest.log(LogStatus.PASS,"Page loaded successfully in " + i + " seconds");
                        break;

                 }

                if (i > boardTimeout){
                    Thread.sleep(800);
                    getScreenShot(driver);
                    extentTest.log(LogStatus.FAIL,"Unable to  load page successfully by " + boardTimeout + " seconds");
                    if (stopBrowserOnAssertion) {
                        throw new AssertionAndStopTestError("Unable to load page by " + boardTimeout + " seconds");
                    }
                    return false;
                }


                 i++;

            }

            return true;
    }

    public boolean waitForProgressiveBarToLoad(String locator) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 15);
            extentTest.log(LogStatus.INFO,"Waiting on Progressive Bar to Load");
            Actions mouse = new Actions(driver);
            if(!wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(locator))).get(0).isDisplayed()){
                extentTest.log(LogStatus.PASS,"Page Loaded Successfully");
            } else {
                Thread.sleep(800);
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Unable to load page in 15 seconds");
                }
            }
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to load page");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to load page - " + e);
            }
            return false;
        }
    }

    public boolean clickUsingIndexMouseMovement(String locator, int indexNumber, String elementName) throws InterruptedException {
        Thread.sleep(600);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Clicking on " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElements(By.xpath(locator)).get(indexNumber)).build().perform();
            mouse.click().build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean typeUsingMouseMovement(String locator, String value, String elementName) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Typing "  + value +  " for " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).build().perform();
            mouse.sendKeys(value).sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to type on edit field by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to type on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to type on edit field using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean keyDownAndEnter(String locator,String elementName) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Hitting Key Down on " + elementName);
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).click().sendKeys(Keys.ARROW_DOWN).sendKeys(Keys.ARROW_DOWN).build().perform();
            //mouse.moveToElement(driver.findElement(By.xpath(locator))).sendKeys(Keys.ARROW_DOWN).build().perform();
            //mouse.moveToElement(driver.findElement(By.xpath(locator))).sendKeys(Keys.ARROW_DOWN).build().perform();;
            mouse.sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to hit arrown down  by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to hit arrown down  on " + elementName);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to hit arronw down using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }


    public boolean typeByRowUsingMouseMovement(String locator, String rowNumber, String value, String elementName) throws InterruptedException {
        try{
            String loc = null;
            WebDriverWait wait = new WebDriverWait(driver, 7);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            extentTest.log(LogStatus.INFO,"Typing "  + value +  " for " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(loc))).build().perform();
            //mouse.sendKeys(Keys.DELETE).build().perform();
            mouse.sendKeys(value).sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to type on edit field by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to type on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to type on edit field using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean typeByRowAndScheduleUsingMouseMovement(String locator, String rowNumber, String mntSchedule, String value, String elementName) throws InterruptedException {
        try{
            String loc = null;
            WebDriverWait wait = new WebDriverWait(driver, 7);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='"+rowNumber+"' and @data-monthly-schedule='"+ mntSchedule.toLowerCase() +"' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='"+rowNumber+"' and @data-monthly-schedule='"+ mntSchedule.toLowerCase() +"' and @data");
            }
            extentTest.log(LogStatus.INFO,"Typing "  + value +  " for " + elementName + " by using mouse movement and monthly schedule of " + mntSchedule);
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(loc))).build().perform();
            mouse.sendKeys(value).sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to type on edit field by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to type on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to type on edit field using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean typeByRowAndIndexUsingMouseMovement(String locator, String rowNumber, int indexNumber, String value, String elementName) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Typing "  + value +  " for " + elementName + " by using mouse movement and by index " + indexNumber);
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElements(By.xpath("//*[@data-row='"+rowNumber+"']" + locator)).get(indexNumber)).build().perform();
            mouse.sendKeys(value).sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to type on edit field by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to type on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to type on edit field using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean typeUsingIndexMouseMovement(String locator, int indexNumber, String value, String elementName) throws InterruptedException {
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Typing "  + value +  " for " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElements(By.xpath(locator)).get(indexNumber)).build().perform();
            mouse.sendKeys(value).sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to type on edit field by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to type on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to type on edit field using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean enterUsingMouseMovement(String locator,String elementName) throws InterruptedException {
        try{
            Thread.sleep(500);
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Hit Enter for " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).build().perform();
            mouse.sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to hit Enter by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to hit Enter on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to hit Enter using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean enterByRowUsingMouseMovement(String locator,String rowNumber,String elementName) throws InterruptedException {
        try{
            Thread.sleep(500);
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Hit Enter for " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath("//*[@data-row='"+rowNumber+"']" + locator))).build().perform();
            mouse.sendKeys(Keys.ENTER).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to hit Enter by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to hit Enter on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to hit Enter using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean usingControlAllByMouseMovement(String locator, String elementName) throws InterruptedException {
        Thread.sleep(3000);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Hit Control + A for " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).build().perform();
            mouse.keyDown(Keys.CONTROL).sendKeys(Keys.chord("A")).keyUp(Keys.CONTROL).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to hit Enter by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to hit Control + A on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to hit Control + A using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }


    public boolean doubleClickUsingJavaScript(String locator, String elementName) throws InterruptedException {
        Thread.sleep(3000);
        try{
            extentTest.log(LogStatus.INFO,"Double click on " + elementName + " by using mouse movement");
            ((JavascriptExecutor) driver).executeScript("var evt = document.createEvent('MouseEvents');"+
                    "evt.initMouseEvent('dblclick',true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0,null);"+
                    "arguments[0].dispatchEvent(evt);", locator);
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to double click on " + elementName + " by mouse JavaScript Executor");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to double click on element using JavaScript Executor... Now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean doubleClickUsingMouseMovement(String locator, String elementName) throws InterruptedException {
        Thread.sleep(3000);
        try{
            extentTest.log(LogStatus.INFO,"Double click on " + elementName + " by using JavaScript Executor");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).build().perform();
            mouse.doubleClick().build().perform();
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to double click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to double click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean rightClickUsingMouseMovement(String locator){
        try{
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElement(By.xpath(locator))).build().perform();
            mouse.contextClick().build().perform();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean rightClickUsingIndexMouseMovement(String locator, int indexNumber, String elementName) throws InterruptedException {
        Thread.sleep(600);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Right clicking on " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.moveToElement(driver.findElements(By.xpath(locator)).get(indexNumber)).contextClick(driver.findElements(By.xpath(locator)).get(indexNumber)).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to right click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to right click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to right click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean acceptAlert() throws InterruptedException {
        Thread.sleep(800);
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
            return true;
        } catch (NoAlertPresentException e) {
            extentTest.log(LogStatus.INFO, "No Alert present while reloading page... " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("No Alert Present - " + e);
            }
            return true;
        }
    }

    public boolean reloadPage(String url) throws InterruptedException {
        Thread.sleep(800);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Reloading page by using mouse movement");
            Actions mouse = new Actions(driver);
            driver.navigate().to(url);
            try {
                Thread.sleep(800);
                Alert alert = driver.switchTo().alert();
                alert.accept();
                return true;
            } catch (NoAlertPresentException e) {
                extentTest.log(LogStatus.INFO,"No Alert present while reloading page... " + e);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Fail - Unable to reload by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to reload by mouse movement");
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to reload by mouse movement now failing the test - " + e);
            }
            return false;
        }

    }

    public boolean alert() throws InterruptedException {
        Thread.sleep(800);
        try{
            extentTest.log(LogStatus.INFO,"Clicking on Alert 'Ok' button");
            Alert alert = driver.switchTo().alert();
            alert.accept();
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to switch to Alert window");
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to switch to Alert window...Now failing the test - " + e);
            }
            return false;
        }

    }

    public boolean rightClickByRowUsingMouseMovement(String locator, String rowNumber, String elementName) throws InterruptedException {
        Thread.sleep(800);
        String loc = null;
        try{
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='"+rowNumber+"' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='"+rowNumber+"' and @data");
            }
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Right clicking on " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.contextClick(driver.findElement(By.xpath(loc))).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to right click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to right click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to right click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }

    }



    public boolean rightClickByRowAndScheduleUsingMouseMovement(String locator, String rowNumber, String mntlySchedule, String elementName) throws InterruptedException {
        String loc = null;
        Thread.sleep(800);
        try{
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='"+rowNumber+"' and @data-monthly-schedule='"+ mntlySchedule.toLowerCase() +"' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='"+rowNumber+"' and @data-monthly-schedule='"+ mntlySchedule.toLowerCase() +"' and @data");
            }
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Right clicking on " + elementName + " by using mouse movement and monthly schedule of " + mntlySchedule);
            Actions mouse = new Actions(driver);
            mouse.contextClick(driver.findElement(By.xpath(loc))).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to right click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to right click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to right click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }

    }

    public boolean rightClickByRowAndIndexUsingMouseMovement(String locator, String rowNumber, int indexNumber, String elementName) throws InterruptedException {
        Thread.sleep(600);
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            extentTest.log(LogStatus.INFO,"Right clicking on " + elementName + " by using mouse movement");
            Actions mouse = new Actions(driver);
            mouse.contextClick(driver.findElements(By.xpath("//*[@data-row='" + rowNumber + "']"+locator)).get(indexNumber)).build().perform();
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to right click on element by mouse movement");
            extentTest.log(LogStatus.FAIL,"Unable to right click on " + elementName + " by mouse movement");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to right click on element using mouse movement now failing the test - " + e);
            }
            return false;
        }

    }


    public static void scrollIntoView(String elementValue) throws InterruptedException {
        JavascriptExecutor js =  (JavascriptExecutor) driver;
        WebElement element = driver.findElement(By.xpath(elementValue));
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "border: 2px solid red;");
    }

    public static void scrollIntoViewByRow(String rowNumber,String locator) throws InterruptedException {
        JavascriptExecutor js =  (JavascriptExecutor) driver;
        String elem = null, loc = null;
        String eValue = null;
        if(locator.startsWith("//*[")) {
            loc = locator.replace("//*[", "//*[@data-row='"+rowNumber+"' and ");
        } else if(locator.startsWith("[@data")){
            loc = locator.replace("[@data", "//*[@data-row='"+rowNumber+"' and @data");
        }
        WebElement element = driver.findElement(By.xpath(loc));
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "border: 2px solid red;");
    }

    public static void scrollIntoViewByIndex(String locator, int indexNumber) throws InterruptedException {
        JavascriptExecutor js =  (JavascriptExecutor) driver;
        WebElement element = driver.findElements(By.xpath(locator)).get(indexNumber);
        js.executeScript("arguments[0].scrollIntoView(true);", element);
        Thread.sleep(500);
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);",element, "border: 2px solid red;");
    }


    public boolean objectVerification(String locator, String value, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        try{
            WebDriverWait wait = new WebDriverWait(driver, 7);
            check = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).getText();
            /*if(check.contains("[Change]")){
              String[] arrayCheck = check.split(" \\[");
              check2 = arrayCheck[0];
            } else {
                check2 = check;
            }*/
            //Assert.assertEquals(check,value);
            if(check.contains(value)){
                System.out.println("Text value - " + value + " exist as expected");
                extentTest.log(LogStatus.PASS,"Text value - " + value + " exist as expected on " + elementName);
                return true;
            } else {
                System.out.println("Fail - Expected text '" + value + "' doesn't match the actual text '" + check + "'");
                scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL,"Expected text '" + value + "' doesn't match the actual text '" + check + "' on " + elementName);
                getScreenShot(driver);
                //stopExtentReport();
                /*if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Expected "+ value + " doesn't match with actual " + check + "...Now failing the test");
                }*/
                return false;
            }
        } catch (Exception e) {
            System.out.println("Fail - Verification object is not present/visible");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean enableTextField(String locator, String value,String elementName) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 7);
        try {
            extentTest.log(LogStatus.INFO,"Enabling text field " + elementName);
            WebElement textbox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator)));
            ((JavascriptExecutor) driver).executeScript("arguments[0].value='"+ value +"'", textbox);
            //textbox.click();
            //textbox.sendKeys(value);
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Element is not present");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Element is not Present " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to locate element... Now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean oppTableVerification(String value1, String value2) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 7);
        int j = 0;
        String textValue = null, lowerTextValue;
        boolean returnValue = true;
        try{
            //driver.manage().timeouts().implicitlyWait(4,TimeUnit.SECONDS);
            for(int i = 1; i <= 10;i++){
                textValue = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='opportunitiesTable']/tbody/tr["+ i +"]"))).getText();
                //extentTest.log(LogStatus.INFO,"Opp Table result is " + textValue);
                lowerTextValue = textValue.toLowerCase();
                if(lowerTextValue.contains(value1.toLowerCase()) && lowerTextValue.contains(value2.toLowerCase())){
                    extentTest.log(LogStatus.PASS,"Opp Table: " + value1 + ", " + value2 + " exist as exptected for row " + (j+1));
                    returnValue = true;
                    break;
                }
            }
        } catch (Exception e) {
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Opp Locator doesn't exist..." + e);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Opp Detail locator doesn't exist...Now failing the test" + e);
            }
            returnValue = false;
        }

        return returnValue;
    }

    public boolean closePopUp(String locator) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 7);
        boolean returnVariable = true;
        Thread.sleep(2500);
        if(driver.findElement(By.xpath(locator)).isDisplayed()){
            extentTest.log(LogStatus.INFO,"Pop up exist and clicking on 'Ok' button");
            driver.findElement(By.xpath("//*[text()='OK']")).click();
            returnVariable = true;
        }

        return returnVariable;
    }


    public boolean VerifyUserIsActive(String locator) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 6);
        try{
            extentTest.log(LogStatus.INFO,"Verifying if User is active on user details");
            boolean elementState = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).isSelected();
            if(elementState == false){
                driver.findElement(By.xpath(locator)).click();
                driver.findElement(By.xpath("//*[@title='Save']")).click();
            } else {
                driver.findElement(By.xpath("//*[@title='Cancel']")).click();
            }
            return true;

        }catch (Exception e){
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Active checkbox doesn't exist..." + e);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Active checkbox doesn't exist...Now failing the test " + e);
            }
            return false;
        }
    }

    public boolean manageCompaniesForUser() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 6);
            Thread.sleep(2500);
            extentTest.log(LogStatus.INFO,"Verifying if User has Manage Companies on their Assignment");
            boolean elementExist = driver.findElements(By.xpath("//*[text()='Manage Companies']")).size() > 0;
            try {
                if (elementExist == false) {
                    driver.findElement(By.xpath("//*[@name='editPermSetAssignments']")).click();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//option[@title='Manage Companies']"))).click();
                    //Select dropDown = new Select(element);
                    //dropDown.selectByVisibleText("Manage Companies");
                    driver.findElement(By.xpath("//*[@class='rightArrowIcon']")).click();
                    driver.findElement(By.xpath("//*[@value='Save']")).click();
                }
                return true;

            } catch (Exception e) {
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL, "Unable to Click on Element..." + e);
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Unable to click on Element...Now failing the test "+e);
                }
                return false;
            }
    }

    public boolean elementVerification(String locator, String elementExist, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
            WebDriverWait wait = new WebDriverWait(driver, 7);
            Thread.sleep(600);
        if(elementExist.toLowerCase().equals("yes")) {
            //driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            try{
                if(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).isDisplayed()){
                    extentTest.log(LogStatus.PASS,"Element " + elementName + " exist as expected...");
                    return true;
                } else {
                    scrollIntoView(locator);
                    Thread.sleep(800);
                    extentTest.log(LogStatus.FAIL,"Element" + elementName + " doesn't exist...");
                    getScreenShot(driver);
                    stopExtentReport();
                    if (stopBrowserOnAssertion) {
                        throw new AssertionAndStopTestError("Element " + elementName + " doesn't exist...Now failing the test");
                    }
                    return false;
                }
            } catch (Exception e){
                //scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " not found... " + e);
                getScreenShot(driver);
                //stopExtentReport();
                /*if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " not found... " + e);
                }*/
                return false;
            }
        } else {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            try{
                driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
                driver.findElement(By.xpath(locator)).getText();
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " found...");
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " found... ");
                }
                return false;
            } catch (Exception e){
                extentTest.log(LogStatus.PASS, "Element " + elementName + " is not found as expected");
                return true;
            }
        }
    }

    public boolean elementVerificationByIndex (String locator, int indexNumber, String elementExist, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null, loc = null;
        WebDriverWait wait = new WebDriverWait(driver, 7);
        Thread.sleep(600);
        if(elementExist.toLowerCase().equals("yes")) {
            driver.manage().timeouts().implicitlyWait(4, TimeUnit.SECONDS);
            if (driver.findElements(By.xpath(locator)).get(indexNumber).isDisplayed()){
                extentTest.log(LogStatus.PASS, "Element " + elementName + " exist as expected");
                return true;
            } else {
                //scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " doesn't exist. Now Failing the test...");
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " doesn't exist. Now Failing the test...");
                }
                return false;
            }
        } else {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            if (driver.findElements(By.xpath(locator)).size() == 0) {
                extentTest.log(LogStatus.PASS, "Element " + elementName + " doesn't exist as expected");
                return true;
            } else {
                scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " exist. Now Failing the test...");
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " exist. Now Failing the test...");
                }
                return false;
            }
        }
    }


    public boolean elementVerificationByRow(String rowNumber, String locator, String elementExist, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Thread.sleep(2000);
        String loc = null;
        if(locator.startsWith("//*[")) {
             loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
        } else if(locator.startsWith("[@data")){
            loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
        }
        if(elementExist.toLowerCase().equals("yes")) {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            if(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(loc))).isDisplayed() && driver.findElements(By.xpath(loc)).size() > 0) {
                extentTest.log(LogStatus.PASS, "Element " + elementName + " exist as expected");
                return true;
            } else {
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " doesn't exist. Now Failing the test...");
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " doesn't exist. Now Failing the test...");
                }
                return false;
            }
        } else {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            if (driver.findElements(By.xpath(loc)).size() == 0) {
                extentTest.log(LogStatus.PASS, "Element " + elementName + " doesn't exist as expected");
                return true;
            } else {
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " exist. Now Failing the test...");
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " exist. Now Failing the test...");
                }
                return false;
            }
        }
    }

    public boolean elementVerificationByRowAndIndex(String rowNumber, int index, String locator, String elementExist, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        WebDriverWait wait = new WebDriverWait(driver, 10);
        Thread.sleep(2000);
        String loc = null;
        if(locator.startsWith("//*[")) {
            loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
        } else if(locator.startsWith("[@data")){
            loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
        }
        if(elementExist.toLowerCase().equals("yes")) {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            if(wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(loc))).get(index).isDisplayed()) {
                extentTest.log(LogStatus.PASS, "Element " + elementName + " exist as expected");
                return true;
            } else {
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " doesn't exist. Now Failing the test...");
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " doesn't exist. Now Failing the test...");
                }
                return false;
            }
        } else {
            driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
            //wait = new WebDriverWait(driver, 4);
            Thread.sleep(3000);
            if(!driver.findElements(By.xpath(loc)).get(index).isDisplayed()) {
                extentTest.log(LogStatus.PASS, "Element " + elementName + " doesn't exist as expected");
                return true;
            } else {
                extentTest.log(LogStatus.FAIL, "Element " + elementName + " exist. Now Failing the test...");
                Thread.sleep(800);
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Element " + elementName + " exist. Now Failing the test...");
                }
                return false;
            }
        }
    }

    public boolean objectVerificationByIndex(String locator, int indexNumber, String value, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        try{
            WebDriverWait wait = new WebDriverWait(driver, 8);
            check = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(locator))).get(indexNumber).getText();
            /*if(check.contains("[Change]")){
              String[] arrayCheck = check.split(" \\[");
              check2 = arrayCheck[0];
            } else {
                check2 = check;
            }*/
            //Assert.assertEquals(check,value);
            if(check.equals(value)){
                System.out.println("Text value - " + value + " exist as expected");
                extentTest.log(LogStatus.PASS,"Text value - " + value + " exist as expected on " + elementName);
                return true;
            } else {
                System.out.println("Fail - Expected text '" + value + "' doesn't match the actual text '" + check + "'");
                scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL,"Expected text '" + value + "' doesn't match the actual text '" + check + "' on " + elementName);
                getScreenShot(driver);
               //stopExtentReport();
                /*if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Expected "+ value + " doesn't match with actual " + check + "...Now failing the test");
                }*/
                return false;
            }
        } catch (Exception e) {
            System.out.println("Fail - Verification object is not present/visible");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }
    }


    public boolean verifyActualizedMonthOnListOfOpp(String locator, String elementName) throws InterruptedException {
        String text = null;
        String month = null;
        String desiredDate = getDesiredDateInFormat(0,"/");
        String[]arrayDate = desiredDate.split("/");
        int dateFromat = Integer.parseInt(arrayDate[0]);
        SimpleDateFormat format = new SimpleDateFormat(("MMM"));
        Calendar cal = Calendar.getInstance();
        if (dateFromat >= 12) {
            cal.add(Calendar.MONTH, 0);
            month = format.format(cal.getTime());
        } else {
            cal.add(Calendar.MONTH, -1);
            month = format.format(cal.getTime());
        }
        WebDriverWait wait = new WebDriverWait(driver, 8);
        try {
           text =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))).getText();
            extentTest.log(LogStatus.INFO,"Verifying Accurate Month appears for current year for " + elementName);
           if(text.contains(month) && text.contains(arrayDate[2])){
               extentTest.log(LogStatus.PASS,"Expected value - " + month + " with current year matches with actual value for element " + elementName);
               return true;
           } else {
               scrollIntoView(locator);
               Thread.sleep(800);
               extentTest.log(LogStatus.FAIL,"Expected value - " + month + " doesn't match with actual value " + text + " for element " + elementName);
               getScreenShot(driver);
               stopExtentReport();
               if (stopBrowserOnAssertion) {
                   throw new AssertionAndStopTestError("Expected "+ month + " doesn't match with actual " + text + "...Now failing the test");
               }
               return false;
           }
        } catch (Exception e) {
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible for element " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }

    }

    public boolean objectVerificationByRow(String locator, String rowNumber, String value, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        String loc = null;
        try{
            WebDriverWait wait = new WebDriverWait(driver, 8);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }

            check = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(loc))).getText();
            /*if(check.contains("[Change]")){
              String[] arrayCheck = check.split(" \\[");
              check2 = arrayCheck[0];
            } else {
                check2 = check;
            }*/
            //Assert.assertEquals(check,value);
            if(check.equals(value)){
                System.out.println("Text value - " + value + " exist as expected");
                extentTest.log(LogStatus.PASS,"Text value - " + value + " exist as expected on " + elementName);
                return true;
            } else {
                System.out.println("Fail - Expected text '" + value + "' doesn't match the actual text '" + check + "'");
                scrollIntoViewByRow(rowNumber,locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL,"Expected text '" + value + "' doesn't match the actual text '" + check + "' on " + elementName);
                getScreenShot(driver);
                //stopExtentReport();
                /*if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Expected "+ value + " doesn't match with actual " + check + "...Now failing the test");
                }*/
                return false;
            }
        } catch (Exception e) {
            System.out.println("Fail - Verification object is not present/visible");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean textNotEmpty(String locator, String rowNumber,String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        String loc = null;
        try{
            WebDriverWait wait = new WebDriverWait(driver, 8);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }

            check = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(loc))).getAttribute("data-api");
            if(check.isEmpty()){
                System.out.println("Fail - Expected text '" + check + "is empty");
                scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL,"Expected text '" + check + "is empty for element " + elementName);
                getScreenShot(driver);
                stopExtentReport();
                if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Expected text '" + check + "is empty for element " + elementName + "...Now failing the test");
                }
                return false;
            } else {
                System.out.println("Text value - " + check + " exist as expected");
                extentTest.log(LogStatus.PASS,"Text value - " + check + " exist as expected on " + elementName);
                return true;
            }
        } catch (Exception e) {
            System.out.println("Fail - Verification object is not present/visible");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible on " + elementName + "..." + e);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean objectVerificationByRowAndSchedule(String locator, String rowNumber, String mnthlySchedule, String value, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null;
        String loc = null;
        try{
            WebDriverWait wait = new WebDriverWait(driver, 8);
            if(locator.startsWith("//*[")){
                loc = locator.replace("//*[","//*[@data-row='"+rowNumber+"' and @data-monthly-schedule='"+mnthlySchedule.toLowerCase()+"' and ");
            } else if(locator.startsWith("[@data")) {
                loc = locator.replace("[@data","//*[@data-row='"+rowNumber+"' and @data-monthly-schedule='"+mnthlySchedule.toLowerCase()+"' and @data");
            }
            check = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(loc))).getText();
            if(check.equals(value)){
                System.out.println("Text value - " + value + " exist as expected");
                extentTest.log(LogStatus.PASS,"Text value - " + value + " exist as expected on " + elementName);
                return true;
            } else {
                System.out.println("Fail - Expected text '" + value + "' doesn't match the actual text '" + check + "'");
                scrollIntoView(locator);
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL,"Expected text '" + value + "' doesn't match the actual text '" + check + "' on " + elementName);
                getScreenShot(driver);
                //stopExtentReport();
               /* if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Expected "+ value + " doesn't match with actual " + check + "...Now failing the test");
                }*/
                return false;
            }
        } catch (Exception e) {
            System.out.println("Fail - Verification object is not present/visible");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }
    }



    public boolean objectVerificationByRowAndIndex(String locator, String rowNumber, int indexNumber, String value, String elementName) throws InterruptedException, IOException {
        String check = null, check2 = null, loc = null;
        try{
            WebDriverWait wait = new WebDriverWait(driver, 10);
            if(locator.startsWith("//*[")) {
                loc = locator.replace("//*[", "//*[@data-row='" + rowNumber + "' and ");
            } else if(locator.startsWith("[@data")){
                loc = locator.replace("[@data", "//*[@data-row='" + rowNumber + "' and @data");
            }
            check = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(loc))).get(indexNumber).getText();
            if(check.equals(value)){
                System.out.println("Text value - " + value + " exist as expected");
                extentTest.log(LogStatus.PASS,"Text value - " + value + " exist as expected on " + elementName);
                return true;
            } else {
                System.out.println("Fail - Expected text '" + value + "' doesn't match the actual text '" + check + "'");
                Thread.sleep(800);
                extentTest.log(LogStatus.FAIL,"Expected text '" + value + "' doesn't match the actual text '" + check + "' on " + elementName);
                getScreenShot(driver);
                //stopExtentReport();
                /*if (stopBrowserOnAssertion) {
                    throw new AssertionAndStopTestError("Expected "+ value + " doesn't match with actual " + check + "...Now failing the test");
                }*/
                return false;
            }
        } catch (Exception e) {
            System.out.println("Fail - Verification object is not present/visible");
            Thread.sleep(800);
            extentTest.log(LogStatus.FAIL,"Verification object is not present/visible on " + elementName);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to verify element now failing the test - " + e);
            }
            return false;
        }
    }

    public static String getDesiredDateInFormat(int days, String by) {
        DateFormat dateFormat = new SimpleDateFormat("dd"+by+"MM"+by+"YYYY");
        dateFormat.setLenient(true);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        String date1 = dateFormat.format(calendar.getTime());
        return date1;
    }

    public static String getDesiredDateInFormat(int days, String by, String byYrMntDay) {
        DateFormat dateFormat = null;
        if(byYrMntDay.equalsIgnoreCase("mm")){
            dateFormat = new SimpleDateFormat("MM"+by+"dd"+by+"YYYY");
        } else if(byYrMntDay.equalsIgnoreCase("yyyy")){
            dateFormat = new SimpleDateFormat("YYYY"+by+"MM"+by+"dd");
        } else {
            dateFormat = new SimpleDateFormat("dd"+by+"MM"+by+"YYYY");
        }

        dateFormat.setLenient(true);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        String date1 = dateFormat.format(calendar.getTime());
        return date1;
    }

    public static String getDesiredDateInFormat(int days, String by, String byYrMntDay, String leadingZero) {
        DateFormat dateFormat = null;
        if(byYrMntDay.equalsIgnoreCase("mm")){
            dateFormat = new SimpleDateFormat("MM"+by+"dd"+by+"YYYY");
        } else if(byYrMntDay.equalsIgnoreCase("yyyy")){
            dateFormat = new SimpleDateFormat("YYYY"+by+"MM"+by+"dd");
        } else {
            dateFormat = new SimpleDateFormat("dd"+by+"MM"+by+"YYYY");
        }

        dateFormat.setLenient(true);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, days);
        String date1 = dateFormat.format(calendar.getTime());
        return date1;
    }

    public boolean quit() throws InterruptedException, IOException {
        //driver.close();
        //driver.quit();
        Thread.sleep(700);
        driver.quit();
        return true;
    }

    public boolean close() throws InterruptedException, IOException {
        //driver.close();
        //driver.quit();
        Thread.sleep(700);
        driver.close();
        return true;
    }

    public static void handlingLogOutSession(String userName, String password, String oppUrl) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        Thread.sleep(2500);
        if(driver.findElements(By.xpath("//*[@id='username']")).size() > 0){
            driver.findElement(By.xpath("//*[@id='username']")).sendKeys(userName);
            driver.findElement(By.xpath("//*[@id='password']")).sendKeys(password);
            driver.findElement(By.xpath("//*[@id='Login']")).click();
            Thread.sleep(1000);
            driver.navigate().to(oppUrl);
            Thread.sleep(1000);
            scrollIntoView("//*[@value='Submit for Approval']");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='Submit for Approval']"))).click();
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } else {

        }

    }

    public boolean cancelAlert() throws InterruptedException {
        try{
            extentTest.log(LogStatus.INFO,"Dismissing Alert pop up");
            Thread.sleep(2800);
            Alert alert = driver.switchTo().alert();
            alert.dismiss();
            return true;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to cancel Alert pop up " + e);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            return false;
        }
    }


    public boolean selectDropDownByText(String locator, String text, String elementName) throws IOException, InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))));
        try{
            extentTest.log(LogStatus.INFO,"selecting value " + text + " on " + elementName);
            select.selectByVisibleText(text);
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to select an element by text");
            extentTest.log(LogStatus.FAIL,"Unable to select element from " + elementName);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                Thread.sleep(800);
                throw new AssertionAndStopTestError("Unable to select from drop down by text now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean verifyStateOfCheckBox(String trueFalse, String locator, int indexNumber,String checkBoxName) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 6);
        boolean returnTrueFalse = true;
        try{
            extentTest.log(LogStatus.INFO,"Verify State of Check Box " + checkBoxName );
            boolean elementSelected = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(locator))).get(indexNumber).isSelected();
            if(trueFalse.equalsIgnoreCase("true")){
                if(elementSelected == true){
                    extentTest.log(LogStatus.PASS,"Element " + checkBoxName + " is selected");
                    returnTrueFalse = true;
                    return returnTrueFalse;
                } else {
                    scrollIntoViewByIndex(locator,0);
                    extentTest.log(LogStatus.FAIL,"Element " + checkBoxName + " is not selected " + elementSelected);
                    Thread.sleep(800);
                    getScreenShot(driver);
                    stopExtentReport();
                    returnTrueFalse = false;
                    return returnTrueFalse;
                }
            } else if(trueFalse.equalsIgnoreCase("false")){
                if(elementSelected == false){
                    extentTest.log(LogStatus.PASS,"Element " + checkBoxName + " is not selected");
                    returnTrueFalse = true;
                    return returnTrueFalse;
                } else {
                    extentTest.log(LogStatus.FAIL,"Element " + checkBoxName + " is selected " + elementSelected);
                    Thread.sleep(800);
                    getScreenShot(driver);
                    stopExtentReport();
                    returnTrueFalse = false;
                    return returnTrueFalse;
                }
            }
            return returnTrueFalse;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to located checkbox " + checkBoxName);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                Thread.sleep(800);
                throw new AssertionAndStopTestError("Unable to located checkbox " + checkBoxName + "..." + e);
            }
            return false;
        }

    }



    public boolean selectDropDownByIndex(String locator, int index, String elementName) throws IOException, InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, 3);
        Select select = new Select(wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(locator))));
        try{
            extentTest.log(LogStatus.INFO,"selecting value index of " + index + " on " + elementName);
            select.selectByIndex(index);
            return true;
        } catch (Exception e) {
            System.out.println("Fail - Unable to select an element by index");
            extentTest.log(LogStatus.FAIL,"Unable to select element from " + elementName);
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                Thread.sleep(800);
                throw new AssertionAndStopTestError("Unable to select from drop down by index now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean selectDropDownByValue(String locator, String value, String pageName) throws IOException, InterruptedException {
        Select select = new Select(driver.findElement(By.xpath(locator)));
        try{
            select.selectByValue(value);
            return true;
        } catch (Exception e) {
            Thread.sleep(800);
            takeErrorScreenShots(pageName + "_DropDownByValueIssue");
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to select from drop down by value now failing the test - " + e);
            }
            return false;
        }
    }

    public boolean wait(int miliSec) throws InterruptedException, IOException {

        try{
            Thread.sleep(miliSec);
            return true;
        } catch (Exception e) {
            Thread.sleep(800);
            takeErrorScreenShots( "waitIssue");
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to wait with Thread.sleep " + e);
            }
            return false;
        }
    }

    public static String getCalculatedValue(String rateType, String valueType, int calc1, int calc2){
        float value = 0;
        String formatValue = null;
        double roundOff;
        int budget = 0, rate = 0, volume = 0;
        DecimalFormat df = new DecimalFormat("#,###");
        if(rateType.toUpperCase().equals("CPM")){
            if (valueType.toUpperCase().equals("VOLUME CALCULATION")) {
                budget = calc1;
                rate = calc2;
                value = (float)budget/rate*1000;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("RATE CALCULATION")) {
                budget = calc1;
                volume = calc2;
                value = (float)budget/volume*1000;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("BUDGET CALCULATION")) {
                volume = calc1;
                rate = calc2;
                value = (float)volume*rate/1000;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
        }
        if(rateType.toUpperCase().equals("CPC")){
            if (valueType.toUpperCase().equals("VOLUME CALCULATION")) {
                budget = calc1;
                rate = calc2;
                value = (float)budget/rate;
                roundOff = Math.round(value);
                System.out.print(roundOff);
                formatValue = df.format(roundOff);
                System.out.println(formatValue);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("RATE CALCULATION")) {
                budget = calc1;
                volume = calc2;
                value = (float)budget/volume;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("BUDGET CALCULATION")) {
                volume = calc1;
                rate = calc2;
                value = (float)volume*rate;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
        }
        return formatValue;
    }

    public static String getCalculatedValue(String rateType, String valueType, int calc1, int calc2, String locale){
        float value = 0;
        String formatValue = null;
        double roundOff;
        int budget = 0, rate = 0, volume = 0;
        DecimalFormat df = new DecimalFormat("#,###");
        if(rateType.toUpperCase().equals("CPM")){
            if (valueType.toUpperCase().equals("VOLUME CALCULATION")) {
                budget = calc1;
                rate = calc2;
                value = (float)budget/rate*1000;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("RATE CALCULATION")) {
                budget = calc1;
                volume = calc2;
                value = (float)budget/volume*1000;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("BUDGET CALCULATION")) {
                volume = calc1;
                rate = calc2;
                value = (float)volume*rate/1000;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
        }
        if(rateType.toUpperCase().equals("CPC")){
            if (valueType.toUpperCase().equals("VOLUME CALCULATION")) {
                budget = calc1;
                rate = calc2;
                value = (float)budget/rate;
                roundOff = Math.round(value);
                System.out.print(roundOff);
                formatValue = df.format(roundOff);
                System.out.println(formatValue);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("RATE CALCULATION")) {
                budget = calc1;
                volume = calc2;
                value = (float)budget/volume;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
            if (valueType.toUpperCase().equals("BUDGET CALCULATION")) {
                volume = calc1;
                rate = calc2;
                value = (float)volume*rate;
                roundOff = Math.round(value);
                formatValue = df.format(roundOff);
                return formatValue;
            }
        }
        return formatValue;
    }

    public By ByLocator(String locator) {
        // To get By Class value
        String[] array = locator.split("~");
        if (array[0] == "css" ) {return By.cssSelector(array[1]); }
        else if (array[0] == "id" ) {return By.id(array[1]); }
        else if (array[0] == "name" ) {return By.name(array[1]); }
        else if (array[0] == "link" ) {return By.partialLinkText(array[1]); }
        else if (array[0] == "xpath" ) {return By.partialLinkText(array[1]); }
        return null;

    }

    static DateFormat dateFormat = new SimpleDateFormat("yyMMdd-hhmm");
    public static String today =  dateFormat.format(new Date());
    public String getUniqueName(String name) {

        return name + "-" + today;
    }

    public static void takeErrorScreenShots(String screenShotName) throws IOException {
        String returnlFile = null;
        String[] arrayScreenShot = null;
        try{
            String fileName = screenShotName + "_" + shortUUID() + ".png";
            arrayScreenShot = screenShotName.split("_");
            String directory = "FitNesseRoot/files/testResults/ScreenShots/"+ arrayScreenShot[0]+"/";
            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(sourceFile, new File(directory + fileName));
            returnlFile =  directory + fileName;
        } catch (Exception e) {

        }
    }

    public boolean getScreenShot(String path){
        try {
            File scrnsht = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrnsht, new File(path + "ScreenShot" + shortUUID() + ".png"));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void executorOpenWindow(String url){
        JavascriptExecutor executor = (JavascriptExecutor)driver;
        executor.executeScript("window.open(arguments[0],'_blank');", url);
    }

    /**************
     * switching to New Browser window
     **************/
    public String getLookUpTitle() throws IOException, InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver,7);
        String title= null;
        try {
            Set<String> windowId = driver.getWindowHandles();    // get  window id of current window
            Iterator<String> itererator = windowId.iterator();

            String mainWinID = itererator.next();
            String  newAdwinID = itererator.next();

            driver.switchTo().window(newAdwinID);
            Thread.sleep(1000);
            title = driver.getTitle();
            return  title;
        } catch (Exception e){
            Thread.sleep(800);
            takeErrorScreenShots( "SwitchToTabIssue");
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to get title for new brower window " + e);
            }
            return title;
        }


    }

    /**************
     * switching to child windows
     **************/
    public boolean switchToNewWindow(int windowNumber) throws InterruptedException, IOException {
        try {
            Set<String> s = driver.getWindowHandles();
            Iterator<String> ite = s.iterator();
            int i = 1;
            while (ite.hasNext() && i < 10) {
                String popupHandle = ite.next().toString();
                driver.switchTo().window(popupHandle);
                System.out.println("Window title is : " + driver.getTitle());
                if (i == windowNumber) break;
                i++;
            }
            return true;
        } catch (Exception e) {

            Thread.sleep(800);
            takeErrorScreenShots( "SwitchToTabIssue");
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to switch to Tab " + windowNumber + " " + e);
            }
            return false;

        }
    }

    public boolean closeChildWindows() throws InterruptedException, IOException {
        try{

            String originalHandle = driver.getWindowHandle();

            //Do something to open new tabs

            for(String handle : driver.getWindowHandles()) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    driver.close();
                }
            }
            driver.switchTo().window(originalHandle);
            return true;
        } catch (Exception e) {

            Thread.sleep(800);
            takeErrorScreenShots( "SwitchToTabIssue");
            if (stopBrowserOnAssertion) {
                throw new AssertionAndStopTestError("Unable to close child windows " + e);
            }
            return false;

        }
    }

    public String stopTestOnFirstFailure(String shouldStop){
        shouldStop = "true";
        if(!shouldStop.equals("true")){
            shouldStop = "false";
        }
        return shouldStop;

    }


    static String geturl1 = "Staging";
    static DateFormat dateFormat11 = new SimpleDateFormat("yyMMdd-hhmm");
    public static String today11 =  dateFormat11.format(new Date());
    static String strFrameworkLocation1 =System.getProperty("user.dir");
    public static String reportsPath1 = "FitNesseRoot/files/testResults/ExtentReport/" + geturl1.toUpperCase() + "/";
    static String filePath1 = reportsPath1;
    public static ExtentReports ExtentReports(String TestName) {

        if (Path == null) {
            //filePath = reportsPath + "TestScenario"+getDateTime()+"//" + getDateTime();
            String[] arrayTest = TestName.split("_");
            //Path = filePath1 + arrayTest[0] + "/" + arrayTest[1] + ".html";
            Path = filePath1 + "AutomationReport" + today11 + ".html";
            File file = new File(Path);
            //File screenshotsDirectory = new File(reportsPath + "Screenshots//");
            try {
                if (file.exists()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        extentReport = new ExtentReports(Path, false);
        return extentReport;
    }


    public static void getScreenShot(WebDriver wDriver) {
        String[] testArray = null;
        //testArray = testName.split("_");
        try {
            String fileName = "ScreenShot" + shortUUID() + ".png";
            String directory1 = filePath1 + "/Screenshots/" + fileName;
            File sourceFile = ((TakesScreenshot) wDriver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(sourceFile, new File(directory1));
            //String imgPath = directory + fileName;
            Thread.sleep(400);
            String image = extentTest.addScreenCapture("Screenshots//" + fileName);
            extentTest.log(LogStatus.FAIL, "", image);
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL, "Error Occured while taking SCREENSHOT!!!");
            e.printStackTrace();
        }
    }

    public String getCurrentUrl() throws InterruptedException {
        String url = null;
        try{
            extentTest.log(LogStatus.INFO,"Capturing current page url");
            url = driver.getCurrentUrl();
            return url;
        } catch (Exception e) {
            extentTest.log(LogStatus.FAIL,"Unable to capture the current url");
            Thread.sleep(800);
            getScreenShot(driver);
            stopExtentReport();
            if (stopBrowserOnAssertion) {
                Thread.sleep(800);
                throw new AssertionAndStopTestError("Unable to get current rul... Now failing the test - " + e);
            }
            return url;
        }
    }

    /**************
     * Convert milliseconds to HHmmss
     ************/
    public static String convertSecondsToHMmSs(long milliSeconds) {
        long seconds = milliSeconds/1000;
        long s = seconds % 60;
        long m = (seconds / 60) % 60;
        long h = (seconds / (60 * 60)) % 24;
        return String.format("%d:%02d:%02d", h,m,s);
    }

    public static void stopExtentReport() throws InterruptedException {
        testEndTime = System.currentTimeMillis();
        extentTest.log(LogStatus.INFO, "Total Execution Time for Test Suite: : " + convertSecondsToHMmSs(testEndTime - testStartTime));
        Reporter.log("TOTAL TEST EXECUTION TIME OF WHOLE SUITE: " + convertSecondsToHMmSs(suiteEndTime - suiteStartTime));
        extentTest.log(LogStatus.INFO, "********************* END OF AUTOMATION TEST *********************");
        extentTest.log(LogStatus.INFO, "TEST RESULT SAVED IN: " + Path);
        extentReport.endTest(extentTest);
        extentReport.flush();
        //Thread.sleep(800);
        //String[] testArray = testName.split("_");
        //driver.get("http://localhost:8081/files/testResults/ExtentReport/" + geturl1 + "/"+ testArray[0] + "/" + testArray[1] + ".html");
    }

    public static void stopPassedExtentReport() throws InterruptedException {
        testEndTime = System.currentTimeMillis();
        extentTest.log(LogStatus.INFO, "Total Execution Time for Test Suite: : " + convertSecondsToHMmSs(testEndTime - testStartTime));
        Reporter.log("TOTAL TEST EXECUTION TIME OF WHOLE SUITE: " + convertSecondsToHMmSs(suiteEndTime - suiteStartTime));
        extentTest.log(LogStatus.INFO, "********************* END OF AUTOMATION TEST *********************");
        extentTest.log(LogStatus.INFO, "TEST RESULT SAVED IN: " + Path);
        extentReport.endTest(extentTest);
        extentReport.flush();
        //Thread.sleep(800);
        //String[] testArray = testName.split("_");
        //driver.get("http://localhost:8081/files/testResults/ExtentReport/" + geturl1 + "/"+ testArray[0] + "/" + testArray[1] + ".html");
    }



    public boolean waitForManageBoardToLoad() throws InterruptedException, IOException {
        int boardLoadTimeout = 20;
        boolean elemExist = true;
        WebDriverWait wait = new WebDriverWait(driver, 60);
        String element = (wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'blockUI')]")))).getCssValue("background-color");
        extentTest.log(LogStatus.INFO,"Element color during board loading is " + element);
        int i = 0;
        int j = 0;
        extentTest.log(LogStatus.INFO,"Waiting for Board to be Online.....");
        while (element.equalsIgnoreCase("rgb(0,0,0)")){
            element = (wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(@class,'blockUI')]")))).getCssValue("background-color");;
            Thread.sleep(4000);
                if (j > boardLoadTimeout) {
                    extentTest.log(LogStatus.FAIL, "Board is not online after " + boardLoadTimeout + " seconds. Exiting the test!!!");
                    Thread.sleep(800);
                    takeErrorScreenShots( "BoardLoadIssue");
                    if (stopBrowserOnAssertion) {
                        throw new AssertionAndStopTestError("Board is not online after " + boardLoadTimeout + " seconds. Exiting the test!!!");
                    }
                    elemExist = false;
                    break;
                }
            elemExist = true;
            i++;
            j ++;
        }
        extentTest.log(LogStatus.INFO,"SmartBoard Loaded Successfully: Element Color is Green");
        Thread.sleep(6000);
        return elemExist;
    }






}

