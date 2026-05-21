# Home Seeker Pro

A desktop property search application for the Poznań real estate market. It combines a **C++ backend** for fast property filtering and scoring with a **JavaFX frontend** for an interactive search UI.

---

## Overview

Home Seeker Pro lets users filter a dataset of Poznań apartments and houses by a wide range of criteria (price, area, rooms, floor, parking, garden, amenities, neighbourhood, and more). Each matching property receives a **compatibility score (0–100%)** based on how well it fits the search criteria, and results are displayed as ranked cards with photos, details, and a save-to-file option.

---

## Architecture

The application is split into two independently compiled components that communicate via standard I/O:

```
JavaFX Frontend (Java)
        │
        │  spawns process, passes 28 CLI arguments
        ▼
C++ Backend (cpp_backend.exe)
        │
        │  outputs JSON array to stdout
        ▼
JavaFX Frontend parses JSON and renders result cards
```

- **C++ backend** — reads CSV data, applies search criteria, scores and sorts properties, and prints a JSON array to stdout.
- **Java frontend** — renders the search form, spawns the backend as a subprocess, parses the JSON response, and displays property cards.

---

## Features

- **Core search filters:** price range, area, number of rooms
- **Advanced filters:** construction year, floor, parking spots, garden area, distance to public transport
- **Amenity filters:** kitchen annex, elevator, nearby school / park / shop / pharmacy
- **Neighbourhood selector:** multi-select popup with select/unselect all toggle
- **Property type filter:** Apartments, Houses, or Both
- **For Sale / For Rent** toggle and primary vs. secondary market filter
- **Match score** — every result shows a percentage compatibility with your criteria
- **Property photos** — loads from local `img/` directory with a fallback placeholder
- **Save offer** — writes selected properties (name, city, price, contact) to `saved_offers.txt`

---

## Project Structure

```
project-root/
├── src/
│   ├── App.java                # JavaFX UI — search form and result rendering
│   ├── PropertyModels.java     # Renderable card components (HomeResult, StatusMessage)
│   ├── PropertyService.java    # Phone number generation and offer file writing
│   ├── DataUtils.java          # JSON parser + DataPacket<T> generic wrapper
│   ├── AppTest.java            # Java unit tests
│   ├── Application.cpp         # C++ entry point — argument parsing, criteria setup
│   ├── Application.h
│   ├── Entities.cpp            # Domain model: Home, Apartment, House, Neighbourhood, City
│   ├── Entities.h
│   ├── FileIO.cpp              # CSV readers for neighbourhoods and homes
│   ├── FileIO.h
│   ├── Criteria.h              # Scoring criteria: Min_max, Bool, Vector
│   ├── main.cpp                # C++ main()
│   ├── Tests.cpp               # C++ unit tests
│   └── cpp_backend.exe         # Compiled C++ binary (generated)
├── csv/
│   ├── Homes.csv               # Property dataset
│   └── Neighbourhoods.csv      # Neighbourhood dataset with amenity flags
├── img/
│   └── <id>_plan.png           # Property images (plan / interior / exterior)
├── styles.css                  # JavaFX stylesheet
└── saved_offers.txt            # Auto-generated when offers are saved
```

---

## Prerequisites

| Tool | Version |
|------|---------|
| g++ | C++17 or later |
| Java JDK | 17 or later (tested with JDK 21) |
| JavaFX SDK | 25.0.1 (adjust path as needed) |

Download JavaFX from [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/) and extract it somewhere accessible (e.g. `C:\Java\javafx-sdk-25.0.1`).

---

## Setup & Running

### 1. Compile the C++ backend

```bash
g++ -o src/cpp_backend.exe src/main.cpp src/Application.cpp src/Entities.cpp src/FileIO.cpp
```

### 2. Set your JavaFX path

```powershell
$javafxPath = "C:\Java\javafx-sdk-25.0.1\lib"
```

> Adjust this path to wherever your JavaFX SDK is installed and to match your installed version.

### 3. Compile the Java frontend

```powershell
javac --module-path $javafxPath --add-modules javafx.controls,javafx.swing `
  src/App.java src/PropertyModels.java src/PropertyService.java src/DataUtils.java src/AppTest.java
```

### 4. Launch the application

```powershell
java --module-path $javafxPath --add-modules javafx.controls,javafx.swing -cp src App
```

---

## How It Works

1. The user configures search criteria in the UI (two tabs: *Core Criteria* and *Optional Criteria*) and clicks **Search**.
2. The frontend spawns `cpp_backend.exe` with 28 command-line arguments encoding all filter values.
3. The backend loads `csv/Neighbourhoods.csv` and `csv/Homes.csv`, scores every property against the criteria using a weighted satisfiability function, and outputs a ranked JSON array to stdout.
4. The frontend reads the JSON, parses it with `DataUtils.parseJson()`, wraps it in a `DataPacket<T>`, and renders each result as a `HomeResult` card.
5. Clicking **SAVE OFFER** on any card appends the property details and a generated contact number to `saved_offers.txt`.

### Scoring system

Each criterion carries a weight. The backend computes a score out of 100 for each property:

| Criterion | Weight |
|-----------|--------|
| Price range | 10 |
| Area range | 10 |
| Number of rooms | 10 (bonus: 3) |
| Construction year | 5 |
| Floor | 5 |
| Parking spots | 10 |
| Avg price per sqm | 2 |
| Garden area | 5 |
| Number of floors | 5 (bonus: 3) |
| Transport distance | 8 |
| Kitchen annex | 2 |
| Elevator | 5 |
| For sale / rent | 25 |
| Primary market | 25 |
| School nearby | 8 |
| Park nearby | 8 |
| Shop nearby | 8 |
| Pharmacy nearby | 8 |
| Preferred neighbourhood | 20 |

---

## Running Tests

### Java unit tests

```powershell
java -cp src AppTest
```

Tests cover: full JSON field extraction, multi-object array parsing, phone number format validation, and `DataPacket<T>` generics.

### C++ unit tests

Compile and run `Tests.cpp` separately:

```bash
g++ -o src/cpp_tests.exe src/Tests.cpp src/Entities.cpp -std=c++17
./src/cpp_tests.exe
```

Tests cover: price-per-sqm calculation, city/neighbourhood average pricing, `Min_max_criteria` / `Bool_criteria` / `Vector_criteria` scoring logic, and the end-to-end search algorithm.
