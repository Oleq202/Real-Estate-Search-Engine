import javafx.scene.control.Alert;
import java.io.*;
import java.util.Random;

public class PropertyService {

    private static final Random rand = new Random();

    public static String generatePhoneNumber() {
        int firstDigit = rand.nextBoolean() ? 6 : 7;
        StringBuilder sb = new StringBuilder().append(firstDigit);
        for (int i = 0; i < 8; i++)
            sb.append(rand.nextInt(10));
        return sb.toString();
    }

    public static void saveOfferToFile(String name, String city, int price, String phone) {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("saved_offers.txt", true)))) {
            writer.println("--- Saved Property ---");
            writer.println("Property: " + name + " | City: " + city);
            writer.println("Price: " + price + " zł | Contact: " + phone);
            writer.println("Saved on: " + new java.util.Date() + "\n");
            new Alert(Alert.AlertType.INFORMATION, "Offer saved!").showAndWait();
        } catch (IOException ex) {
            System.err.println("Failed to write to file: " + ex.getMessage());
        }
    }
}