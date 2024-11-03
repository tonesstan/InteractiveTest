package InteractiveTest.Pages;

import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

public enum MenuPage {

    Disabled($("li#ui-id-1")),
    Enabled($("li#ui-id-3")),
    Downloads($("li#ui-id-4")),
    PDF($("li#ui-id-5")),
    CSV($("li#ui-id-6")),
    EXCEL($("li#ui-id-7")),
    BackToJQueryUI($("li#ui-id-8"));

    private final SelenideElement element;

    MenuPage (SelenideElement element) {this.element = element;}

    public static void waitForPageLoad() {$("h3").shouldBe(visible).shouldHave(text("JQueryUI - Menu"));}

    public void move() {element.shouldBe(visible, Duration.ofSeconds(2)).hover();}

    public void check() {element.shouldBe(visible, Duration.ofSeconds(2)).shouldBe(enabled, Duration.ofSeconds(2));}

}