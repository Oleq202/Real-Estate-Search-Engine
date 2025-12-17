import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.stream.Collectors;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

    @SuppressWarnings("unchecked")
    private NeighbourhoodGroup createNeighbourhoodStage(List<CheckBox> cbs) {
        VBox content = new VBox(15);
        content.getChildren().add(new Label("Select Preferred Neighbourhoods:"));
        content.getChildren().addAll(cbs);
        content.setPadding(new Insets(20));
        content.getStyleClass().add("neighbourhood-popup-content");
        Stage popupStage = new Stage();
        popupStage.setTitle("Neighbourhood Selector");
        Scene scene = new Scene(content, 300, 250);
        scene.getStylesheets().add("styles.css");
        popupStage.setScene(scene);
        Button neighbourhoodButton = new Button("Select Neighbourhoods");
        neighbourhoodButton.setId("neighbourhood-button");
        neighbourhoodButton.setOnAction(e -> popupStage.show());
        return new NeighbourhoodGroup(neighbourhoodButton, cbs);
    }

    public void start(Stage stage) {
        // --- Sliders ---
        SliderGroup minPriceG = createSliderGroup(0, 10000000, 100000, "€", 2000000, 50000);
        SliderGroup maxPriceG = createSliderGroup(0, 10000000, 500000, "€", 2000000, 50000);
        connectSliders(minPriceG.slider, maxPriceG.slider);

        SliderGroup minAreaG = createSliderGroup(0, 500, 50, "sqm", 100, 1);
        SliderGroup maxAreaG = createSliderGroup(0, 500, 300, "sqm", 100, 1);
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
        SliderGroup minAvgPriceG = createSliderGroup(0, 10000, 1000, "€/sqm", 2000, 100);
        SliderGroup maxAvgPriceG = createSliderGroup(0, 10000, 5000, "€/sqm", 2000, 100);
        connectSliders(minAvgPriceG.slider, maxAvgPriceG.slider);

        SliderGroup minGardenG = createSliderGroup(0, 1000, 0, "sqm", 200, 10);
        SliderGroup maxGardenG = createSliderGroup(0, 1000, 100, "sqm", 200, 10);
        connectSliders(minGardenG.slider, maxGardenG.slider);

        SliderGroup minHouseFloorsG = createSliderGroup(0, 5, 1, " floors", 1, 1);
        SliderGroup maxHouseFloorsG = createSliderGroup(0, 5, 3, " floors", 1, 1);
        connectSliders(minHouseFloorsG.slider, maxHouseFloorsG.slider);

        // --- Checkboxes ---
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

        CheckBox cb1 = new CheckBox("Wielkopolska");
        CheckBox cb2 = new CheckBox("Zachodniopomorskie");
        CheckBox cb3 = new CheckBox("Lubuskie");
        NeighbourhoodGroup nGroup = createNeighbourhoodStage(List.of(cb1, cb2, cb3));

        VBox resultsLayout = new VBox(15);
        resultsLayout.getStyleClass().add("results-layout");

        Button searchButton = new Button("Search");
        searchButton.setId("search-button");

        searchButton.setOnAction(e -> {
            resultsLayout.getChildren().clear();
            try {
                List<String> selectedN = nGroup.checkboxes.stream()
                        .filter(CheckBox::isSelected)
                        .map(CheckBox::getText)
                        .collect(Collectors.toList());
                String nString = selectedN.isEmpty() ? "NONE" : String.join(",", selectedN);

                ProcessBuilder pb = new ProcessBuilder(
                        "src/cpp_backend.exe",
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
                        kitchenAnnex.isSelected() ? "1" : "0",
                        wantElevator.isSelected() ? "1" : "0",
                        forSale.isSelected() ? "1" : "0",
                        primaryMarket.isSelected() ? "1" : "0",
                        nString);

                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
                process.waitFor();

                List<Map<String, Object>> receivedHomes = parseJson(output.toString());
                for (Map<String, Object> home : receivedHomes) {
                    displayHomeCard(home, resultsLayout);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // --- Layout Assembly ---
        GridPane grid1 = new GridPane();
        grid1.setHgap(10); grid1.setVgap(10);
        grid1.add(new Label("Price Range:"), 0, 0); grid1.add(minPriceG.slider, 1, 0); grid1.add(minPriceG.label, 2, 0);
        grid1.add(new Label("to"), 0, 1); grid1.add(maxPriceG.slider, 1, 1); grid1.add(maxPriceG.label, 2, 1);
        grid1.add(new Label("Area Range:"), 0, 2); grid1.add(minAreaG.slider, 1, 2); grid1.add(minAreaG.label, 2, 2);
        grid1.add(new Label("to"), 0, 3); grid1.add(maxAreaG.slider, 1, 3); grid1.add(maxAreaG.label, 2, 3);
        grid1.add(new Label("Rooms:"), 0, 4); grid1.add(minRoomsG.slider, 1, 4); grid1.add(minRoomsG.label, 2, 4);
        grid1.add(new Label("to"), 0, 5); grid1.add(maxRoomsG.slider, 1, 5); grid1.add(maxRoomsG.label, 2, 5);
        VBox coreChecks = new VBox(5, forSale, primaryMarket, preferApartment, preferHouse, nGroup.button);
        grid1.add(coreChecks, 4, 0, 1, 6);

        GridPane grid2 = new GridPane();
        grid2.setHgap(10); grid2.setVgap(10);
        grid2.add(new Label("Year Built:"), 0, 0); grid2.add(minYearG.slider, 1, 0); grid2.add(minYearG.label, 2, 0);
        grid2.add(new Label("to"), 0, 1); grid2.add(maxYearG.slider, 1, 1); grid2.add(maxYearG.label, 2, 1);
        grid2.add(new Label("Floor:"), 0, 2); grid2.add(minFloorG.slider, 1, 2); grid2.add(minFloorG.label, 2, 2);
        grid2.add(new Label("to"), 0, 3); grid2.add(maxFloorG.slider, 1, 3); grid2.add(maxFloorG.label, 2, 3);
        grid2.add(new Label("Parking:"), 0, 4); grid2.add(minParkingG.slider, 1, 4); grid2.add(minParkingG.label, 2, 4);
        VBox extraChecks = new VBox(5, kitchenAnnex, wantElevator, preferSchool, preferPark, preferShop, preferPharmacy);
        grid2.add(extraChecks, 4, 0, 1, 6);

        TabPane tabs = new TabPane(new Tab("Core Criteria", grid1), new Tab("Optional Criteria", grid2));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        VBox mainLayout = new VBox(15, tabs, new HBox(searchButton), resultsLayout);
        mainLayout.setPadding(new Insets(15));
        mainLayout.getStyleClass().add("vbox");

        ScrollPane rootScroll = new ScrollPane(mainLayout);
        rootScroll.setFitToWidth(true);
        rootScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Scene scene = new Scene(rootScroll, 1000, 850);
        scene.getStylesheets().add("styles.css");
        stage.setScene(scene);
        stage.setTitle("Home Seeker Pro");
        stage.show();
    }

    private List<Map<String, Object>> parseJson(String json) {
        List<Map<String, Object>> list = new ArrayList<>();
        int startIndex = json.indexOf("[");
        int endIndex = json.lastIndexOf("]");
        if (startIndex == -1 || endIndex == -1 || startIndex >= endIndex) return list;

        String content = json.substring(startIndex + 1, endIndex).trim();
        if (content.isEmpty()) return list;

        String[] objects = content.split("(?<=\\}),\\s*(?=\\{)");
        for (String obj : objects) {
            Map<String, Object> homeMap = new HashMap<>();
            homeMap.put("name", extractValue(obj, "name"));
            homeMap.put("price", extractValue(obj, "price"));
            homeMap.put("area_sqm", extractValue(obj, "area_sqm"));
            homeMap.put("rooms", extractValue(obj, "rooms"));
            homeMap.put("year_built", extractValue(obj, "year_built"));
            homeMap.put("city", extractValue(obj, "city"));
            homeMap.put("province", extractValue(obj, "province"));
            homeMap.put("floor", extractValue(obj, "floor"));
            homeMap.put("parking", extractValue(obj, "parking"));
            homeMap.put("image", extractValue(obj, "image"));
            homeMap.put("score", extractValue(obj, "score"));
            list.add(homeMap);
        }
        return list;
    }

    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\"";
        int start = json.indexOf(searchKey);
        if (start == -1) return "0";
        int colonIndex = json.indexOf(":", start);
        if (colonIndex == -1) return "0";
        int valStart = colonIndex + 1;
        while (valStart < json.length() && (Character.isWhitespace(json.charAt(valStart)) || json.charAt(valStart) == ':')) {
            valStart++;
        }
        if (valStart >= json.length()) return "0";
        if (json.charAt(valStart) == '\"') {
            int valEnd = json.indexOf("\"", valStart + 1);
            return (valEnd != -1) ? json.substring(valStart + 1, valEnd) : "Unknown";
        } else {
            int valEnd = valStart;
            while (valEnd < json.length() && json.charAt(valEnd) != ',' && json.charAt(valEnd) != '}' && !Character.isWhitespace(json.charAt(valEnd))) {
                valEnd++;
            }
            return json.substring(valStart, valEnd).trim();
        }
    }

    private void displayHomeCard(Map<String, Object> home, VBox layout) {
    HBox card = new HBox(20);
    card.getStyleClass().add("home-card");

    String imgPath = (String) home.get("image");
    ImageView imgView = new ImageView();
    
    try {
        // backgroundLoading: true prevents the UI from freezing while loading images
        Image img = new Image(imgPath, 180, 130, true, true, true);
        
        // If the local file is missing, use the placeholder
        img.errorProperty().addListener((obs, oldVal, isError) -> {
            if (isError) {
                imgView.setImage(new Image("https://picsum.photos/id/1/200/140", 180, 130, true, true));
            }
        });
        
        imgView.setImage(img);
    } catch (Exception e) {
        // Fallback for malformed URLs
        imgView.setImage(new Image("https://picsum.photos/id/1/200/140", 180, 130, true, true));
    }

    VBox info = new VBox(8);
    Label name = new Label((String) home.get("name"));
    name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #3f51b5;");
    Label price = new Label(home.get("price") + " € (" + home.get("score") + "% Match)");
    price.setStyle("-fx-font-weight: bold; -fx-text-fill: #ff4081;");
    
    info.getChildren().addAll(name, price, 
        new Label("📐 " + home.get("area_sqm") + " sqm | 🚪 " + home.get("rooms") + " Rooms"),
        new Label("🏢 Floor: " + home.get("floor") + " | 🚗 Parking: " + home.get("parking")),
        new Label("📍 " + home.get("city") + " | 🏗️ " + home.get("year_built")));

    card.getChildren().addAll(imgView, info);
    layout.getChildren().add(card);
}

    public static void main(String[] args) { launch(args); }
}