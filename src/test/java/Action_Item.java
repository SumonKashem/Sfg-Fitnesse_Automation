/*
import Utilities.Reusable_Methods_Loggers;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Action_Item {


   */
/* @Test case 1
    you will define logger1 = report.startTest("Proceed to Check out for Tshirts"). you will use logger 1 for this
    1. navigate to http://automationpractice.com/index.php
    2. Verify the page title displays as My-Store //use if else with log.PASS and log.FAIL
    3. using mouseHover method hover over Women tab
    4. click on T-shirts link from there
    5. scroll down about 350 times on next page
    6. now hover over the picture with women in it
    7. click on add to cart button
    8. on the pop up using if else verify the message appears '
    Product successfully added to your shopping cart'
    9. click on proceed to checkout button
    10. change the quantity to 3 items
    11. click on proceed to check out*//*



   */
/* now @Test case 2 will be depending on test case 1
    you will start another logger saying
    logger2 = report.startTest("Proceed to Checkout for Summer Dresses")
    1. Hover over Dresses tab
    2. click on Summer Dresses
    3. scroll down about 250 to 300 times
    4. hover over first picture of the dress
    5. click on More tab
    6. change the quantity to 4
    7. select a size from dropdown(S,M or L)
    8. click on 'Add to Car' button
    9. on pop up verify checkpoint says Product successfully added to your shopping cart using if else condition with logger.pass and fail
    10. click on proceed to checkout
    11. next page click on delete icon to delete the item
    12. on next page verify following message appears using if else
    Your shopping cart is empty. *//*



    //declare all the global variables before annotation methods
    WebDriver driver;
    ExtentReports report;
    ExtentTest logger1;
    ExtentTest logger2;



    @BeforeSuite
    public void openBrowser(){
        //define the driver path
        System.setProperty("webdriver.chrome.driver","src\\main\\resources\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--incognito","--disable-infobars");
        driver = new ChromeDriver(options);
        //timeout using implicit wait
        driver.manage().timeouts().implicitlyWait(7,TimeUnit.SECONDS);        //implicit wait
        //define the report path
        report = new ExtentReports("src\\main\\java\\Report_Folder\\ExtentReport.html", true);
    }//end of before suite


    @AfterSuite
    public void closeBrowser(){
        report.endTest(logger1);
        report.endTest(logger2);
        report.flush();
        report.close();
        //driver.quit;
    }//end of after suite


    @Test
    public void testCase1() throws IOException, InterruptedException {
        //start the test
        logger1 = report.startTest("Proceed to Check out for Tshirts");
        //1. navigate to http://automationpractice.com/index.php
        Reusable_Methods_Loggers.navigate(logger1, driver,"http://automationpractice.com/index.php");
        //2. Verify the page title displays as My-Store //use if else with log.PASS and log.FAIL
        String practiceTitle = driver.getTitle();
        if (practiceTitle.equalsIgnoreCase("My Store")){
            logger1.log(LogStatus.PASS,"my store title matches");

        }else {
            logger1.log(LogStatus.FAIL,"my store title doesn't match " + practiceTitle);

        }//end of else

        //3. using mouseHover method hover over Women tab
        Reusable_Methods_Loggers.mouseHover(logger1,driver,"//*[@title='Women']","Women Tab");
        Thread.sleep(1500);
        //4. click on T-shirts link from there
        Reusable_Methods_Loggers.clickMethod(logger1, driver, "//*[@title='T-shirts']", "T-Shirt");
        //5. scroll down about 350 times on next page
        //define javascript executor
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        //scroll to the bottom of the page
        logger1.log(LogStatus.INFO,"Scroll down about 350 on next page");
        Thread.sleep(1500);
        jse.executeScript("scroll(0,350)");

        //6. now hover over the picture with women in it
        Reusable_Methods_Loggers.mouseHover(logger1,driver,"//*[@title='Faded Short Sleeve T-shirts']","picture");

        //7. click on add to cart button
        Reusable_Methods_Loggers.clickMethod(logger1,driver,"//*[@title='Add to cart']","Add to Cart button");
        Thread.sleep(2000);
        //8. on the pop up using if else verify the message appears 'Product successfully added to your shopping cart'
        String popUpMessage = driver.findElement(By.xpath("//*[@id='layer_cart']/div[1]/div[1]/h2")).getText();
        if (popUpMessage.equalsIgnoreCase("Product successfully added to your shopping cart")) {
            logger1.log(LogStatus.PASS, "Product successfully added to your shopping cart matches");
        } else {
            logger1.log(LogStatus.FAIL, "Product successfully added to your shopping cart doesn't match " + popUpMessage);
            Reusable_Methods_Loggers.getScreenshot(driver,logger1,"Pop up Message");
        }//end of else

        //9. click on proceed to checkout button
        Thread.sleep(1500);
        Reusable_Methods_Loggers.clickMethod(logger1,driver,"//*[@rel='nofollow' and @title='Proceed to checkout']","Checkout button");
        //10. change the quantity to 3 items
        Reusable_Methods_Loggers.clearMethod(logger1,driver,"//*[@size='2']","Quantity");
        Reusable_Methods_Loggers.sendKeysMethod(logger1,driver,"//*[@type='text']","3","quantity");
        //11. click on proceed to check out
        Reusable_Methods_Loggers.clickMethod(logger1,driver,"//*[@class='button btn btn-default standard-checkout button-medium']","Proceed to checkout");
    }//end of test


    @Test (dependsOnMethods = "testCase1")
    public void TestCase2() throws IOException, InterruptedException {
        //start the test
        logger2 = report.startTest("Proceed to Checkout for Summer Dresses");
        //1. Hover over Dresses tab
        Reusable_Methods_Loggers.mouseHover(logger2,driver,"//*[@class='sf-with-ul']","Dresses Tab");
        //2. click on Summer Dresses
        Reusable_Methods_Loggers.clickMethod(logger2, driver, "//*[@title='Summer Dresses']", "summer dresses");
        //3. scroll down about 250 to 300 times
        //define javascript executor
        JavascriptExecutor jse = (JavascriptExecutor)driver;
        //scroll to the bottom of the page
        logger2.log(LogStatus.INFO,"Scroll down about 330 on next page");
        Thread.sleep(1500);
        jse.executeScript("scroll(0,300)");
        //4. hover over first picture of the dress
        Reusable_Methods_Loggers.mouseHover(logger2,driver,"//*[@title='Printed Summer Dress']","Hover over the first picture");
        //5. click on More tab
        Reusable_Methods_Loggers.clickMethod(logger2, driver, "//*[@class='button lnk_view btn btn-default']", "More Tab");
        //6. change the quantity to 4
        Thread.sleep(2000);
        Reusable_Methods_Loggers.clearMethod(logger2,driver,"//*[@name='qty']","clear the items");
        Reusable_Methods_Loggers.sendKeysMethod(logger2,driver,"//*[@name='qty']","4","quantity");
        //7. select a size from dropdown(S,M or L)
        Reusable_Methods_Loggers.selectByText(logger2,driver,"//*[@id='group_1']","M","dropdown");
        //8. click on 'Add to Cart' button
        Reusable_Methods_Loggers.clickMethod(logger2,driver,"//*[@type='submit']","Add to Cart button");
        //9. on pop up verify checkpoint says Product successfully added to your shopping cart using if else condition with logger.pass and fail
        String popUpMessage = driver.getTitle();
        if (popUpMessage.equalsIgnoreCase("Product successfully added to your shopping cart")){
            logger2.log(LogStatus.PASS,"Product successfully added to your shopping cart title matches");
        }else {
            logger2.log(LogStatus.FAIL,"Product successfully added to your shopping cart title doesn't match " + popUpMessage);
            Reusable_Methods_Loggers.getScreenshot(driver,logger2,"Pop up Message");
        }//end of else

        //10. click on proceed to checkout
        Reusable_Methods_Loggers.clickMethod(logger2,driver,"//*[@title='Proceed to checkout']","CheckOut");
        //11. next page click on delete icon to delete the item
        Reusable_Methods_Loggers.clickMethod(logger2,driver,"//*[@class='icon-trash']","Delete Icon");
        //12. on next page verify following message appears using if else
        //    Your shopping cart is empty.
        String empty = driver.getTitle();
        if (empty.equalsIgnoreCase("Your shopping cart is empty.")) {
            logger2.log(LogStatus.PASS, "Your shopping cart is empty. matches");
        } else {
            logger2.log(LogStatus.FAIL, "Your shopping cart is empty. doesn't match " + empty);
        }//end of else

    }//end of testcase2




}//end of public class
*/
