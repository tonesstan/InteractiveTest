package InteractiveTest.Tests;

import InteractiveTest.Pages.FormPage;
import com.codeborne.selenide.Configuration;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.*;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;

import static InteractiveTest.Pages.FormPage.*;
import static InteractiveTest.Utils.*;
import static InteractiveTest.Utils.clickRandomMenuItem;
import static com.codeborne.selenide.Selenide.*;
import static java.util.logging.Logger.getLogger;
import static org.junit.jupiter.api.Assertions.fail;

public class FormTests {

    private static Level previousLevel;

    @BeforeAll
    public static void setUp() {
        previousLevel = getLogger("").getLevel();
        getLogger("").setLevel(Level.WARNING);
        Configuration.baseUrl = "https://demoqa.com/automation-practice-form";
        Configuration.browser = "firefox";
        Configuration.browserCapabilities = new FirefoxOptions().setPageLoadStrategy(PageLoadStrategy.EAGER)
                .addArguments("--headless", "--window-size=1920,1080", "--disable-notifications", "--disable-gpu", "--disable-dev-tools", "--fastSetValue");
    }

    @BeforeEach
    public void openLoginPage() {
        open("");
        waitForPageLoad();
    }

    @AfterEach
    public void cleanUp() {
        clearBrowserCookies();
        clearBrowserLocalStorage();
        executeJavaScript("window.sessionStorage.clear()");
    }

    @AfterAll
    public static void tearDown() {
        closeWebDriver();
        getLogger("").setLevel(previousLevel);
    }

    @Test
    @Tag("form")
    @DisplayName("Проверка видимости и интерактивности полей формы")
    public void fieldsTest() {
        System.out.println("Тестируем поля формы...");
        FormPage[] elements = values();
        for (int i = 0; i < elements.length - 1; i++) {
            try {
                scrollToElement(elements[i].getElement());
                elements[i].check();
                if (elements[i] == Year || elements[i] == Month) selectRandomOption(elements[i].getElement());
                else if (elements[i] == Day) {
                    String month = Month.getElement().getSelectedOptionText();
                    String year = Year.getElement().getSelectedOptionText();
                    assert month != null && year != null;
                    chooseDay(randomDayByMonthAndYear(month, year));
                }
                else if (elements[i] == State || elements[i] == City) {
                    elements[i].click();
                    clickRandomMenuItem(elements[i].getElement(), "Select " + elements[i].name());
                }
                else elements[i].click();
            }
            catch (AssertionError e) {
                fail("\nОшибка: поле " + elements[i].name() + " работает некорректно! \nТест провален! Подробности:\n" + e.getMessage());
            }
            System.out.println("Поле " + elements[i].name() + " прошло проверку.");
        }
        System.out.println("\nТест прошёл успешно!\n");
    }

    @Test
    @Tag("form")
    @DisplayName("Проверка валидации вводимых данных")
    public void inputTest() {
        System.out.println("Тестируем валидацию вводимых данных...");
        System.out.println("Получаем случайный userprofile...");
        JsonObject userProfile = getRandomUserProfile();

        String firstName = userProfile.get("name").getAsJsonObject().get("first").getAsString();
        System.out.println("Имя: " + firstName);
        String lastName = userProfile.get("name").getAsJsonObject().get("last").getAsString();
        System.out.println("Фамилия: " + lastName);
        String email = userProfile.get("email").getAsString();
        System.out.println("Email: " + email);
        String gender = capitalizeFirstLetter(userProfile.get("gender").getAsString());
        System.out.println("Пол: " + gender);
        String phone = userProfile.get("cell").getAsString().replaceAll("\\D", "");
        System.out.println("Телефон: " + phone);
        if (phone.length() < 10) {
            phone = phone + generateRandomString(10 - phone.length(),10 - phone.length(), "0123456789");
        }
        String address = userProfile.get("location").getAsJsonObject().get("street").getAsJsonObject().get("number").getAsString() +
                ", " + userProfile.get("location").getAsJsonObject().get("street").getAsJsonObject().get("name").getAsString();
        System.out.println("Адрес: " + address);

        String birthDateString = userProfile.get("dob").getAsJsonObject().get("date").getAsString();
        ZonedDateTime birthDateTime = ZonedDateTime.parse(birthDateString, DateTimeFormatter.ISO_DATE_TIME);
        int year = birthDateTime.getYear();
        System.out.println("Дата рождения");
        System.out.println("Год: " + year);
        int month = birthDateTime.getMonthValue();
        System.out.println("Месяц: " + month);
        int day = birthDateTime.getDayOfMonth();
        System.out.println("День: " + day);

        FirstName.input(firstName);
        LastName.input(lastName);
        Email.input(email);
        chooseGender(gender);
        scrollToElement(Mobile.getElement());
        Mobile.input(phone);
        scrollToElement(DateOfBirth.getElement());
        DateOfBirth.click();
        Year.select(String.valueOf(year));
        Month.select(String.valueOf(month - 1));
        chooseDay(String.valueOf(day));
        scrollToElement(Subjects.getElement());
        for (int i = 0; i < randomNumber(1, 3); i++) {
            String randomLetter = generateRandomString(1, 1, "aceghilmnorstuy");
            Subjects.input(randomLetter);
            clickRandomMenuItem(Subjects.getElement().$("div.subjects-auto-complete__menu"), randomLetter);
        }
        checkOrNot(Sports.getElement());
        checkOrNot(Reading.getElement());
        checkOrNot(Music.getElement());
        scrollToElement(Address.getElement());
        Address.input(address);
        scrollToElement(State.getElement());
        State.click();
        clickRandomMenuItem(State.getElement(), "Select State");
        scrollToElement(City.getElement());
        City.click();
        clickRandomMenuItem(City.getElement(), "Select City");
        scrollToElement(Submit.getElement());
        Submit.click();
        try {
            SubmittedData.check();
            System.out.println("\nТест прошёл успешно!");
            System.out.println("Отправленные данные:");
            printTable(copyTable(SubmittedData.getElement()));
        } catch (AssertionError e) {
            fail("\nОшибка: данные не были корректно введены и отправлены!\nТест провален! Подробности:\n" + e.getMessage());
        }
    }

}