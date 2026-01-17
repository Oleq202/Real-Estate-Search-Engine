import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.*;
import javafx.application.Platform;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class App extends Application {
    private VBox resultsLayout = new VBox(15);

    private static class SliderGroup {
        public final Slider slider;
        public final Label label;

        public SliderGroup(Slider s, Label l) {
            this.slider = s;
            this.label = l;
        }
    }

    private static class NeighbourhoodGroup {
        public final Button button;
        public final List<CheckBox> checkboxes;

        public NeighbourhoodGroup(Button b, List<CheckBox> c) {
            this.button = b;
            this.checkboxes = c;
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

    private NeighbourhoodGroup createNeighbourhoodStage(List<CheckBox> cbs) {
        VBox content = new VBox(10);
        content.setPadding(new Insets(15));

        Button toggleBtn = new Button("Select All");
        toggleBtn.setOnAction(e -> {
            boolean allSelected = cbs.stream().allMatch(CheckBox::isSelected);
            if (allSelected) {
                cbs.forEach(cb -> cb.setSelected(false));
                toggleBtn.setText("Select All");
            } else {
                cbs.forEach(cb -> cb.setSelected(true));
                toggleBtn.setText("Unselect All");
            }
        });

        for (CheckBox cb : cbs) {
            cb.selectedProperty().addListener((obs, old, nv) -> {
                boolean allMatch = cbs.stream().allMatch(CheckBox::isSelected);
                toggleBtn.setText(allMatch ? "Unselect All" : "Select All");
            });
        }

        content.getChildren().addAll(new Label("Select Preferred Neighbourhoods:"), toggleBtn);
        content.getChildren().addAll(cbs);
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(400);

        Stage popup = new Stage();
        Scene scene = new Scene(scroll, 350, 450);
        scene.getStylesheets().add("styles.css");
        popup.setScene(scene);

        Button b = new Button("Select Neighbourhoods");
        b.setId("neighbourhood-button");
        b.setOnAction(e -> popup.show());
        return new NeighbourhoodGroup(b, cbs);
    }

    @Override
    public void start(Stage stage) {

        SliderGroup minPriceG = createSliderGroup(0, 10000000, 100000, " zł", 2000000, 50000);
        SliderGroup maxPriceG = createSliderGroup(0, 10000000, 500000, " zł", 2000000, 50000);
        SliderGroup minAreaG = createSliderGroup(0, 500, 50, " sqm", 100, 1);
        SliderGroup maxAreaG = createSliderGroup(0, 500, 300, " sqm", 100, 1);
        SliderGroup minRoomsG = createSliderGroup(0, 10, 2, " rooms", 2, 1);
        SliderGroup maxRoomsG = createSliderGroup(0, 10, 5, " rooms", 2, 1);

        SliderGroup minYearG = createSliderGroup(1950, 2026, 2000, "", 20, 1);
        SliderGroup maxYearG = createSliderGroup(1950, 2026, 2026, "", 20, 1);
        SliderGroup minFloorG = createSliderGroup(0, 25, 0, " floor", 5, 1);
        SliderGroup maxFloorG = createSliderGroup(0, 25, 10, " floor", 5, 1);
        SliderGroup minParkG = createSliderGroup(0, 5, 1, " slots", 1, 1);
        SliderGroup maxParkG = createSliderGroup(0, 5, 3, " slots", 1, 1);
        SliderGroup gardenG = createSliderGroup(0, 1000, 0, " sqm", 200, 10);
        SliderGroup transportG = createSliderGroup(0, 10, 1, " km", 2, 1);

        List<CheckBox> neighborhoodCBs = new ArrayList<>();
        try {
            List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get("csv/Neighbourhoods.csv"));
            for (int i = 1; i < lines.size(); i++)
                neighborhoodCBs.add(new CheckBox(lines.get(i).split(",")[0].trim()));
        } catch (Exception ex) {
            neighborhoodCBs.add(new CheckBox("Default"));
        }
        NeighbourhoodGroup nGroup = createNeighbourhoodStage(neighborhoodCBs);

        ToggleGroup typeGroup = new ToggleGroup();
        RadioButton rbBoth = new RadioButton("Both");
        RadioButton rbFlat = new RadioButton("Flat");
        RadioButton rbHouse = new RadioButton("House");
        rbBoth.setToggleGroup(typeGroup);
        rbFlat.setToggleGroup(typeGroup);
        rbHouse.setToggleGroup(typeGroup);
        rbBoth.setSelected(true);
        VBox typeBox = new VBox(5, new Label("Property Type:"), rbBoth, rbFlat, rbHouse);

        CheckBox forSale = new CheckBox("Search for Sale");
        forSale.setSelected(true);
        CheckBox kitchen = new CheckBox("Kitchen annex");
        CheckBox elevator = new CheckBox("Elevator");
        CheckBox school = new CheckBox("School");
        CheckBox park = new CheckBox("Park");
        CheckBox shop = new CheckBox("Shop");
        CheckBox pharmacy = new CheckBox("Pharmacy");
        CheckBox primary = new CheckBox("Primary Market");

        resultsLayout.setAlignment(Pos.TOP_CENTER);
        resultsLayout.setMaxWidth(850);
        Button searchButton = new Button("Search");
        searchButton.setId("search-button");

        searchButton.setOnAction(e -> {
            resultsLayout.getChildren().clear();
            new StatusMessage("Searching...", Color.BLACK).renderTo(resultsLayout);

            new Thread(() -> {
                try {
                    String nStr = nGroup.checkboxes.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                            .collect(Collectors.joining(","));
                    Process p = new ProcessBuilder("src/cpp_backend.exe",
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
                            String.valueOf((int) minParkG.slider.getValue()),
                            String.valueOf((int) maxParkG.slider.getValue()),
                            "0", "10000", "0", String.valueOf((int) gardenG.slider.getValue()), "0", "5",
                            String.valueOf((int) transportG.slider.getValue()),
                            kitchen.isSelected() ? "1" : "0", elevator.isSelected() ? "1" : "0",
                            forSale.isSelected() ? "1" : "0", primary.isSelected() ? "1" : "0",
                            school.isSelected() ? "1" : "0", park.isSelected() ? "1" : "0",
                            shop.isSelected() ? "1" : "0", pharmacy.isSelected() ? "1" : "0",
                            nStr.isEmpty() ? "NONE" : nStr).start();

                    try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                        String out = r.lines().collect(Collectors.joining());
                        List<Map<String, Object>> homes = DataUtils.parseJson(out);
                        Platform.runLater(() -> {
                            resultsLayout.getChildren().clear();
                            resultsLayout.setAlignment(Pos.TOP_CENTER);

                            boolean foundAny = false;
                            for (Map<String, Object> h : homes) {
                                String type = h.getOrDefault("type", "").toString().toLowerCase();
                                boolean matchesFlat = rbFlat.isSelected() && type.contains("apartment");
                                boolean matchesHouse = rbHouse.isSelected() && type.contains("house");
                                if (rbBoth.isSelected() || matchesFlat || matchesHouse) {
                                    new HomeResult(h).renderTo(resultsLayout);
                                    foundAny = true;
                                }
                            }

                            if (!foundAny) {
                                new StatusMessage("No properties found matching your criteria.", Color.RED)
                                        .renderTo(resultsLayout);
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        GridPane g1 = new GridPane();
        g1.setHgap(15);
        g1.setVgap(10);
        g1.setAlignment(Pos.CENTER);
        g1.setPadding(new Insets(20));
        g1.add(new Label("Price:"), 0, 0);
        g1.add(minPriceG.slider, 1, 0);
        g1.add(minPriceG.label, 2, 0);
        g1.add(new Label("to"), 0, 1);
        g1.add(maxPriceG.slider, 1, 1);
        g1.add(maxPriceG.label, 2, 1);
        g1.add(new Label("Area:"), 0, 2);
        g1.add(minAreaG.slider, 1, 2);
        g1.add(minAreaG.label, 2, 2);
        g1.add(new Label("to"), 0, 3);
        g1.add(maxAreaG.slider, 1, 3);
        g1.add(maxAreaG.label, 2, 3);
        g1.add(new Label("Rooms:"), 0, 4);
        g1.add(minRoomsG.slider, 1, 4);
        g1.add(minRoomsG.label, 2, 4);
        g1.add(new Label("to"), 0, 5);
        g1.add(maxRoomsG.slider, 1, 5);
        g1.add(maxRoomsG.label, 2, 5);
        g1.add(new VBox(10, typeBox, forSale, primary, nGroup.button), 4, 0, 1, 6);

        GridPane g2 = new GridPane();
        g2.setHgap(15);
        g2.setVgap(10);
        g2.setAlignment(Pos.CENTER);
        g2.setPadding(new Insets(20));
        g2.add(new Label("Year:"), 0, 0);
        g2.add(minYearG.slider, 1, 0);
        g2.add(minYearG.label, 2, 0);
        g2.add(new Label("Floor:"), 0, 2);
        g2.add(minFloorG.slider, 1, 2);
        g2.add(minFloorG.label, 2, 2);
        g2.add(new Label("Parking:"), 0, 4);
        g2.add(minParkG.slider, 1, 4);
        g2.add(minParkG.label, 2, 4);
        g2.add(new Label("Garden:"), 0, 6);
        g2.add(gardenG.slider, 1, 6);
        g2.add(gardenG.label, 2, 6);
        g2.add(new VBox(10, kitchen, elevator, school, park, shop, pharmacy), 4, 0, 1, 6);

        TabPane tabs = new TabPane(new Tab("Core Criteria", g1), new Tab("Optional Criteria", g2));
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setMaxWidth(950);

        VBox main = new VBox(25, new HBox(tabs) {
            {
                setAlignment(Pos.CENTER);
            }
        }, new HBox(searchButton) {
            {
                setAlignment(Pos.CENTER);
            }
        }, resultsLayout);
        main.setAlignment(Pos.TOP_CENTER);
        main.setPadding(new Insets(30));

        StackPane root = new StackPane(main);
        root.setStyle("-fx-background-color: #e8eaf6;");
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        stage.setScene(new Scene(scroll, 1150, 850));
        stage.getScene().getStylesheets().add("styles.css");
        stage.setTitle("Home Seeker Pro");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}