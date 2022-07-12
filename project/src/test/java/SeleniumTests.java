import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;


public class SeleniumTests {
    private WebDriver driver;
    private WebDriverWait wait;
    private Map<String, Object> vars;
    JavascriptExecutor js;


    /**
     * Сделал всего один тест, который вызывает поочерёдно 4 метода, чтобы лишний раз не логиниться,
     * да не перезапускать драйвер Chrome
     * Местами, конечно, xpath'ы длинноваты, прошу простить, если это плохо. С Selenium'ом работаю впервые плотно,
     * в том числе с автотестами.
     * Там есть разные assert'ы в коде на Equals, разницы в них нет, просто так написалось
     */
    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "./src/chromedriver.exe");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        js = (JavascriptExecutor) driver;
        vars = new HashMap<String, Object>();
    }

    @After
    public void tearDown() {
        driver.quit();
    }


    /**
     * Форма авторизации
     * Просто метод на login, ничего необычного, проверяем на заполнение полей, заполняем will/will, и мы в системе
     */
    public void loginTest() {

        driver.get("https://suite8demo.suiteondemand.com");
        driver.manage().window().fullscreen();
        driver.findElement(By.name("username")).click();
        {
            WebElement element = driver.findElement(By.id("login-button"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element).perform();
        }
        driver.findElement(By.id("login-button")).click();
        {
            WebElement element = driver.findElement(By.tagName("body"));
            Actions builder = new Actions(driver);
            builder.moveToElement(element, 0, 0).perform();
        }
        driver.findElement(By.name("username")).click();
        driver.findElement(By.name("username")).sendKeys("will");
        driver.findElement(By.id("login-button")).click();
        driver.findElement(By.name("password")).click();
        driver.findElement(By.name("password")).sendKeys("will");
        driver.findElement(By.id("login-button")).click();
        assertNotNull(driver.findElement(By.className("home-nav-link")));
    }


    /**
     * Форма создания предварительного контакта (Leads)
     */
    public void leadTest() {
        /*Ждем пока страница прогрузится*/
        wait.until(ExpectedConditions.urlToBe("https://suite8demo.suiteondemand.com/#/home"));
        /*Заходим в Lead, начинаем создание нового предконтакта*/
        driver.findElement(By.xpath("//a[contains(text(),\'Leads\')]")).click();
        driver.findElement(By.cssSelector(".ng-star-inserted:nth-child(1) > .action-button")).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".ng-star-inserted:nth-child(1) > .button-group-button")));
        /*Проверяем, есть ли проверка от пустые поля*/
        driver.findElement(By.xpath("//button[contains(.,'Save')]")).click();
        driver.findElement(By.xpath("//scrm-message-ui/div/div/div")).click();
        {
            List<WebElement> elements = driver.findElements(By.cssSelector(".is-invalid"));
            assertTrue(elements.size() > 0);
        }
        /*Записываем новые данные в lastName и phoneNumber, ибо обязательным считается только lastName */
        driver.findElement(By.xpath("//div[3]/span[2]/scrm-dynamic-field/scrm-varchar-edit/input")).sendKeys("asd");
        driver.findElement(By.cssSelector(".dynamic-field-name-phone_work .form-control")).click();
        driver.findElement(By.cssSelector(".dynamic-field-name-phone_work .form-control")).sendKeys("123321");
        driver.findElement(By.cssSelector(".ng-star-inserted:nth-child(1) > .button-group-button")).click();
        /*Проверяем, правильно ли заполнились данные*/
        MatcherAssert.assertThat(driver.findElement(By.xpath("//div[@id='ngb-nav-6-panel']/div/scrm-field-layout/form/div/div/div[2]/div/scrm-field/scrm-dynamic-field/scrm-group-field/div/div[3]/span/scrm-dynamic-field/scrm-varchar-detail")).getText(), is("asd"));
        MatcherAssert.assertThat(driver.findElement(By.xpath("//scrm-phone-detail/a")).getText(), is("123321"));
    }

    /**
     * Форма преобразования предварительного контакта
     */
    public void convertLeadTest() {
        /*Кликаем Actions->Convert Lead*/
        driver.findElement(By.xpath("//button[contains(.,\'Actions \')]")).click();
        driver.findElement(By.xpath("//a[5]/div/div")).click();
        /*Выбираем iframe*/
        driver.switchTo().frame(driver.findElement(By.tagName("iframe")));
        driver.findElement(By.xpath("//input[@id='newContacts']")).click();
        vars.put("window_handles", driver.getWindowHandles());
        driver.findElement(By.cssSelector("#btn_report_to_name > #Layer_1 #Page-1-Copy-6")).click();
        wait.until(ExpectedConditions.numberOfWindowsToBe(2));
        /* Засовываем в vars разные окна, одно из которых всплывающее*/
        vars.put("win2149", new ArrayList<>(driver.getWindowHandles()).stream().filter(o -> !o.equals(driver.getWindowHandle())).findAny().orElse(driver.getWindowHandle()));
        vars.put("root", driver.getWindowHandle());
        driver.switchTo().window(vars.get("win2149").toString());
        /*Очищаем, ищем, выбираем контакт*/
        driver.findElement(By.xpath("//form[@id=\'popup_query_form\']/table/tbody/tr[2]/td/input[11]")).click();
        driver.findElement(By.xpath("//form[@id=\'popup_query_form\']/table/tbody/tr[2]/td/input[10]")).click();
        driver.findElement(By.xpath("//a[@onclick=\"send_back(\'Contacts\',\'10046ca8-f6a4-b8b1-722f-625443641192\');\"]")).click();
        driver.switchTo().window(vars.get("root").toString());
        driver.switchTo().frame(0);
        /*Переключаемся на основное окно, заполняем название контрагента, сохраняем*/
        driver.findElement(By.xpath("//div[@id=\'createAccounts\']/div/div/div[3]/input")).click();
        driver.findElement(By.xpath("//div[@id=\'createAccounts\']/div/div/div[3]/input")).sendKeys("asd");
        driver.findElement(By.xpath("//form[@id=\'ConvertLead\']/table/tbody/tr/td/input[12]")).click();
        driver.findElement(By.xpath("//div[@id=\'pagecontent\']/div/ul/li[2]/a")).click();
        driver.switchTo().defaultContent();
        /*Проверяем, правильно ли записали данные*/
        MatcherAssert.assertThat(driver.findElement(By.xpath("//div[@id=\'ngb-nav-9-panel\']/div/scrm-field-layout/form/div/div/div[2]/div/scrm-field/scrm-dynamic-field/scrm-varchar-detail")).getText(), is("asd"));
        MatcherAssert.assertThat(driver.findElement(By.xpath("//div[@id=\'ngb-nav-9-panel\']/div/scrm-field-layout/form/div/div[2]/div[2]/div/scrm-field/scrm-dynamic-field/scrm-phone-detail/a")).getText(), is("123321"));
    }

    /**
     * Форма просмотра контрагента
     *  Этот пункт был мне не совсем понятен, поэтому реализовал вот так.
     *  У меня никак не получалось сделать через click() на subpanels'ах, выдавало ошибку про interrupted click, что якобы в том
     *  месте на панельки нельзя нажать. Пришлось городить большие xpath'ы, дабы задание выполнить. В целом то понятно как сделать,
     *  не понятно, почему Selenium кликнуть там не мог, то ли Overlay, то ли еще что.
     */
    public void testAcc() {
        assertEquals("Malcolm Chmura", driver.findElement(By.xpath("//html/body/app-root/div/scrm-record/div/scrm-record-container/div/div/div[1]/div/div[4]/div/scrm-subpanel-container/div/scrm-subpanel[2]/scrm-panel/div/div[2]/div/scrm-table/div/scrm-table-body/div/table/tbody/tr/td[1]/scrm-field/scrm-dynamic-field/a/scrm-varchar-detail")).getText());
        assertEquals("asd", driver.findElement(By.xpath("//html/body/app-root/div/scrm-record/div/scrm-record-container/div/div/div[1]/div/div[4]/div/scrm-subpanel-container/div/scrm-subpanel[1]/scrm-panel/div/div[2]/div/scrm-table/div/scrm-table-body/div/table/tbody/tr/td[1]/scrm-field/scrm-dynamic-field/a/scrm-varchar-detail")).getText());
    }


    @Test
    public void testScenario() {
        loginTest();
        leadTest();
        convertLeadTest();
        testAcc();
    }

}
