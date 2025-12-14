#include<bits/stdc++.h>
using namespace std;

class Home;
class Apartment;
class House;
class Neighbourhood;
class City;
class Image;
class Min_max_criteria;
class Bool_criteria;
class Vector_criteria;
class Search_criteria;
class All_home_list;
class Home_initializer;
class Neighbourhood_initializer;

class Contact{
    public:
    string phone_number;
    string email;

    Contact() = default;
    Contact(const string& phone_number, const string& email) : phone_number(phone_number), email(email) {}
};

class Image{
    private:
    string image_path;
    string description;
    public:

    Image() = default;
    Image(const string& image_path, const string& description) : image_path(image_path), description(description) {}

    void set_path(const string& p) {image_path = p;}
    string get_path() const {return image_path;}

    void set_description(const string& d) {description = d;}
    string get_description() const {return description;}
};

class Home{
    public:
    string address;
    int price = 0;
    int area = 0;
    int num_of_rooms = 0;
    bool has_kitchen_annex = false;
    int construction_year = 0;
    int transport_dist = 0;
    int parking_spots = 0;
    bool for_sale = true;
    bool is_primary_market = false;
    string web_path;

    Neighbourhood* neighbourhood = nullptr;
    Contact* contact = nullptr;
    unique_ptr<Image> image;

    Home() = default;
    Home(string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
         int parking_spots, bool for_sale, bool is_primary_market, string web_path,
         Neighbourhood* neighbourhood, unique_ptr<Image> image)
        : address(address), price(price), area(area), num_of_rooms(num_of_rooms), has_kitchen_annex(has_kitchen_annex),
          construction_year(construction_year), transport_dist(transport_dist), parking_spots(parking_spots),
          for_sale(for_sale), is_primary_market(is_primary_market), web_path(web_path),
          neighbourhood(neighbourhood), image(move(image)) {}

    int calculate_price_per_sqm() const {
        if (area == 0) return 0;
        return price/area;
    }

    virtual ~Home() = default;
};

class Apartment: public Home{
    public:
    int floor = 0;
    bool has_elevator = 0;

    Apartment() = default;
    Apartment(string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
              int parking_spots, bool for_sale, bool is_primary_market, string web_path, int floor, bool has_elevator,
              Neighbourhood* neighbourhood = nullptr, unique_ptr<Image> image = nullptr)
        : Home(address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist,
               parking_spots, for_sale, is_primary_market, web_path,
               neighbourhood, move(image)),
          floor(floor), has_elevator(has_elevator) {}

};

class House: public Home{
    public:
    int garden_area = 0;
    int num_of_floors = 0;

    House() = default;
    House(string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
          int parking_spots, bool for_sale, bool is_primary_market, string web_path, int garden_area, int num_of_floors,
          Neighbourhood* neighbourhood = nullptr, unique_ptr<Image> image = nullptr)
        : Home(address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist,
               parking_spots, for_sale, is_primary_market, web_path,
               neighbourhood, move(image)),
          garden_area(garden_area), num_of_floors(num_of_floors) {}

    ~House() override = default;
};

class Neighbourhood{
    public:
    string name;
    City* city;
    vector<unique_ptr<Home>> home_list;
    bool has_school = false;
    bool has_park = false;
    bool has_shop = false;
    bool has_pharmacy = false;

    Neighbourhood() = default;
    Neighbourhood(string nm, City* cty, bool school, bool park, bool shop, bool pharmacy)
        : name(nm), city(cty), has_school(school), has_park(park), has_shop(shop), has_pharmacy(pharmacy) {}

    int avg_price(){
        if (home_list.empty()) return 0;
        int total_price = 0;
        for (auto& home : home_list){
            total_price += home->price;
        }
        return total_price/home_list.size();
    }
    int avg_price_per_sqm(){
        if (home_list.empty()) return 0;
        int total_price_per_sqm = 0;
        for (auto& home : home_list){
            total_price_per_sqm += home->calculate_price_per_sqm();
        }
        return total_price_per_sqm/home_list.size();
    }
};

class City{
    public:
    string name;
    vector<unique_ptr<Neighbourhood>> neighbourhood_list;
    int population = 0;
    bool is_capital = false;

    City() = default;
    City(string nm, int pop, bool capital) : name(nm), population(pop), is_capital(capital) {}

    int avg_price(){
        int total_price = 0;
        int total_homes = 0;
        for (auto& neighbourhood : neighbourhood_list){
            for (auto& home : neighbourhood->home_list){
                total_price += home->price;
                total_homes++;
            }
        }
        if (total_homes == 0) return 0;
        return total_price/total_homes;
    }
    int avg_price_per_sqm(){
        int total_price_per_sqm = 0;
        int total_homes = 0;
        for (auto& neighbourhood : neighbourhood_list){
            for (auto& home : neighbourhood->home_list){
                total_price_per_sqm += home->calculate_price_per_sqm();
                total_homes++;
            }
        }
        if (total_homes == 0) return 0;
        return total_price_per_sqm/total_homes;
    }
};

class Min_max_criteria{
    public:
    int min_value = 0;
    int max_value = INT_MAX;
    int priority = 5;
    int step = 1;
    int change_iter = 1;

    Min_max_criteria() = default;
    Min_max_criteria(int min_value, int max_value, int priority, int step, int change_iter = 1)
        : min_value(min_value), max_value(max_value), priority(priority), step(step), change_iter(change_iter) {}

    int score(int value) const {
        if (value >= min_value && value <= max_value) 
            return priority;
        
        int distance = 0;

        if (value < min_value)
            distance = min_value - value;
        else
            distance = value - max_value;
        
        int penalty_steps = distance / step;
        int score = priority - (penalty_steps * change_iter);
        return max(score, 0);
    }

    ~Min_max_criteria() = default;
};

class Bool_criteria{
    public:
    bool preferred = false;
    int priority = 5;

    Bool_criteria() = default;
    Bool_criteria(bool preferred, int priority) : preferred(preferred), priority(priority) {}

    int score(bool actual) const {
        return (actual == preferred) ? priority : 0; 
    }

    ~Bool_criteria() = default;
};

class Vector_criteria{
    public:
    vector<string> list;
    int priority = 5;

    Vector_criteria() = default;
    Vector_criteria(const vector<string>& list, int priority) : list(list), priority(priority) {}

    int score(const string& value) const {
        for (auto& v : list)
            if (value == v) 
                return priority;
        return 0;
    }

    ~Vector_criteria() = default;
};

class Search_criteria{
    public:
    template <typename Criteria, typename Value>
    inline void evaluate_criterion(const Criteria& criterion, const Value& value,
                                   int& score, int& max_score) const {
        max_score += criterion.priority;
        score += criterion.score(value);
    }
    
    Min_max_criteria price{0, INT_MAX, 0, 25000};
    Min_max_criteria avg_price_per_sqm{0, INT_MAX, 0, 50};
    Min_max_criteria area{0, INT_MAX, 0, 2};
    Min_max_criteria num_of_rooms{0, INT_MAX, 0, 1, 3};
    Min_max_criteria min_construction_year{0, INT_MAX, 0, 4};
    Min_max_criteria transport_dist{0, INT_MAX, 0, 100};
    Min_max_criteria parking_spots{0, INT_MAX, 0, 1, 3};

    Bool_criteria want_kitchen_annex{false, 0};
    Bool_criteria want_elevator{false, 0};
    Bool_criteria prefer_for_sale{false, 0};
    Bool_criteria prefer_primary_market{false, 0};
    Bool_criteria prefer_school{false, 0};
    Bool_criteria prefer_park{false, 0};
    Bool_criteria prefer_shop{false, 0};
    Bool_criteria prefer_pharmacy{false, 0};

    Vector_criteria preferred_neighbourhoods{{}, 0};
    Vector_criteria preferred_cities{{}, 0};

    Bool_criteria prefer_apartment{false, 0};
    Min_max_criteria floor{0, INT_MAX, 0, 1};

    Bool_criteria prefer_house{false, 0};
    Min_max_criteria garden_area{0, INT_MAX, 0, 10};
    Min_max_criteria num_of_floors{0, INT_MAX, 0, 1, 3};

    Search_criteria() = default;

    int calculate_satisfiability(Home* home){
        if (!home) return 0;

        Apartment* apt = dynamic_cast<Apartment*>(home);
        House* house = dynamic_cast<House*>(home);

        int score = 0;
        int max_score = 0;

        evaluate_criterion(price, home->price, score, max_score);
        evaluate_criterion(avg_price_per_sqm, home->calculate_price_per_sqm(), score, max_score);
        evaluate_criterion(area, home->area, score, max_score);
        evaluate_criterion(num_of_rooms, home->num_of_rooms, score, max_score);
        evaluate_criterion(min_construction_year, home->construction_year, score, max_score);
        evaluate_criterion(transport_dist, home->transport_dist, score, max_score);
        evaluate_criterion(parking_spots, home->parking_spots, score, max_score);
        evaluate_criterion(want_kitchen_annex, home->has_kitchen_annex, score, max_score);
        evaluate_criterion(prefer_for_sale, home->for_sale, score, max_score);
        evaluate_criterion(prefer_primary_market, home->is_primary_market, score, max_score);

        if (apt){
            evaluate_criterion(prefer_apartment, true, score, max_score);
            evaluate_criterion(floor, apt->floor, score, max_score);
            evaluate_criterion(want_elevator, apt->has_elevator, score, max_score);
        }

        if (house){
            evaluate_criterion(garden_area, house->garden_area, score, max_score);
            evaluate_criterion(num_of_floors, house->num_of_floors, score, max_score);
            evaluate_criterion(prefer_house, true, score, max_score);
        }
        
        if (home->neighbourhood){
            evaluate_criterion(preferred_neighbourhoods, home->neighbourhood->name, score, max_score);
            evaluate_criterion(prefer_school, home->neighbourhood->has_school, score, max_score);
            evaluate_criterion(prefer_park, home->neighbourhood->has_park, score, max_score);
            evaluate_criterion(prefer_shop, home->neighbourhood->has_shop, score, max_score);
            evaluate_criterion(prefer_pharmacy, home->neighbourhood->has_pharmacy, score, max_score);
            if (home->neighbourhood->city){
                evaluate_criterion(preferred_cities, home->neighbourhood->city->name, score, max_score);
            }
        }

        return (max_score == 0) ? 0 : (score * 100) / max_score;
    }

    ~Search_criteria() = default;

};

class All_home_list{
    public:
    vector<Home*> homes;

    All_home_list() = default;

    vector<pair<Home*, int>> sort_results(Search_criteria& criteria){
        vector<pair<Home*, int>> scored;

        for (Home* h : homes){
            int score = criteria.calculate_satisfiability(h);
            scored.push_back({h, score});
        }

        sort(scored.begin(), scored.end(), [](const auto& a, const auto& b){return a.second > b.second;});
        return scored;
    }

};

class Home_initializer{
    private:
    bool parse_bool(const string& str) {
        return str == "TRUE";
    }
    
    Neighbourhood* find_neighbourhood(City& city, const string& name) {
        for (auto& neighbourhood : city.neighbourhood_list) {
            if (neighbourhood->name == name) {
                return neighbourhood.get();
            }
        }
        return nullptr;
    }

    public:
    void read_csv(const string& filename, City& city, All_home_list& all_home_list) {
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

            while (getline(ss, item, ',')) {
                row.push_back(item);
            }
            
            if (row.size() < 14) { 
                continue; 
            }

            try {
                string type = row[0];
                string address = row[1];
                int price = stoi(row[2]);
                int area = stoi(row[3]);
                int num_of_rooms = stoi(row[4]);
                bool has_kitchen_annex = parse_bool(row[5]);
                int construction_year = stoi(row[6]);
                int transport_dist = stoi(row[7]);
                int parking_spots = stoi(row[8]);
                bool for_sale = parse_bool(row[9]);
                bool is_primary_market = parse_bool(row[10]);
                
                string col_11 = row[11];
                string col_12 = row[12];
                string neighbourhood_name = row[13];
                string web_path = (row.size() > 14) ? row[14] : ""; 

                Neighbourhood* neighbourhood = find_neighbourhood(city, neighbourhood_name);
                
                if (neighbourhood) {
                    if (type == "apartment") {
                        int floor = stoi(col_11);
                        bool has_elevator = parse_bool(col_12);
                        auto apartment = make_unique<Apartment>(address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist, parking_spots, for_sale, is_primary_market, web_path, floor, has_elevator, neighbourhood);
                        all_home_list.homes.push_back(apartment.get());
                        neighbourhood->home_list.push_back(move(apartment));
                    } else if (type == "house") {
                        int garden_area = stoi(col_11);
                        int num_of_floors = stoi(col_12);
                        auto house = make_unique<House>(address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist, parking_spots, for_sale, is_primary_market, web_path, garden_area, num_of_floors, neighbourhood);
                        all_home_list.homes.push_back(house.get());
                        neighbourhood->home_list.push_back(move(house));
                    }
                } else {
                    cout << "Warning: Neighbourhood not found: " << neighbourhood_name << endl;
                }
            } catch (const exception& e) {
                cout << "Error parsing line: " << line << "\nReason: " << e.what() << endl;
            }
        }
    }
};

class Neighbourhood_initializer{
    private:
    bool parse_bool(const string& str) {
        return str == "TRUE";
    }
    public:
    void read_csv(const string& filename, City& city) {
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

            while (getline(ss, item, ',')) {
                row.push_back(item);
            }

            try {
                string name = row[0];
                bool has_school = parse_bool(row[1]);
                bool has_park = parse_bool(row[2]);
                bool has_shop = parse_bool(row[3]);
                bool has_pharmacy = parse_bool(row[4]);

                auto neighbourhood = make_unique<Neighbourhood>(name, &city, has_school, has_park, has_shop, has_pharmacy);
                city.neighbourhood_list.push_back(move(neighbourhood));
            } catch (const exception& e) {
                cout << "Error parsing line: " << line << "\nReason: " << e.what() << endl;
            }
        }
    }
};


int main() {
    All_home_list all_homes;

    auto Poznan = make_unique<City>("Poznan", 538633, false);

    Neighbourhood_initializer neighbourhood_initializer;
    neighbourhood_initializer.read_csv("Neighbourhoods.csv", *Poznan);

    Home_initializer initializer;
    initializer.read_csv("Homes.csv", *Poznan, all_homes);

    Search_criteria criteria;
    criteria.price = Min_max_criteria(350000, 380000, 10, 25000);
    criteria.area = Min_max_criteria(60, 70, 3, 2);
    criteria.num_of_rooms = Min_max_criteria(3, 3, 8, 1, 3);
    criteria.transport_dist = Min_max_criteria(0, 200, 10, 100);
    criteria.prefer_apartment = Bool_criteria(true, 5);
    criteria.want_elevator = Bool_criteria(true, 4);

    auto results = all_homes.sort_results(criteria);

    for (const auto& result : results){
        Home* home = result.first;
        int score = result.second;
        cout << "Address: " << home->address << ", Score: " << score << "%\n";
    }
    
    return 0;
}

