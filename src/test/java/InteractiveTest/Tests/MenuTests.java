package InteractiveTest.Tests;

import InteractiveTest.Pages.MenuPage;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.*;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.logging.Level;

import static InteractiveTest.Pages.MenuPage.*;
import static com.codeborne.selenide.Selenide.*;
import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.fail;

public class MenuTests {

    private static Level previousLevel;

    @BeforeAll
    public static void setUp() {
        previousLevel = getLogger("").getLevel();
        getLogger("").setLevel(Level.WARNING);
        Configuration.baseUrl = "https://the-internet.herokuapp.com/jqueryui/menu";
        Configuration.browser = "firefox";
        Configuration.browserCapabilities = new FirefoxOptions().setPageLoadStrategy(PageLoadStrategy.EAGER)
                .addArguments("--headless", "--window-size=1920,1080", "--disable-notifications", "--disable-gpu", "--disable-dev-tools", "--fastSetValue");
//                .addArguments("user-data-dir=C:\\Users\\Tone\\AppData\\Local\\Google\\Chrome\\User Data")
//                .addArguments("profile-directory=Default")
//                .addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:132.0) Gecko/20100101 Firefox/132.0")
//        ;
    }

    @AfterAll
    public static void tearDown() {
        closeWebDriver();
        getLogger("").setLevel(previousLevel);
    }

    @Test
    @Tag("menu")
    @DisplayName("Проверка видимости и интерактивности элементов меню")
    public void menuTest() {
        System.out.println("Тестируем элементы меню...");
        open("");
        waitForPageLoad();
        MenuPage[] elements = values();
        for (int i = 0; i < elements.length - 1; i++) {
            elements[i].move();
            try {elements[i+1].check();} catch (AssertionError e) {
                fail("\nОшибка: кнопка" + elements[i+1].name() + " работает некорректно! \nТест провален! Подробности:\n" + e.getMessage());
            }
            System.out.println("Кнопка " + elements[i+1].name() + " прошла проверку.");
        }
        System.out.println("Тест прошёл успешно!");
    }
}