/*
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Test_Vaca {

    WebDriver driver;

    @BeforeSuite
    public void openBrowser(){
        //define the driver path
        System.setProperty("webdriver.chrome.driver","src\\main\\resources\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized", "--incognito","--disable-infobars");
        driver = new ChromeDriver(options);
        //timeout using implicit wait
        driver.manage().timeouts().implicitlyWait(15,TimeUnit.SECONDS);
    }//end of before suite


    @Test
    public void depositrestore() throws InterruptedException, ParseException, IOException {

        driver.navigate().to("https://qab.rci.com/");
        driver.findElement(By.xpath("//*[@id='username']")).sendKeys("edgrider");
        driver.findElement(By.xpath("//*[@name='password']")).sendKeys("resort1");
        driver.findElement(By.xpath("//*[text()='SIGN IN']")).click();
        driver.findElement(By.xpath("//*[text()='MY ACCOUNT']")).click();
        //clicking on available deposit by first index 0 for dashboard
        driver.findElements(By.xpath("//*[@class='account-dashboard-grid-cell']")).get(0).click();

        JavascriptExecutor jse = (JavascriptExecutor)driver;
        Thread.sleep(2000);
        //you also need to scroll so that table is within frame if it is not then your getText will fail to capture
        jse.executeScript("scroll(0,800)");
        Thread.sleep(1000);

        //remember the date is dynamic so i'm using findElements with random command to choose random start data
        //using List to capture the count of start dates tables appear on the page
        List<WebElement> dateCount = driver.findElements(By.xpath("//td[@classs='depNowStartDate']"));
        System.out.println("Start Date table count is " + dateCount.size());
        Random rand = new Random();
        int randomNumber = 0;
        //randomNumber = rand.nextInt(dateCount.size());
        //System.out.println("Random start date index is  " + randomNumber);

        String[] monthArray = new String[]{"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        String[] monthNumArray = new String[]{"01","02","03","04","05","06","07","08","09","10","11","12"};

        String startdate = null;

        for(int i = 0;i < dateCount.size();i++) {
            randomNumber = rand.nextInt(dateCount.size());
            //defining random start date and capturing the text
            startdate = driver.findElements(By.xpath("//td[@classs='depNowStartDate']")).get(randomNumber).getText();
            //if start date is not equals blank
            if (!startdate.equals("-")) {
                //splitting the start date
                String[] arraySplit = startdate.split("-");

                String monthNum = null;
                //getting the accurate month from array to compare
                for(int j = 0;i < monthArray.length;j++){
                    if(arraySplit[1].equalsIgnoreCase(monthArray[j])){
                        monthNum = monthNumArray[j];
                        break;
                    }
                }//end of array for loop

                //new formatted date after changing month from letter to number using loop and if
                String newStartDate = arraySplit[0]+"-"+ monthNum + "-" + arraySplit[2];
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Calendar currentDay = Calendar.getInstance();
                Calendar futureMonth = Calendar.getInstance();
                currentDay.add(Calendar.MONTH, 0);
                futureMonth.add(Calendar.MONTH, 9);
                //converting vaca start date to date format to compare with calendar month
                Date VacaDate = format.parse(newStartDate);

                //inside first if this if will be verified only if the start data is not equal - then if current day and vacadate is before future month then click on Deposit now
                if (currentDay.getTime().before(futureMonth.getTime()) && VacaDate.before(futureMonth.getTime())) {
                    System.out.println("Random start date index is  " + randomNumber);
                    System.out.println("Start date is " + startdate);
                    //clicking on Random deposit now since i put the start date count in random command
                    System.out.println("Clicking on 'Deposit Now' with index " + randomNumber);
                    driver.findElements(By.xpath("//*[@type='button' and contains(@id,'depNow')]")).get(randomNumber).click();
                    System.out.println("Vacation Start Date is within 9 months.. Current Date is " + currentDay.getTime() + " & Vacatation Date is " + VacaDate);
                    break;
                }//end of month if



            }//end of first if
        }//end of first for loop


    }

    @AfterSuite
    public void closeBrowser(){

        driver.quit();
    }


}
*/
