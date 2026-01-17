import java.util.Map;
import java.util.List;

public class AppTest {
    public static void main(String[] args) {
        System.out.println("--- STARTING EXTENDED UNIT TESTS ---");

        testFullPropertyParsing();

        testMultipleResultsParsing();

        testPhoneNumberLogic();

        System.out.println("--- ALL TESTS COMPLETED ---");
    }

    private static void testFullPropertyParsing() {
        System.out.print("Test 1: Full Schema Extraction... ");

        String mockJson = "{" +
                "\"name\":\"Dolna Wilda\"," +
                "\"price\":3300," +
                "\"area_sqm\":68," +
                "\"rooms\":3," +
                "\"floor\":1," +
                "\"parking\":0," +
                "\"city\":\"Poznan\"," +
                "\"year_built\":1990," +
                "\"score\":88" +
                "}";

        boolean pass = true;
        pass &= "Dolna Wilda".equals(DataUtils.extractValue(mockJson, "name"));
        pass &= "3300".equals(DataUtils.extractValue(mockJson, "price"));
        pass &= "68".equals(DataUtils.extractValue(mockJson, "area_sqm"));
        pass &= "3".equals(DataUtils.extractValue(mockJson, "rooms"));
        pass &= "1".equals(DataUtils.extractValue(mockJson, "floor"));
        pass &= "0".equals(DataUtils.extractValue(mockJson, "parking"));
        pass &= "Poznan".equals(DataUtils.extractValue(mockJson, "city"));
        pass &= "1990".equals(DataUtils.extractValue(mockJson, "year_built"));
        pass &= "88".equals(DataUtils.extractValue(mockJson, "score"));

        if (pass)
            System.out.println("PASSED");
        else
            System.err.println("FAILED (Field mismatch)");
    }

    private static void testMultipleResultsParsing() {
        System.out.print("Test 2: Multi-Object Array Parsing... ");
        String mockArray = "[{\"name\":\"Home A\"}, {\"name\":\"Home B\"}]";
        List<Map<String, Object>> results = DataUtils.parseJson(mockArray);

        if (results.size() == 2 && "Home A".equals(results.get(0).get("name"))) {
            System.out.println("PASSED (Found " + results.size() + " items)");
        } else {
            System.err.println("FAILED (Expected 2 items, got " + results.size() + ")");
        }
    }

    private static void testPhoneNumberLogic() {
        System.out.print("Test 3: PropertyService Phone Logic... ");
        String phone = PropertyService.generatePhoneNumber();
        boolean valid = phone.length() == 9 && (phone.startsWith("6") || phone.startsWith("7"));

        if (valid)
            System.out.println("PASSED (Generated: " + phone + ")");
        else
            System.err.println("FAILED (Invalid format: " + phone + ")");
    }
}