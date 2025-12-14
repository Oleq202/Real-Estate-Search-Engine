import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.CheckBox;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class App extends Application {
    private static final List<Map<String, Object>> HOMES = new ArrayList<>();
    static {
        HOMES.add(Map.of("name", "Modern Apartment", "price", 200000, "city", "New York", "bedrooms", 2, "province",
                "Wielkopolska", "area_sqm", 80, "year_built", 2018));
        HOMES.add(Map.of("name", "Family House", "price", 350000, "city", "Los Angeles", "bedrooms", 4, "province",
                "Zachodniopomorskie", "area_sqm", 150, "year_built", 1995));
        HOMES.add(Map.of("name", "Cozy Studio", "price", 180000, "city", "Chicago", "bedrooms", 1, "province",
                "Wielkopolska", "area_sqm", 45, "year_built", 2022));
        HOMES.add(Map.of("name", "Beach Villa", "price", 275000, "city", "Miami", "bedrooms", 3, "province", "Lubuskie",
                "area_sqm", 200, "year_built", 2005));
        HOMES.add(Map.of("name", "Penthouse View", "price", 450000, "city", "Boston", "bedrooms", 3, "province",
                "Zachodniopomorskie", "area_sqm", 120, "year_built", 2010));
    }

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

        slider.valueProperty().addListener((obs, oldVal, newVal) -> {
            label.setText(newVal.intValue() + unit);
        });

        return new SliderGroup(slider, label);
    }

    private void connectSliders(Slider minSlider, Slider maxSlider) {
        minSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > maxSlider.getValue()) {
                maxSlider.setValue(newVal.intValue());
            }
        });

        maxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() < minSlider.getValue()) {
                minSlider.setValue(newVal.intValue());
            }
        });
    }

    private CheckBox createCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);

        return checkBox;
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
        popupStage.initOwner(null);

        Scene scene = new Scene(content, 300, 250);
        scene.getStylesheets().add("styles.css");
        popupStage.setScene(scene);

        Button neighbourhoodButton = new Button("Select Neighbourhoods");
        neighbourhoodButton.setId("neighbourhood-button");

        neighbourhoodButton.setOnAction(e -> {
            popupStage.show();
        });

        return new NeighbourhoodGroup(neighbourhoodButton, cbs);
    }

    public void start(Stage stage) {
        // control creation
        SliderGroup minPriceGroup = createSliderGroup(0, 10000000, 100000, "€", 200000, 50000);
        Slider minPriceSlider = minPriceGroup.slider;
        Label minPriceLabel = minPriceGroup.label;

        SliderGroup maxPriceGroup = createSliderGroup(0, 10000000, 500000, "€", 200000, 50000);
        Slider maxPriceSlider = maxPriceGroup.slider;
        Label maxPriceLabel = maxPriceGroup.label;

        SliderGroup minAreaGroup = createSliderGroup(0, 500, 50, "sqm", 100, 1);
        Slider minAreaSlider = minAreaGroup.slider;
        Label minAreaLabel = minAreaGroup.label;

        SliderGroup maxAreaGroup = createSliderGroup(0, 500, 50, "sqm", 100, 1);
        Slider maxAreaSlider = maxAreaGroup.slider;
        Label maxAreaLabel = maxAreaGroup.label;

        SliderGroup minNofRoomsGroup = createSliderGroup(0, 10, 2, " rooms", 5, 1);
        Slider minNofRoomsSlider = minNofRoomsGroup.slider;
        Label minNofRoomsLabel = minNofRoomsGroup.label;

        SliderGroup maxRoomsGroup = createSliderGroup(0, 10, 4, " rooms", 5, 1);
        Slider maxRoomsSlider = maxRoomsGroup.slider;
        Label maxRoomsLabel = maxRoomsGroup.label;

        CheckBox kitchenAnnex = createCheckBox("Kitchen annex");

        SliderGroup minConstructionYearGroup = createSliderGroup(1950, 2026, 2000, "", 5, 1);
        Slider minConstructionYearSlider = minConstructionYearGroup.slider;
        Label minConstructionYearLabel = minConstructionYearGroup.label;

        SliderGroup maxConstructionYearGroup = createSliderGroup(1950, 2026, 2026, "", 5, 1);
        Slider maxConstructionYearSlider = maxConstructionYearGroup.slider;
        Label maxConstructionYearLabel = maxConstructionYearGroup.label;

        SliderGroup minFloorGroup = createSliderGroup(0, 25, 0, " floor", 5, 1);
        Slider minFloorSlider = minFloorGroup.slider;
        Label minFloorLabel = minFloorGroup.label;

        SliderGroup maxFloorGroup = createSliderGroup(0, 25, 10, " floor", 5, 1);
        Slider maxFloorSlider = maxFloorGroup.slider;
        Label maxFloorLabel = maxFloorGroup.label;

        SliderGroup maxTransportDistGroup = createSliderGroup(0, 10, 1, " km", 5, 1);
        Slider maxTransportDistSlider = maxTransportDistGroup.slider;
        Label maxTransportDistLabel = maxTransportDistGroup.label;

        SliderGroup minParkingSpaceGroup = createSliderGroup(0, 5, 1, " slots", 1, 1);
        Slider minParkingSpaceSlider = minParkingSpaceGroup.slider;
        Label minParkingSpaceLabel = minParkingSpaceGroup.label;

        SliderGroup maxParkingSpaceGroup = createSliderGroup(0, 5, 1, " slots", 1, 1);
        Slider maxParkingSpaceSlider = maxParkingSpaceGroup.slider;
        Label maxParkingSpaceLabel = maxParkingSpaceGroup.label;

        CheckBox forSale = createCheckBox("Prefere for sale");
        CheckBox primaryMarket = createCheckBox("Prefer primary market");

        SliderGroup minAvgPricePerSqmGroup = createSliderGroup(0, 10000, 0, "€/sqm", 1000, 100);
        Slider minAvgPricePerSqmSlider = minAvgPricePerSqmGroup.slider;
        Label minAvgPricePerSqmLabel = minAvgPricePerSqmGroup.label;

        SliderGroup maxAvgPricePerSqmGroup = createSliderGroup(0, 10000, 5000, "€/sqm", 1000, 100);
        Slider maxAvgPricePerSqmSlider = maxAvgPricePerSqmGroup.slider;
        Label maxAvgPricePerSqmLabel = maxAvgPricePerSqmGroup.label;

        CheckBox wantElevator = createCheckBox("Want elevator");
        CheckBox preferSchool = createCheckBox("Prefer school nearby");
        CheckBox preferPark = createCheckBox("Prefer park nearby");
        CheckBox preferShop = createCheckBox("Prefer shop nearby");
        CheckBox preferPharmacy = createCheckBox("Prefer pharmacy nearby");
        CheckBox preferApartment = createCheckBox("Prefer apartment");
        CheckBox preferHouse = createCheckBox("Prefer house");

        SliderGroup minGardenAreaGroup = createSliderGroup(0, 1000, 0, "sqm", 100, 10);
        Slider minGardenAreaSlider = minGardenAreaGroup.slider;
        Label minGardenAreaLabel = minGardenAreaGroup.label;

        SliderGroup maxGardenAreaGroup = createSliderGroup(0, 1000, 100, "sqm", 100, 10);
        Slider maxGardenAreaSlider = maxGardenAreaGroup.slider;
        Label maxGardenAreaLabel = maxGardenAreaGroup.label;

        SliderGroup minNumOfFloorsGroup = createSliderGroup(0, 5, 1, " floors", 1, 1);
        Slider minNumOfFloorsSlider = minNumOfFloorsGroup.slider;
        Label minNumOfFloorsLabel = minNumOfFloorsGroup.label;

        SliderGroup maxNumOfFloorsGroup = createSliderGroup(0, 5, 3, " floors", 1, 1);
        Slider maxNumOfFloorsSlider = maxNumOfFloorsGroup.slider;
        Label maxNumOfFloorsLabel = maxNumOfFloorsGroup.label;

        CheckBox cb1 = new CheckBox("Wielkopolska");
        CheckBox cb2 = new CheckBox("Zachodniopomorskie");
        CheckBox cb3 = new CheckBox("Lubuskie");
        List<CheckBox> allNeighbourhoodCheckboxes = List.of(cb1, cb2, cb3);

        Button searchButton = new Button("Search");
        searchButton.setId("search-button");

        HBox optionsLayout = new HBox(10);
        optionsLayout.setPadding(new Insets(10, 0, 10, 0));
        optionsLayout.setId("options-bar");

        VBox resultsLayout = new VBox(10);

        NeighbourhoodGroup neighbourhoodGroup = createNeighbourhoodStage(allNeighbourhoodCheckboxes);
        Button neighbourhoodButton = neighbourhoodGroup.button;
        List<CheckBox> neighborhoodList = neighbourhoodGroup.checkboxes;

        // layout assembly

        GridPane coreCriteriasGrid = new GridPane();
        GridPane optionalCriteriasGrid = new GridPane();
        TabPane criteriaTabs = new TabPane();

        coreCriteriasGrid.setHgap(10);
        coreCriteriasGrid.setVgap(10);
        optionalCriteriasGrid.setHgap(10);
        optionalCriteriasGrid.setVgap(10);

        int coreRow = 0;

        coreCriteriasGrid.add(new Label("Price range:"), 0, coreRow);
        coreCriteriasGrid.add(minPriceSlider, 1, coreRow);
        coreCriteriasGrid.add(minPriceLabel, 2, coreRow);
        coreCriteriasGrid.add(new Label("to"), 0, ++coreRow);
        coreCriteriasGrid.add(maxPriceSlider, 1, coreRow);
        coreCriteriasGrid.add(maxPriceLabel, 2, coreRow);
        connectSliders(minPriceSlider, maxPriceSlider);

        coreRow++;
        coreCriteriasGrid.add(new Label("Area range:"), 0, ++coreRow);
        coreCriteriasGrid.add(minAreaSlider, 1, coreRow);
        coreCriteriasGrid.add(minAreaLabel, 2, coreRow);
        coreCriteriasGrid.add(new Label("to"), 0, ++coreRow);
        coreCriteriasGrid.add(maxAreaSlider, 1, coreRow);
        coreCriteriasGrid.add(maxAreaLabel, 2, coreRow);
        connectSliders(minAreaSlider, maxAreaSlider);

        coreRow++;
        coreCriteriasGrid.add(new Label("Rooms range:"), 0, ++coreRow);
        coreCriteriasGrid.add(minNofRoomsSlider, 1, coreRow);
        coreCriteriasGrid.add(minNofRoomsLabel, 2, coreRow);
        coreCriteriasGrid.add(new Label("to:"), 0, ++coreRow);
        coreCriteriasGrid.add(maxRoomsSlider, 1, coreRow);
        coreCriteriasGrid.add(maxRoomsLabel, 2, coreRow);
        connectSliders(minNofRoomsSlider, maxRoomsSlider);

        int coreCol = 4;
        int coreRowStart = 0;
        coreCriteriasGrid.add(forSale, coreCol, coreRowStart++);
        coreCriteriasGrid.add(primaryMarket, coreCol, coreRowStart++);
        coreCriteriasGrid.add(wantElevator, coreCol, coreRowStart++);
        coreCriteriasGrid.add(preferApartment, coreCol, coreRowStart++);
        coreCriteriasGrid.add(preferHouse, coreCol, coreRowStart++);

        int optionalRow = 0;

        optionalCriteriasGrid.add(new Label("Construction year:"), 0, optionalRow);
        optionalCriteriasGrid.add(minConstructionYearSlider, 1, optionalRow);
        optionalCriteriasGrid.add(minConstructionYearLabel, 2, optionalRow);
        optionalCriteriasGrid.add(new Label("to:"), 0, ++optionalRow);
        optionalCriteriasGrid.add(maxConstructionYearSlider, 1, optionalRow);
        optionalCriteriasGrid.add(maxConstructionYearLabel, 2, optionalRow);
        connectSliders(minConstructionYearSlider, maxConstructionYearSlider);

        optionalRow++;
        optionalCriteriasGrid.add(new Label("Floor:"), 0, ++optionalRow);
        optionalCriteriasGrid.add(minFloorSlider, 1, optionalRow);
        optionalCriteriasGrid.add(minFloorLabel, 2, optionalRow);
        optionalCriteriasGrid.add(new Label("to:"), 0, ++optionalRow);
        optionalCriteriasGrid.add(maxFloorSlider, 1, optionalRow);
        optionalCriteriasGrid.add(maxFloorLabel, 2, optionalRow);
        connectSliders(minFloorSlider, maxFloorSlider);

        optionalRow++;
        optionalCriteriasGrid.add(new Label("Parking space:"), 0, ++optionalRow);
        optionalCriteriasGrid.add(minParkingSpaceSlider, 1, optionalRow);
        optionalCriteriasGrid.add(minParkingSpaceLabel, 2, optionalRow);
        optionalCriteriasGrid.add(new Label("to:"), 0, ++optionalRow);
        optionalCriteriasGrid.add(maxParkingSpaceSlider, 1, optionalRow);
        optionalCriteriasGrid.add(maxParkingSpaceLabel, 2, optionalRow);
        connectSliders(minParkingSpaceSlider, maxParkingSpaceSlider);

        optionalRow++;
        optionalCriteriasGrid.add(new Label("Avg price per sqm:"), 0, ++optionalRow);
        optionalCriteriasGrid.add(minAvgPricePerSqmSlider, 1, optionalRow);
        optionalCriteriasGrid.add(minAvgPricePerSqmLabel, 2, optionalRow);
        optionalCriteriasGrid.add(new Label("to"), 0, ++optionalRow);
        optionalCriteriasGrid.add(maxAvgPricePerSqmSlider, 1, optionalRow);
        optionalCriteriasGrid.add(maxAvgPricePerSqmLabel, 2, optionalRow);
        connectSliders(minAvgPricePerSqmSlider, maxAvgPricePerSqmSlider);

        int optionalCol = 4;
        int optionalRowStart = 0;

        optionalCriteriasGrid.add(new Label("Amenities:"), optionalCol, optionalRowStart);
        optionalCriteriasGrid.add(preferSchool, optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(preferPark, optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(preferShop, optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(preferPharmacy, optionalCol, ++optionalRowStart);

        optionalRowStart++;
        optionalCriteriasGrid.add(kitchenAnnex, optionalCol, optionalRowStart);
        optionalCriteriasGrid.add(new Label("Max transport distance:"), optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(maxTransportDistSlider, optionalCol + 1, optionalRowStart);
        optionalCriteriasGrid.add(maxTransportDistLabel, optionalCol + 2, optionalRowStart);

        optionalRowStart++;
        optionalCriteriasGrid.add(new Label("Garden area:"), optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(minGardenAreaSlider, optionalCol + 1, optionalRowStart);
        optionalCriteriasGrid.add(minGardenAreaLabel, optionalCol + 2, optionalRowStart);
        optionalCriteriasGrid.add(new Label("to:"), optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(maxGardenAreaSlider, optionalCol + 1, optionalRowStart);
        optionalCriteriasGrid.add(maxGardenAreaLabel, optionalCol + 2, optionalRowStart);
        connectSliders(minGardenAreaSlider, maxGardenAreaSlider);

        optionalRowStart++;
        optionalCriteriasGrid.add(new Label("Number of floors:"), optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(minNumOfFloorsSlider, optionalCol + 1, optionalRowStart);
        optionalCriteriasGrid.add(minNumOfFloorsLabel, optionalCol + 2, optionalRowStart);
        optionalCriteriasGrid.add(new Label("to:"), optionalCol, ++optionalRowStart);
        optionalCriteriasGrid.add(maxNumOfFloorsSlider, optionalCol + 1, optionalRowStart);
        optionalCriteriasGrid.add(maxNumOfFloorsLabel, optionalCol + 2, optionalRowStart);
        connectSliders(minNumOfFloorsSlider, maxNumOfFloorsSlider);

        optionalCriteriasGrid.add(neighbourhoodButton, 0, optionalRowStart + 1);

        criteriaTabs.getTabs().add(new Tab("Core Criteria", coreCriteriasGrid));
        criteriaTabs.getTabs().add(new Tab("Optional Criteria", optionalCriteriasGrid));
        criteriaTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        coreCriteriasGrid.getStyleClass().add("grid-pane");
        optionalCriteriasGrid.getStyleClass().add("grid-pane");

        ScrollPane resultsScrollPane = new ScrollPane(resultsLayout);
        resultsScrollPane.setFitToWidth(true);
        resultsLayout.getStyleClass().add("results-layout");

        searchButton.setOnAction(e -> {
            resultsLayout.getChildren().clear();

            int minPrice = (int) minPriceSlider.getValue();
            int maxPrice = (int) maxPriceSlider.getValue();
            int minBeds = (int) minNofRoomsSlider.getValue();
            int maxBeds = (int) maxRoomsSlider.getValue();
            int minArea = (int) minAreaSlider.getValue();
            int maxArea = (int) maxAreaSlider.getValue();

            int minYear = (int) minConstructionYearSlider.getValue();
            int maxYear = (int) maxConstructionYearSlider.getValue();

            List<String> selectedNeighborhoods = neighborhoodList.stream()
                    .filter(CheckBox::isSelected)
                    .map(CheckBox::getText)
                    .collect(Collectors.toList());

            boolean neighborhoodFilterActive = !selectedNeighborhoods.isEmpty();

            int resultsFound = 0;

            for (Map<String, Object> home : HOMES) {

                int homePrice = (Integer) home.get("price");
                int homeBedrooms = (Integer) home.get("bedrooms");
                String homeProvince = (String) home.get("province");
                int homeArea = (Integer) home.get("area_sqm");
                int homeYear = (Integer) home.get("year_built");
                boolean passesPriceFilter = (homePrice >= minPrice) && (homePrice <= maxPrice);

                boolean passesBedroomsFilter = (homeBedrooms >= minBeds) && (homeBedrooms <= maxBeds);

                boolean passesAreaFilter = (homeArea >= minArea) && (homeArea <= maxArea);

                boolean passesYearFilter = (homeYear >= minYear) && (homeYear <= maxYear);

                boolean passesNeighborhoodFilter = true;
                if (neighborhoodFilterActive) {
                    passesNeighborhoodFilter = selectedNeighborhoods.contains(homeProvince);
                }

                // --- FINAL CHECK: All filters must pass ---
                if (passesPriceFilter && passesBedroomsFilter && passesAreaFilter && passesYearFilter
                        && passesNeighborhoodFilter) {

                    Label resultLabel = new Label((String) home.get("name") +
                            " | Price: " + home.get("price") + "€" +
                            " | Area: " + home.get("area_sqm") + "sqm" +
                            " | Year: " + home.get("year_built") +
                            " | Bedrooms: " + home.get("bedrooms") +
                            " | Province: " + home.get("province"));
                    resultLabel.setId("result-label");
                    resultsLayout.getChildren().add(resultLabel);
                    resultsFound++;
                }
            }

            if (resultsFound == 0) {
                resultsLayout.getChildren().add(new Label("No homes found matching your criteria."));
            }
        });

        optionsLayout.getChildren().addAll(
                new Label("Max nof offers: inf"),
                searchButton);

        resultsLayout.getChildren().addAll(
                new Label("Initial: Home 1"),
                new Label("Initial: Home 2"));

        VBox mainLayout = new VBox(10);
        mainLayout.getChildren().addAll(criteriaTabs, optionsLayout, resultsScrollPane);

        Scene scene = new Scene(mainLayout, 1000, 1000);

        scene.getStylesheets().add("styles.css");
        stage.setScene(scene);
        stage.setTitle("Home Seeker");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}