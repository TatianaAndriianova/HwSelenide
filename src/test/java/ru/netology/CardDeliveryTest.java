package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class CardDeliveryTest {

    private String generateDate(int daysToAdd) {
        return LocalDate.now()
                .plusDays(daysToAdd)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @BeforeEach
    public void setUp() {
        Configuration.baseUrl = "http://localhost:9999";
        Configuration.browser = "chrome";
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        Configuration.browserCapabilities = options;
        open("/");
    }

    @Test
    public void shouldSubmitFormSuccessfully() {

        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, java.time.Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + generateDate(3)));
    }

    @Test
    public void shouldSubmitFormWithDifferentCity() {
        $("[data-test-id=city] input").setValue("Санкт-Петербург");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(7));

        $("[data-test-id=name] input").setValue("Петров Пётр");
        $("[data-test-id=phone] input").setValue("+79009876543");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, java.time.Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__content")
                .shouldHave(Condition.text("Встреча успешно забронирована на " + generateDate(7)));
    }

    @Test
    public void shouldSubmitFormWithNameContainingHyphen() {
        $("[data-test-id=city] input").setValue("Казань");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов-Сидоров Алексей");
        $("[data-test-id=phone] input").setValue("+79001112233");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, java.time.Duration.ofSeconds(15));
    }

    @Test
    public void shouldShowErrorWhenCityIsEmpty() {
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldShowErrorWhenCityIsNotAdminCenter() {
        $("[data-test-id=city] input").setValue("Химки");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldShowErrorWhenDateIsTooEarly() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(2));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=date] .input_invalid .input__sub")
                .shouldBe(Condition.visible);
    }

    @Test
    public void shouldShowErrorWhenDateIsEmpty() {
        $("[data-test-id=city] input").setValue("Москва");

        // Полностью очищаем поле даты
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=date] .input_invalid .input__sub")
                .shouldHave(Condition.text("Неверно введена дата"));
    }

    @Test
    public void shouldShowErrorWhenNameIsInLatin() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        // Латинские буквы — запрещены
        $("[data-test-id=name] input").setValue("Ivan Ivanov");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(Condition.text("Имя и Фамилия указаны неверно. Допустимы только русские буквы, пробелы и дефисы."));
    }

    @Test
    public void shouldShowErrorWhenNameIsEmpty() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        // Не заполняем имя
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldShowErrorWhenPhoneHasNoPlus() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("79001234567");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    public void shouldShowErrorWhenPhoneIsTooShort() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        // Меньше 11 цифр
        $("[data-test-id=phone] input").setValue("+7900123");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678."));
    }

    @Test
    public void shouldShowErrorWhenPhoneIsEmpty() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=agreement]").click();
        $("button.button").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Поле обязательно для заполнения"));
    }

    @Test
    public void shouldShowErrorWhenCheckboxNotChecked() {
        $("[data-test-id=city] input").setValue("Москва");

        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        $("[data-test-id=date] input").setValue(generateDate(3));

        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79001234567");
        $("button.button").click();

        $("[data-test-id=agreement].input_invalid")
                .shouldBe(Condition.visible);
    }
}