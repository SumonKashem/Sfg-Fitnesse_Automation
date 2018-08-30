import com.fitNesse.DriverClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.fitNesse.DriverClass.getDesiredDateInFormat;

public class abc {

        @Test
        public static void test() throws InterruptedException {
            System.setProperty("webdriver.chrome.driver", "C:\\Users\\SumonK\\Desktop\\FitNesseWithSelenium_Copy\\src\\main\\resources\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized", "--disable-extensions");
            WebDriver driver = new ChromeDriver(options);
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            WebDriverWait wait = new WebDriverWait(driver, 7);

            driver.navigate().to("https://test.salesforce.com");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@type='email']"))).sendKeys("sumon.kashem@xaxis.com.glstaging");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@type='password']"))).sendKeys("xaxisAuto#1");
            executor.executeScript("arguments[0].click();", wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='Log In to Sandbox']"))));
            Thread.sleep(3000);
            driver.navigate().to("https://cs64.salesforce.com/a1J0q000000EaF3");
           // Thread.sleep(2000);
            //wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//th[contains(@class,'dataCell')]/a"))).click();
           // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@title='Edit' and @type='button']"))).click();
   

        }


}
