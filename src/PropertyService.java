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
        try (OfferWriter writer = new OfferWriter("saved_offers.txt")) {
            writer.writeOffer(name, city, price, phone);
            new Alert(Alert.AlertType.INFORMATION, "Offer saved!").showAndWait();
        } catch (IOException ex) {
            System.err.println("Failed to write to file: " + ex.getMessage());
        }
    }

    private static class OfferWriter implements AutoCloseable {
        private PrintWriter writer;

        public OfferWriter(String filename) throws IOException {
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(filename, true)));
        }

        public void writeOffer(String name, String city, int price, String phone) {
            writer.println("--- Saved Property ---");
            writer.println("Property: " + name + " | City: " + city);
            writer.println("Price: " + price + " zł | Contact: " + phone);
            writer.println("Saved on: " + new java.util.Date() + "\n");
        }

        @Override
        public void close() {
            writer.close();
        }
    }

}