#include "FileIO.h"

vector<pair<Home *, int>> All_home_list::sort_results(Search_criteria &criteria) {
    vector<pair<Home *, int>> scored;
    for (Home *h : homes) {
        int score = criteria.calculate_satisfiability(h);
        scored.push_back({h, score});
    }
    sort(scored.begin(), scored.end(), [](const auto &a, const auto &b) { return a.second > b.second; });
    return scored;
}

bool Neighbourhood_initializer::parse_bool(const string &str) { return str == "TRUE"; }

void Neighbourhood_initializer::read_csv(const string &filename, City &city) {
    ifstream file(filename);
    string line;
    if (!file.is_open()) {
        cout << "ERROR: Could not open file: " << filename << endl;
        return;
    }
    getline(file, line);
    while (getline(file, line)) {
        if (line.empty()) continue;
        stringstream ss(line);
        string item;
        vector<string> row;
        while (getline(ss, item, ',')) row.push_back(item);
        
        try {
            string name = row[0];
            bool has_school = parse_bool(row[2]);
            bool has_park = parse_bool(row[3]);
            bool has_shop = parse_bool(row[4]);
            bool has_pharmacy = parse_bool(row[5]);
            auto neighbourhood = make_unique<Neighbourhood>(name, &city, has_school, has_park, has_shop, has_pharmacy);
            city.neighbourhood_list.push_back(move(neighbourhood));
        } catch (const exception &e){
            cout << "Error parsing line: " << line << "\nReason: " << e.what() << endl;
        }
    }
}

bool Home_initializer::parse_bool(const string &str) { return str == "TRUE"; }

Neighbourhood *Home_initializer::find_neighbourhood(City &city, const string &name) {
    for (auto &neighbourhood : city.neighbourhood_list) {
        if (neighbourhood->name == name) return neighbourhood.get();
    }
    return nullptr;
}

bool Home_initializer::file_exists(const string &name) {
    ifstream f(name.c_str());
    return f.good();
}

void Home_initializer::read_csv(const string &filename, City &city, All_home_list &all_home_list) {
    ifstream file(filename);
    string line;
    if (!file.is_open()) {
        cout << "ERROR: Could not open file: " << filename << endl;
        return;
    }
    getline(file, line);
    while (getline(file, line)) {
        if (line.empty()) continue;
        stringstream ss(line);
        string item;
        vector<string> row;
        while (getline(ss, item, ',')) row.push_back(item);
        if (row.size() < 14) continue;

        try {
            int id = stoi(row[0]);
            string type = row[1];
            string address = row[2];
            int price = stoi(row[3]);
            int area = stoi(row[4]);
            int num_of_rooms = stoi(row[5]);
            bool has_kitchen_annex = parse_bool(row[6]);
            int construction_year = stoi(row[7]);
            int transport_dist = stoi(row[8]);
            int parking_spots = stoi(row[9]);
            bool for_sale = parse_bool(row[10]);
            bool is_primary_market = parse_bool(row[11]);
            string col_12 = row[12];
            string col_13 = row[13];
            string neighbourhood_name = row[14];
            string web_path = (row.size() > 15) ? row[15] : "";

            Neighbourhood *neighbourhood = find_neighbourhood(city, neighbourhood_name);
            string image_plan_path = "img/" + to_string(id) + "_plan.png";
            string image_interior_path = "img/" + to_string(id) + "_interior.png";
            string image_exterior_path = "img/" + to_string(id) + "_exterior.png";
            unique_ptr<Image> image = make_unique<Image>();
            
            if (file_exists(image_plan_path)) { image->set_path(image_plan_path); image->set_description("Floor Plan"); }
            else if (file_exists(image_interior_path)) { image->set_path(image_interior_path); image->set_description("Interior View"); }
            else if (file_exists(image_exterior_path)) { image->set_path(image_exterior_path); image->set_description("Exterior View"); }
            else { image = nullptr; }

            if (neighbourhood) {
                if (type == "apartment") {
                    int floor = stoi(col_12);
                    bool has_elevator = parse_bool(col_13);
                    auto apartment = make_unique<Apartment>(id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist, parking_spots, for_sale, is_primary_market, web_path, floor, has_elevator, neighbourhood, move(image));
                    all_home_list.homes.push_back(apartment.get());
                    neighbourhood->home_list.push_back(move(apartment));
                } else if (type == "house") {
                    int garden_area = stoi(col_12);
                    int num_of_floors = stoi(col_13);
                    auto house = make_unique<House>(id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist, parking_spots, for_sale, is_primary_market, web_path, garden_area, num_of_floors, neighbourhood, move(image));
                    all_home_list.homes.push_back(house.get());
                    neighbourhood->home_list.push_back(move(house));
                }
            }
        } catch (const exception &e){
            cout << "Error parsing line: " << line << "\nReason: " << e.what() << endl;
        }
    }
}