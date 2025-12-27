import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.geometry.*;
import javafx.embed.swing.SwingFXUtils;
import java.io.*;
import java.util.Map;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

interface Renderable { void renderTo(VBox container); }

abstract class AbstractHome implements Renderable {
    protected String name; protected int price; protected String imagePath; protected Map<String, Object> rawData;

    public AbstractHome(Map<String, Object> data) {
        this.rawData = data;
        this.name = (String) data.getOrDefault("name", "Unknown");
        this.price = Integer.parseInt(data.getOrDefault("price", "0").toString());
        this.imagePath = (String) data.getOrDefault("image", "");
    }

    protected void loadPhoto(ImageView imgView) {
        String path = imagePath.replace("\"", "").trim();
        try {
            File f = new File(System.getProperty("user.dir"), path);
            if (f.exists()) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    Image img = new Image(fis, 180, 130, true, true);
                    if (!img.isError()) imgView.setImage(img);
                    else {
                        BufferedImage bi = ImageIO.read(f);
                        if (bi != null) imgView.setImage(SwingFXUtils.toFXImage(bi, null));
                    }
                }
            } else applyPlaceholder(imgView);
        } catch (Exception e) { applyPlaceholder(imgView); }
    }

    private void applyPlaceholder(ImageView v) {
        Rectangle r = new Rectangle(180, 130); r.setFill(Color.LIGHTGRAY);
        v.setImage(new StackPane(r, new Text("No Image")).snapshot(null, null));
    }
}

class HomeResult extends AbstractHome {
    public HomeResult(Map<String, Object> d) { super(d); }

    @Override
    public void renderTo(VBox container) {
        HBox card = new HBox(20); card.setPadding(new Insets(20));
        card.getStyleClass().add("home-card");
        card.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 12; -fx-background-radius: 12;");
        card.setMaxWidth(850);

        ImageView img = new ImageView(); img.setFitWidth(180); img.setFitHeight(130);
        img.setPreserveRatio(true); loadPhoto(img);

        String forSaleVal = (String)rawData.getOrDefault("for_sale", "0");
        boolean isForSale = forSaleVal.equals("1") || forSaleVal.equalsIgnoreCase("true") || forSaleVal.equalsIgnoreCase("TRUE");
        
        Label statusLabel = new Label(isForSale ? "FOR SALE" : "FOR RENT");
        statusLabel.setStyle(isForSale ? 
            "-fx-background-color: #3f51b5; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 5;" : 
            "-fx-background-color: #9c27b0; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 5;");

        VBox info = new VBox(8);
        HBox titleBox = new HBox(10, new Label(this.name), statusLabel);
        ((Label)titleBox.getChildren().get(0)).setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #3f51b5;");

        String ph = PropertyService.generatePhoneNumber();
        Label phLab = new Label("📞 Contact: " + ph); phLab.setStyle("-fx-text-fill: #555; -fx-font-style: italic;");
        
        Button save = new Button("SAVE OFFER");
        save.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        save.setOnAction(e -> PropertyService.saveOfferToFile(this.name, (String)rawData.get("city"), this.price, ph));

        // Displaying City and Province (Neighbourhood)
        String location = rawData.getOrDefault("city", "Unknown") + " (" + rawData.getOrDefault("province", "N/A") + ")";

        info.getChildren().addAll(
            titleBox, 
            new Label(this.price + " zł (" + rawData.get("score") + "% Match)"), 
            phLab,
            new Label("📐 Area: " + rawData.get("area_sqm") + " sqm | 🚪 Rooms: " + rawData.get("rooms")),
            new Label("🏢 Floor: " + rawData.get("floor") + " | 🚗 Parking: " + rawData.get("parking")),
            new Label("📍 Location: " + location), 
            new Label("🏗️ Built: " + rawData.get("year_built")),
            save
        );

        card.getChildren().addAll(img, info);
        container.getChildren().add(card);
    }
}