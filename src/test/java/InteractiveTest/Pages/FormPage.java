package InteractiveTest.Pages;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public enum FormPage {

    FirstName($("input#firstName")),
    LastName($("input#lastName")),
    Email($("input#userEmail")),
    Male($("input#gender-radio-1")),
    Female($("input#gender-radio-2")),
    Other($("input#gender-radio-3")),
    Mobile($("input#userNumber")),
    DateOfBirth($("input#dateOfBirthInput")),
    Year($("select.react-datepicker__year-select")),
    Month($("select.react-datepicker__month-select")),
    Day($("div.react-datepicker__month")),
    Subjects($("div#subjectsContainer")),
    Sports($("input#hobbies-checkbox-1")),
    Reading($("input#hobbies-checkbox-2")),
    Music($("input#hobbies-checkbox-3")),
    Address($("textarea#currentAddress")),
    State($("div#state")),
    City($("div#city")),
    Submit($("button#submit")),
    SubmittedData($("table"));

    private final SelenideElement element;

    FormPage (SelenideElement element) {this.element = element;}

    public SelenideElement getElement() {return element;}

    public static void waitForPageLoad() {$("h1.text-center").shouldBe(visible).shouldHave(text("Practice Form"));}

    public static void chooseGender (String gender) {valueOf(gender).click();}

    public static void chooseDay(String day) {
        System.out.println("Выбираем день: " + day);
        long startTime = System.currentTimeMillis();
        Day.getElement().shouldBe(visible, Duration.ofSeconds(2));
        String script = "return Array.from(arguments[0].querySelectorAll('*')).filter(el => " +
                "!el.querySelector('*') && " +  // элемент не имеет дочерних
                "!el.classList.contains('react-datepicker__day--outside-month') && " +  // исключаем дни вне месяца
                "el.innerText === arguments[1])";  // точное совпадение с нужным днём
        List<SelenideElement> elements = executeJavaScript(script, Day.getElement().getWrappedElement(), day);
        assert elements != null;
        if (!elements.isEmpty()) {executeJavaScript("arguments[0].click();", elements.getFirst());}
        System.out.println("Время выполнения: " + (System.currentTimeMillis() - startTime) + " мс.");
    }

    public void check() {
        String type = element.getAttribute("type");
        if(!"checkbox".equals(type) && !"radio".equals(type)) element.shouldBe(visible, Duration.ofSeconds(2)).shouldBe(enabled);
    }

    public void click() {
        String type = element.getAttribute("type");
        if ("checkbox".equals(type) || "radio".equals(type)) executeJavaScript("arguments[0].click();", element);
        else element.shouldBe(visible, Duration.ofSeconds(2)).click();
    }

    public void input(String text) {
        if (name().equals("Subjects")) element.$("input#subjectsInput").shouldBe(visible, Duration.ofSeconds(2)).setValue(text);
        else element.shouldBe(visible, Duration.ofSeconds(2)).setValue(text);
    }

    public void select(String option) {
        element.shouldBe(visible, Duration.ofSeconds(2)).selectOptionByValue(option);
    }

}