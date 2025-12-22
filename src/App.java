import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.SnapshotParameters;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class App extends Application {

    private static class SliderGroup {
        public final Slider slider;
        public final Label label;

        public SliderGroup(Slider slider, Label label) {
            this.slider = slider;
            this.label = label;
        }
    }

    private static class NeighbourhoodGroup {
        public final Button button;
        public final List<CheckBox> checkboxes;

        public NeighbourhoodGroup(Button button, List<CheckBox> checkboxes) {
            this.button = button;
            this.checkboxes = checkboxes;
        }
    }

    private SliderGroup createSliderGroup(int min, int max, int initial, String unit, int tickUnit, int increment) {
        Slider slider = new Slider(min, max, initial);
        Label label = new Label(initial + unit);
        slider.setShowTickLabels(true);
        slider.setShowTickMarks(true);
        slider.setMajorTickUnit(tickUnit);
        slider.setBlockIncrement(increment);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> label.setText(newVal.intValue() + unit));
        return new SliderGroup(slider, label);
    }

    private void connectSliders(Slider minSlider, Slider maxSlider) {
        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > maxSlider.getValue())
                maxSlider.setValue(newVal.intValue());
        });
        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() < minSlider.getValue())
                minSlider.setValue(newVal.intValue());
        });
    }

    private CheckBox createCheckBox(String text) {
        return new CheckBox(text);
    }

    private NeighbourhoodGroup createNeighbourhoodStage(List<CheckBox> cbs) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));
        content.getChildren().add(new Label("Select Preferred Neighbourhoods:"));

        // Select All Button
        Button selectAll = new Button("Select All");
        selectAll.setOnAction(e -> cbs.forEach(cb -> cb.setSelected(true)));
        content.getChildren().add(selectAll);

        content.getChildren().addAll(cbs);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);
        Stage popupStage = new Stage();
        popupStage.setTitle("Neighbourhood Selector");
        Scene scene = new Scene(scroll, 350, 450);
        scene.getStylesheets().add("styles.css");
        popupStage.setScene(scene);
        Button neighbourhoodButton = new Button("Select Neighbourhoods");
        neighbourhoodButton.setId("neighbourhood-button");
        neighbourhoodButton.setOnAction(e -> popupStage.show());
        return new NeighbourhoodGroup(neighbourhoodButton, cbs);
    }

    @Override
    public void start(Stage stage) {
        // Sliders
        SliderGroup minPriceG = createSliderGroup(0, 10000000, 100000, " zł", 2000000, 50000);
        SliderGroup maxPriceG = createSliderGroup(0, 10000000, 500000, " zł", 2000000, 50000);
        connectSliders(minPriceG.slider, maxPriceG.slider);
        SliderGroup minAreaG = createSliderGroup(0, 500, 50, " sqm", 100, 1);
        SliderGroup maxAreaG = createSliderGroup(0, 500, 300, " sqm", 100, 1);
        connectSliders(minAreaG.slider, maxAreaG.slider);
        SliderGroup minRoomsG = createSliderGroup(0, 10, 2, " rooms", 2, 1);
        SliderGroup maxRoomsG = createSliderGroup(0, 10, 5, " rooms", 2, 1);
        connectSliders(minRoomsG.slider, maxRoomsG.slider);
        SliderGroup minYearG = createSliderGroup(1950, 2026, 2000, "", 20, 1);
        SliderGroup maxYearG = createSliderGroup(1950, 2026, 2026, "", 20, 1);
        connectSliders(minYearG.slider, maxYearG.slider);
        SliderGroup minFloorG = createSliderGroup(0, 25, 0, " floor", 5, 1);
        SliderGroup maxFloorG = createSliderGroup(0, 25, 10, " floor", 5, 1);
        connectSliders(minFloorG.slider, maxFloorG.slider);
        SliderGroup minParkingG = createSliderGroup(0, 5, 1, " slots", 1, 1);
        SliderGroup maxParkingG = createSliderGroup(0, 5, 3, " slots", 1, 1);
        connectSliders(minParkingG.slider, maxParkingG.slider);
        SliderGroup transportDistG = createSliderGroup(0, 10, 1, " km", 2, 1);
        SliderGroup minAvgPriceG = createSliderGroup(0, 10000, 1000, " zł/sqm", 2000, 100);
        SliderGroup maxAvgPriceG = createSliderGroup(0, 10000, 5000, " zł/sqm", 2000, 100);
        connectSliders(minAvgPriceG.slider, maxAvgPriceG.slider);
        SliderGroup minGardenG = createSliderGroup(0, 1000, 0, " sqm", 200, 10);
        SliderGroup maxGardenG = createSliderGroup(0, 1000, 100, " sqm", 200, 10);
        connectSliders(minGardenG.slider, maxGardenG.slider);
        SliderGroup minHouseFloorsG = createSliderGroup(0, 5, 1, " levels", 1, 1);
        SliderGroup maxHouseFloorsG = createSliderGroup(0, 5, 3, " levels", 1, 1);
        connectSliders(minHouseFloorsG.slider, maxHouseFloorsG.slider);

        CheckBox forSale = createCheckBox("Prefer for sale");
        CheckBox primaryMarket = createCheckBox("Prefer primary market");
        CheckBox kitchenAnnex = createCheckBox("Kitchen annex");
        CheckBox wantElevator = createCheckBox("Want elevator");
        CheckBox preferSchool = createCheckBox("Prefer school nearby");
        CheckBox preferPark = createCheckBox("Prefer park nearby");
        CheckBox preferShop = createCheckBox("Prefer shop nearby");
        CheckBox preferPharmacy = createCheckBox("Prefer pharmacy nearby");
        CheckBox preferApartment = createCheckBox("Prefer apartment");
        CheckBox preferHouse = createCheckBox("Prefer house");

        List<CheckBox> neighborhoodCBs = new ArrayList<>();
        try {
            java.nio.file.Path path = java.nio.file.Paths.get("csv/Neighbourhoods.csv");
            List<String> lines = java.nio.file.Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (!line.trim().isEmpty()) {
                    String name = line.split(",")[0].trim();
                    neighborhoodCBs.add(new CheckBox(name));
                }
            }
        } catch (Exception ex) {
            neighborhoodCBs.add(new CheckBox("Default Neighbourhood"));
        }
        NeighbourhoodGroup nGroup = createNeighbourhoodStage(neighborhoodCBs);

        VBox resultsLayout = new VBox(15);
        resultsLayout.getStyleClass().add("results-layout");
        Button searchButton = new Button("Search");
        searchButton.setId("search-button");

        searchButton.setOnAction(e -> {
            // 1. Clear immediately to show the user the search has started
            resultsLayout.getChildren().clear();
            resultsLayout.getChildren().add(new Label("Searching..."));

            new Thread(() -> {
                try {
                    String nString = nGroup.checkboxes.stream()
                            .filter(CheckBox::isSelected).map(CheckBox::getText).collect(Collectors.joining(","));
                    if (nString.isEmpty())
                        nString = "NONE";

                    ProcessBuilder pb = new ProcessBuilder("src/cpp_backend.exe",
                            String.valueOf((int) minPriceG.slider.getValue()),
                            String.valueOf((int) maxPriceG.slider.getValue()),
                            String.valueOf((int) minAreaG.slider.getValue()),
                            String.valueOf((int) maxAreaG.slider.getValue()),
                            String.valueOf((int) minRoomsG.slider.getValue()),
                            String.valueOf((int) maxRoomsG.slider.getValue()),
                            String.valueOf((int) minYearG.slider.getValue()),
                            String.valueOf((int) maxYearG.slider.getValue()),
                            String.valueOf((int) minFloorG.slider.getValue()),
                            String.valueOf((int) maxFloorG.slider.getValue()),
                            String.valueOf((int) minParkingG.slider.getValue()),
                            String.valueOf((int) maxParkingG.slider.getValue()),
                            String.valueOf((int) minAvgPriceG.slider.getValue()),
                            String.valueOf((int) maxAvgPriceG.slider.getValue()),
                            String.valueOf((int) minGardenG.slider.getValue()),
                            String.valueOf((int) maxGardenG.slider.getValue()),
                            String.valueOf((int) minHouseFloorsG.slider.getValue()),
                            String.valueOf((int) maxHouseFloorsG.slider.getValue()),
                            String.valueOf((int) transportDistG.slider.getValue()),
                            kitchenAnnex.isSelected() ? "1" : "0", wantElevator.isSelected() ? "1" : "0",
                            forSale.isSelected() ? "1" : "0", primaryMarket.isSelected() ? "1" : "0", nString);

                    Process process = pb.start();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    StringBuilder output = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null)
                        output.append(line);
                    process.waitFor();

                    List<Map<String, Object>> receivedHomes = parseJson(output.toString());

                    // 2. IMPORTANT: Move ALL UI logic back to the Platform thread at once
                    Platform.runLater(() -> {
                        resultsLayout.getChildren().clear(); // Clear the "Searching..." label
                        if (receivedHomes.isEmpty()) {
                            resultsLayout.getChildren().add(new Label("No results found."));
                        } else {
                            for (Map<String, Object> home : receivedHomes) {
                                displayHomeCard(home, resultsLayout);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        resultsLayout.getChildren().clear();
                        resultsLayout.getChildren().add(new Label("Error during search: " + ex.getMessage()));
                    });
                }
            }).start();
        });

        GridPane grid1 = new GridPane();
        grid1.setHgap(10);
        grid1.setVgap(10);
        grid1.add(new Label("Price Range:"), 0, 0);
        grid1.add(minPriceG.slider, 1, 0);
        grid1.add(minPriceG.label, 2, 0);
        grid1.add(new Label("to"), 0, 1);
        grid1.add(maxPriceG.slider, 1, 1);
        grid1.add(maxPriceG.label, 2, 1);
        grid1.add(new Label("Area Range:"), 0, 2);
        grid1.add(minAreaG.slider, 1, 2);
        grid1.add(minAreaG.label, 2, 2);
        grid1.add(new Label("to"), 0, 3);
        grid1.add(maxAreaG.slider, 1, 3);
        grid1.add(maxAreaG.label, 2, 3);
        grid1.add(new Label("Rooms:"), 0, 4);
        grid1.add(minRoomsG.slider, 1, 4);
        grid1.add(minRoomsG.label, 2, 4);
        grid1.add(new Label("to"), 0, 5);
        grid1.add(maxRoomsG.slider, 1, 5);
        grid1.add(maxRoomsG.label, 2, 5);
        VBox coreChecks = new VBox(5, forSale, primaryMarket, preferApartment, preferHouse, nGroup.button);
        grid1.add(coreChecks, 4, 0, 1, 6);

        GridPane grid2 = new GridPane();
        grid2.setHgap(10);
        grid2.setVgap(10);
        grid2.add(new Label("Year Built:"), 0, 0);
        grid2.add(minYearG.slider, 1, 0);
        grid2.add(minYearG.label, 2, 0);
        grid2.add(new Label("to"), 0, 1);
        grid2.add(maxYearG.slider, 1, 1);
        grid2.add(maxYearG.label, 2, 1);
        grid2.add(new Label("Floor:"), 0, 2);
        grid2.add(minFloorG.slider, 1, 2);
        grid2.add(minFloorG.label, 2, 2);
        grid2.add(new Label("to"), 0, 3);
        grid2.add(maxFloorG.slider, 1, 3);
        grid2.add(maxFloorG.label, 2, 3);
        grid2.add(new Label("Parking:"), 0, 4);
        grid2.add(minParkingG.slider, 1, 4);
        grid2.add(minParkingG.label, 2, 4);
        VBox extraChecks = new VBox(5, kitchenAnnex, wantElevator, preferSchool, preferPark, preferShop,
                preferPharmacy);
        grid2.add(extraChecks, 4, 0, 1, 6);

        TabPane tabs = new TabPane(new Tab("Core Criteria", grid1), new Tab("Optional Criteria", grid2));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox mainLayout = new VBox(15, tabs, new HBox(searchButton), resultsLayout);
        mainLayout.setPadding(new Insets(15));
        mainLayout.getStyleClass().add("vbox");
        ScrollPane rootScroll = new ScrollPane(mainLayout);
        rootScroll.setFitToWidth(true);
        Scene scene = new Scene(rootScroll, 1000, 850);
        scene.getStylesheets().add("styles.css");
        stage.setScene(scene);
        stage.setTitle("Home Seeker Pro");
        stage.show();
    }

    private void displayHomeCard(Map<String, Object> home, VBox layout) {
        HBox card = new HBox(20);
        card.setPadding(new Insets(10));
        card.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");

        ImageView imgView = new ImageView();
        imgView.setFitWidth(180);
        imgView.setFitHeight(130);
        imgView.setPreserveRatio(true);

        String imgPath = (String) home.getOrDefault("image", "");
        System.out.println("LOG: RAW JSON path -> [" + imgPath + "]");

        if (imgPath != null && !imgPath.trim().isEmpty() && !imgPath.equals("0")) {
            imgPath = imgPath.replace("\"", "").trim();

            if (imgPath.startsWith("http")) {
                System.out.println("LOG: Web URL detected.");
                imgView.setImage(new Image(imgPath, 180, 130, true, true));
            } else {
                // REMOVED replaceFirst logic: Use the path exactly as returned by backend
                File imageFile = new File(System.getProperty("user.dir"), imgPath);
                System.out.println("LOG: Checking local path -> " + imageFile.getAbsolutePath());

                if (imageFile.exists()) {
                    try (java.io.FileInputStream fis = new java.io.FileInputStream(imageFile)) {
                        Image image = new Image(fis, 180, 130, true, true);
                        if (!image.isError()) {
                            imgView.setImage(image);
                            System.out.println("LOG: Local image loaded via stream.");
                        } else {
                            BufferedImage bi = ImageIO.read(imageFile);
                            if (bi != null)
                                imgView.setImage(SwingFXUtils.toFXImage(bi, null));
                        }
                    } catch (Exception e) {
                        System.err.println("LOG: Load error -> " + e.getMessage());
                        setPlaceholderImage(imgView);
                    }
                } else {
                    System.err.println("LOG: FILE NOT FOUND ON DISK.");
                    setPlaceholderImage(imgView);
                }
            }
        } else {
            setPlaceholderImage(imgView);
        }

        VBox info = new VBox(8);
        Label nameLabel = new Label((String) home.getOrDefault("name", "Unknown Name"));
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #3f51b5;");
        Label priceLabel = new Label(home.get("price") + " zł (" + home.get("score") + "% Match)");
        priceLabel.setStyle("-fx-text-fill: #ff4081; -fx-font-weight: bold;");

        info.getChildren().addAll(
                nameLabel, priceLabel,
                new Label("📐 Area: " + home.get("area_sqm") + " sqm | 🚪 Rooms: " + home.get("rooms")),
                new Label("🏢 Floor: " + home.get("floor") + " | 🚗 Parking: " + home.get("parking")),
                new Label("📍 City: " + home.get("city") + " | 🏗️ Built: " + home.get("year_built")));

        card.getChildren().addAll(imgView, info);
        layout.getChildren().add(card);
    }

    private void setPlaceholderImage(ImageView imgView) {
        Rectangle rect = new Rectangle(180, 130);
        rect.setFill(Color.LIGHTGRAY);
        StackPane pane = new StackPane(rect, new Text("No Image Found"));
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        imgView.setImage(pane.snapshot(params, null));
    }

    private List<Map<String, Object>> parseJson(String json) {
        List<Map<String, Object>> list = new ArrayList<>();
        int start = json.indexOf("["), end = json.lastIndexOf("]");
        if (start == -1 || end == -1)
            return list;
        String content = json.substring(start + 1, end).trim();
        if (content.isEmpty())
            return list;

        for (String obj : content.split("(?<=\\}),\\s*(?=\\{)")) {
            Map<String, Object> map = new HashMap<>();
            String[] keys = { "name", "price", "area_sqm", "rooms", "year_built", "city", "floor", "parking", "image",
                    "score" };
            for (String key : keys)
                map.put(key, extractValue(obj, key));
            list.add(map);
        }
        return list;
    }

    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int start = json.indexOf(searchKey);
        if (start == -1)
            return "0";
        int colon = json.indexOf(":", start + searchKey.length());
        if (colon == -1)
            return "0";
        int valStart = colon + 1;
        while (valStart < json.length() && (Character.isWhitespace(json.charAt(valStart))))
            valStart++;
        if (valStart < json.length() && json.charAt(valStart) == '\"') {
            int valEnd = json.indexOf("\"", valStart + 1);
            if (valEnd != -1)
                return json.substring(valStart + 1, valEnd);
        }
        int valEnd = valStart;
        while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}')
            valEnd++;
        return json.substring(valStart, valEnd).trim();
    }

    public static void main(String[] args) {
        launch(args);
    }
}