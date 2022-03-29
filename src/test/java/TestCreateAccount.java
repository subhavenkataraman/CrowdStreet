import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestCreateAccount {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCreateAccount.class);

    @Test
    public void testCreateAccount() {

        System.setProperty("webdriver.chrome.driver", "/Users/thirunallathambi/Downloads/chromedriver");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // use timestamp in email for uniqueness
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        String email = "em" + formatter.format(date)  + "@em.com";
        LOGGER.info("Opening Test URL");

        try {
            driver.get("https://test.crowdstreet.com");
            LOGGER.info("Clicking CreateAccount");
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(120));
            wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href*='create-account']"))).click();
            LOGGER.info("Wait until CreateAccount form loads");
            WebElement firstName = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='firstName']")));
            LOGGER.info("Enter Account Info in form");
            firstName.sendKeys("firstname");
            driver.findElement(By.cssSelector("[data-testid='lastName']")).sendKeys("lastname");
            driver.findElement(By.cssSelector("[data-testid='email']")).sendKeys(email);
            driver.findElement(By.cssSelector("[data-testid='password']")).sendKeys("Pass@123");
            driver.findElement(By.cssSelector("[data-testid='confirm-password']")).sendKeys("Pass@123");
            driver.findElement(By.cssSelector("input[data-testid='accreditedOptionYes']")).click();
            driver.findElement(By.cssSelector("input[data-testid='hasAgreedTos']")).click();

            WebElement captchaFrame = driver.findElement(By.xpath("//iframe[starts-with(@name, 'a-') and starts-with(@src, 'https://www.google.com/recaptcha')]"));
            if(captchaFrame.isDisplayed())
            {
                LOGGER.info("Handling Captcha");
                driver.switchTo().frame(captchaFrame);
                WebElement captcha = new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("div.recaptcha-checkbox-checkmark")));
                ((JavascriptExecutor)driver).executeScript("arguments[0].click();", captcha);
                driver.switchTo().parentFrame();
            }

            LOGGER.info("Submit Form");
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[data-testid='submit-button']"))).click();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            LOGGER.info("Verify SIGN OUT button exists to ensure you are logged in");
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[text()='Sign Out']")));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        driver.close();
    }
}
