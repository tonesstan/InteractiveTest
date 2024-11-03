package InteractiveTest;

import com.codeborne.selenide.SelenideElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.executeJavaScript;

public class Utils {

    //Получаем JSON-объект по URL
    private static JsonObject getJSONbyURL(String url) {
        StringBuilder response = new StringBuilder();

        try {
            URL obj = new URI(url).toURL();
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } else {System.out.println("GET request not worked");}
            con.disconnect();
        } catch (IOException | URISyntaxException e) {throw new RuntimeException(e);}

        return JsonParser.parseString(response.toString()).getAsJsonObject();
    }

    //получаем случайный англоязычный userprofile с https://randomuser.me
    public static JsonObject getRandomUserProfile() {
        return getJSONbyURL("https://randomuser.me/api").getAsJsonArray("results").get(0).getAsJsonObject();
    }

    //генерируем случайную строку с заданными допустимыми длиной и символами
    public static String generateRandomString(int minLength, int maxLength, String allowedChars) {
        SecureRandom random = new SecureRandom();
        int length = random.nextInt(maxLength - minLength + 1) + minLength;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            sb.append(allowedChars.charAt(index));
        }
        return sb.toString();
    }

    //генерируем случайное целое число в заданном диапазоне
    public static int randomNumber(int min, int max) {return new SecureRandom().nextInt(max - min + 1) + min;}

    //генерируем случайный день месяца по месяцу и году
    public static String randomDayByMonthAndYear(String month, String year) {
        int maxValue;
        switch (month) {
            case "January", "March", "May", "July", "August", "October", "December" -> maxValue = 31;
            case "February" -> maxValue = 28;
            case "April", "June", "September", "November" -> maxValue = 30;
            default -> throw new IllegalArgumentException("Unexpected value: " + month);
        }
        if (month.equals("February") && Integer.parseInt(year) % 4 == 0) maxValue = 29;
        return String.valueOf(randomNumber(1, maxValue));
    }

    //Выбрать случайный вариант селектора
    public static void selectRandomOption(SelenideElement select) {
        System.out.println("Выбираем опцию селектора.");
        long startTime = System.currentTimeMillis();
        select.shouldBe(visible, Duration.ofSeconds(2));
        List<SelenideElement> options = select.$$("option").stream().toList();
        System.out.println("Всего опций обнаружено: " + options.size());
        if(!options.isEmpty()) {
            int index = new SecureRandom().nextInt(options.size());
            System.out.println("Выбираем: " + options.get(index).getText());
            select.selectOptionByValue(Objects.requireNonNull(options.get(index).getValue()));
        }
        System.out.println("Время выбора опции: " + (System.currentTimeMillis() - startTime) + " мс.");
    }

    //кликнуть случайный пункт из выпадающего меню, исключая подсказку
    public static void clickRandomMenuItem(SelenideElement menu, String prompt) {
        System.out.println("Выбираем текстовый элемент за исключением \"" + prompt + "\".");
        long startTime = System.currentTimeMillis();
        menu.shouldBe(visible, Duration.ofSeconds(10));
        List <WebElement> menuItems = menu.$$("*").filterBy(visible).stream().map(SelenideElement::getWrappedElement).toList();
        String script = "return Array.from(arguments[0])" +
                ".filter(el => el.offsetParent !== null && el.children.length === 0 && " +
                "el.textContent.trim() !== arguments[1] && el.textContent.trim() !== '');";
        List<WebElement> items = executeJavaScript(script, menuItems, prompt);
        assert items != null;
        System.out.println("Всего текстовых элементов обнаружено: " + items.size());
        if(!items.isEmpty()){
            int index = new SecureRandom().nextInt(items.size());
            System.out.println("Выбираем: " + executeJavaScript("return arguments[0].textContent", items.get(index)));
            executeJavaScript("arguments[0].click();", items.get(index));
        }
        System.out.println("Время выбора элемента: " + (System.currentTimeMillis() - startTime) + " мс.");
    }

    //поставить в чекбоксе галочку или нет случайным образом
    public static void checkOrNot(SelenideElement checkbox){
        if (new SecureRandom().nextBoolean()) executeJavaScript("arguments[0].click();", checkbox);
    }

    //скролим к элементу, чтобы он был в центре экрана
    public static void scrollToElement(SelenideElement element){executeJavaScript("arguments[0].scrollIntoView({block: 'center'});", element);}

    //скопируем все данные из указанной таблицы
    public static List<List<String>> copyTable(SelenideElement table){
        String tableHTML = table.getAttribute("outerHTML");
        assert tableHTML != null;
        Document doc = Jsoup.parse(tableHTML);
        Elements rows = doc.select("tr");
        List<List<String>> tableData = new ArrayList<>();
        for (Element row : rows) {
            List<String> rowData = new ArrayList<>();
            for (Element cell : row.select("td, th")) {
                rowData.add(cell.text());
            }
            tableData.add(rowData);
        }
        return tableData;
    }

    //распечатаем список в виде таблицы
    public static void printTable(List<List<String>> tableData) {
        // Сначала определим максимальную ширину для каждой колонки
        int[] columnWidths = new int[tableData.getFirst().size()];

        for (List<String> row : tableData) {
            for (int i = 0; i < row.size(); i++) {
                int cellWidth = row.get(i).length();
                if (cellWidth > columnWidths[i]) {
                    columnWidths[i] = cellWidth;
                }
            }
        }

        // Теперь печатаем каждую строку с учетом максимальной ширины столбцов
        for (List<String> row : tableData) {
            for (int i = 0; i < row.size(); i++) {
                String cell = row.get(i);
                // Форматируем ячейку с выравниванием и добавляем несколько пробелов после
                System.out.printf("%-" + (columnWidths[i] + 3) + "s", cell);
            }
            System.out.println(); // Переход на новую строку после каждой строки таблицы
        }
    }

    //делаем первую букву строки заглавной
    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty() || Character.isUpperCase(input.charAt(0))|| !Character.isAlphabetic(input.charAt(0))) return input;
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

}