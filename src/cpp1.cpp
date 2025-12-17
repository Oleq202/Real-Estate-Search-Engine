#include <bits/stdc++.h>
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

class Image
{
private:
    string image_path;
    string description;

public:
    Image() = default;

    void set_path(const string &p) { image_path = p; }
    string get_path() const { return image_path; }

    void set_description(const string &d) { description = d; }
    string get_description() const { return description; }
};

class Home
{
public:
    int id;
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

    Neighbourhood *neighbourhood;
    unique_ptr<Image> image;

    Home() = default;
    Home(int id, string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
         int parking_spots, bool for_sale, bool is_primary_market, string web_path,
         Neighbourhood *neighbourhood, unique_ptr<Image> image)
        : id(id), address(address), price(price), area(area), num_of_rooms(num_of_rooms), has_kitchen_annex(has_kitchen_annex),
          construction_year(construction_year), transport_dist(transport_dist), parking_spots(parking_spots),
          for_sale(for_sale), is_primary_market(is_primary_market), web_path(web_path),
          neighbourhood(neighbourhood), image(move(image)) {}

    int calculate_price_per_sqm() const
    {
        if (area == 0)
            return 0;
        return price / area;
    }

    virtual ~Home() = default;
};

class Apartment : public Home
{
public:
    int floor = 0;
    bool has_elevator = 0;

    Apartment() = default;
    Apartment(int id, string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
              int parking_spots, bool for_sale, bool is_primary_market, string web_path, int floor, bool has_elevator,
              Neighbourhood *neighbourhood = nullptr, unique_ptr<Image> image = nullptr)
        : Home(id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist,
               parking_spots, for_sale, is_primary_market, web_path,
               neighbourhood, move(image)),
          floor(floor), has_elevator(has_elevator) {}
};

class House : public Home
{
public:
    int garden_area = 0;
    int num_of_floors = 0;

    House() = default;
    House(int id, string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
          int parking_spots, bool for_sale, bool is_primary_market, string web_path, int garden_area, int num_of_floors,
          Neighbourhood *neighbourhood = nullptr, unique_ptr<Image> image = nullptr)
        : Home(id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist,
               parking_spots, for_sale, is_primary_market, web_path,
               neighbourhood, move(image)),
          garden_area(garden_area), num_of_floors(num_of_floors) {}

    ~House() override = default;
};

class Neighbourhood
{
public:
    string name;
    City *city;
    vector<unique_ptr<Home>> home_list;
    bool has_school = false;
    bool has_park = false;
    bool has_shop = false;
    bool has_pharmacy = false;

    Neighbourhood() = default;
    Neighbourhood(string nm, City *cty, bool school, bool park, bool shop, bool pharmacy)
        : name(nm), city(cty), has_school(school), has_park(park), has_shop(shop), has_pharmacy(pharmacy) {}

    int avg_price()
    {
        if (home_list.empty())
            return 0;
        int total_price = 0;
        for (auto &home : home_list)
        {
            total_price += home->price;
        }
        return total_price / home_list.size();
    }
    int avg_price_per_sqm()
    {
        if (home_list.empty())
            return 0;
        int total_price_per_sqm = 0;
        for (auto &home : home_list)
        {
            total_price_per_sqm += home->calculate_price_per_sqm();
        }
        return total_price_per_sqm / home_list.size();
    }
};

class City
{
public:
    string name;
    vector<unique_ptr<Neighbourhood>> neighbourhood_list;
    int population = 0;
    bool is_capital = false;

    City() = default;
    City(string nm, int pop, bool capital) : name(nm), population(pop), is_capital(capital) {}

    int avg_price()
    {
        int total_price = 0;
        int total_homes = 0;
        for (auto &neighbourhood : neighbourhood_list)
        {
            for (auto &home : neighbourhood->home_list)
            {
                total_price += home->price;
                total_homes++;
            }
        }
        if (total_homes == 0)
            return 0;
        return total_price / total_homes;
    }
    int avg_price_per_sqm()
    {
        int total_price_per_sqm = 0;
        int total_homes = 0;
        for (auto &neighbourhood : neighbourhood_list)
        {
            for (auto &home : neighbourhood->home_list)
            {
                total_price_per_sqm += home->calculate_price_per_sqm();
                total_homes++;
            }
        }
        if (total_homes == 0)
            return 0;
        return total_price_per_sqm / total_homes;
    }
};

class Min_max_criteria
{
public:
    int min_value = 0;
    int max_value = INT_MAX;
    int priority = 5;
    int step = 1;
    int change_iter = 1;

    Min_max_criteria() = default;
    Min_max_criteria(int min_value, int max_value, int priority, int step, int change_iter = 1)
        : min_value(min_value), max_value(max_value), priority(priority), step(step), change_iter(change_iter) {}

    int score(int value) const
    {
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

class Bool_criteria
{
public:
    bool preferred = false;
    int priority = 5;

    Bool_criteria() = default;
    Bool_criteria(bool preferred, int priority) : preferred(preferred), priority(priority) {}

    int score(bool actual) const
    {
        return (actual == preferred) ? priority : 0;
    }

    ~Bool_criteria() = default;
};

class Vector_criteria
{
public:
    vector<string> list;
    int priority = 5;

    Vector_criteria() = default;
    Vector_criteria(const vector<string> &list, int priority) : list(list), priority(priority) {}

    int score(const string &value) const
    {
        for (auto &v : list)
            if (value == v)
                return priority;
        return 0;
    }

    ~Vector_criteria() = default;
};

class Search_criteria
{
public:
    template <typename Criteria, typename Value>
    inline void evaluate_criterion(const Criteria &criterion, const Value &value,
                                   int &score, int &max_score) const
    {
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

    int calculate_satisfiability(Home *home)
    {
        if (!home)
            return 0;

        Apartment *apt = dynamic_cast<Apartment *>(home);
        House *house = dynamic_cast<House *>(home);

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

        if (apt)
        {
            evaluate_criterion(prefer_apartment, true, score, max_score);
            evaluate_criterion(floor, apt->floor, score, max_score);
            evaluate_criterion(want_elevator, apt->has_elevator, score, max_score);
        }

        if (house)
        {
            evaluate_criterion(garden_area, house->garden_area, score, max_score);
            evaluate_criterion(num_of_floors, house->num_of_floors, score, max_score);
            evaluate_criterion(prefer_house, true, score, max_score);
        }

        if (home->neighbourhood)
        {
            evaluate_criterion(preferred_neighbourhoods, home->neighbourhood->name, score, max_score);
            evaluate_criterion(prefer_school, home->neighbourhood->has_school, score, max_score);
            evaluate_criterion(prefer_park, home->neighbourhood->has_park, score, max_score);
            evaluate_criterion(prefer_shop, home->neighbourhood->has_shop, score, max_score);
            evaluate_criterion(prefer_pharmacy, home->neighbourhood->has_pharmacy, score, max_score);
            if (home->neighbourhood->city)
            {
                evaluate_criterion(preferred_cities, home->neighbourhood->city->name, score, max_score);
            }
        }

        return (max_score == 0) ? 0 : (score * 100) / max_score;
    }

    ~Search_criteria() = default;
};

class All_home_list
{
public:
    vector<Home *> homes;

    All_home_list() = default;

    vector<pair<Home *, int>> sort_results(Search_criteria &criteria)
    {
        vector<pair<Home *, int>> scored;

        for (Home *h : homes)
        {
            int score = criteria.calculate_satisfiability(h);
            scored.push_back({h, score});
        }

        sort(scored.begin(), scored.end(), [](const auto &a, const auto &b)
             { return a.second > b.second; });
        return scored;
    }
};

class Home_initializer
{
private:
    bool parse_bool(const string &str)
    {
        return str == "TRUE";
    }

    Neighbourhood *find_neighbourhood(City &city, const string &name)
    {
        for (auto &neighbourhood : city.neighbourhood_list)
        {
            if (neighbourhood->name == name)
            {
                return neighbourhood.get();
            }
        }
        return nullptr;
    }

    bool file_exists(const string &name)
    {
        ifstream f(name.c_str());
        return f.good();
    }

public:
    void read_csv(const string &filename, City &city, All_home_list &all_home_list)
    {
        ifstream file(filename);
        string line;

        if (!file.is_open())
        {
            cout << "ERROR: Could not open file: " << filename << endl;
            return;
        }

        getline(file, line);

        while (getline(file, line))
        {
            if (line.empty())
                continue;

            stringstream ss(line);
            string item;
            vector<string> row;

            while (getline(ss, item, ','))
            {
                row.push_back(item);
            }

            if (row.size() < 14)
            {
                continue;
            }

            try
            {
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

                string image_plan_path = "img/" + to_string(id) + ".plan.png";
                string image_interior_path = "img/" + to_string(id) + ".interior.png";
                string image_exterior_path = "img/" + to_string(id) + ".exterior.png";
                unique_ptr<Image> image = make_unique<Image>();
                if (file_exists(image_plan_path))
                {
                    image->set_path(image_plan_path);
                    image->set_description("Floor Plan");
                }
                else if (file_exists(image_interior_path))
                {
                    image->set_path(image_interior_path);
                    image->set_description("Interior View");
                }
                else if (file_exists(image_exterior_path))
                {
                    image->set_path(image_exterior_path);
                    image->set_description("Exterior View");
                }
                else
                {
                    image = nullptr;
                }

                if (neighbourhood)
                {
                    if (type == "apartment")
                    {
                        int floor = stoi(col_12);
                        bool has_elevator = parse_bool(col_13);
                        auto apartment = make_unique<Apartment>(id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist, parking_spots, for_sale, is_primary_market, web_path, floor, has_elevator, neighbourhood, move(image));
                        all_home_list.homes.push_back(apartment.get());
                        neighbourhood->home_list.push_back(move(apartment));
                    }
                    else if (type == "house")
                    {
                        int garden_area = stoi(col_12);
                        int num_of_floors = stoi(col_13);
                        auto house = make_unique<House>(id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist, parking_spots, for_sale, is_primary_market, web_path, garden_area, num_of_floors, neighbourhood, move(image));
                        all_home_list.homes.push_back(house.get());
                        neighbourhood->home_list.push_back(move(house));
                    }
                }
                else
                {
                    cout << "Warning: Neighbourhood not found: " << neighbourhood_name << endl;
                }
            }
            catch (const exception &e)
            {
                cout << "Error parsing line: " << line << "\nReason: " << e.what() << endl;
            }
        }
    }
};

class Neighbourhood_initializer
{
private:
    bool parse_bool(const string &str)
    {
        return str == "TRUE";
    }

public:
    void read_csv(const string &filename, City &city)
    {
        ifstream file(filename);
        string line;

        if (!file.is_open())
        {
            cout << "ERROR: Could not open file: " << filename << endl;
            return;
        }

        getline(file, line);

        while (getline(file, line))
        {
            if (line.empty())
                continue;

            stringstream ss(line);
            string item;
            vector<string> row;

            while (getline(ss, item, ','))
            {
                row.push_back(item);
            }

            try
            {
                string name = row[0];
                bool has_school = parse_bool(row[1]);
                bool has_park = parse_bool(row[2]);
                bool has_shop = parse_bool(row[3]);
                bool has_pharmacy = parse_bool(row[4]);

                auto neighbourhood = make_unique<Neighbourhood>(name, &city, has_school, has_park, has_shop, has_pharmacy);
                city.neighbourhood_list.push_back(move(neighbourhood));
            }
            catch (const exception &e)
            {
                cout << "Error parsing line: " << line << "\nReason: " << e.what() << endl;
            }
        }
    }
};

// modified
int main(int argc, char *argv[])
{
    // CHANGE 1: Comprehensive Argument Validation
    // Expecting:
    // 1-2: Price(min/max), 3-4: Area(min/max), 5-6: Rooms(min/max),
    // 7-8: Year(min/max), 9-10: Floor(min/max), 11-12: Parking(min/max),
    // 13-14: AvgPrice(min/max), 15-16: Garden(min/max), 17-18: HouseFloors(min/max),
    // 19: TransportDist(val), 20: Kitchen(bool), 21: Elevator(bool),
    // 22: ForSale(bool), 23: PrimaryMarket(bool), 24: NeighbourhoodList(string)
    if (argc < 25)
    {
        cerr << "ERROR: Missing arguments. Expected 24 filters + program name." << endl;
        return 1;
    }

    All_home_list all_homes;

    auto Poznan = make_unique<City>("Poznan", 538633, false);

    Neighbourhood_initializer neighbourhood_initializer;
    // modified
    neighbourhood_initializer.read_csv("csv/Neighbourhoods.csv", *Poznan);

    Home_initializer initializer;
    // modified
    initializer.read_csv("csv/Homes.csv", *Poznan, all_homes);

    Search_criteria criteria;
    // criteria.price = Min_max_criteria(350000, 380000, 10, 25000);
    // criteria.area = Min_max_criteria(60, 70, 3, 2);
    // criteria.num_of_rooms = Min_max_criteria(3, 3, 8, 1, 3);
    // criteria.transport_dist = Min_max_criteria(0, 200, 10, 100);
    // criteria.prefer_apartment = Bool_criteria(true, 5);
    // criteria.want_elevator = Bool_criteria(true, 4);

    // Core Criteria (Indices 1-6) - Priority 10
    criteria.price = Min_max_criteria(stoi(argv[1]), stoi(argv[2]), 10, 25000);
    criteria.area = Min_max_criteria(stoi(argv[3]), stoi(argv[4]), 10, 2);
    criteria.num_of_rooms = Min_max_criteria(stoi(argv[5]), stoi(argv[6]), 10, 1, 3);

    // Optional Criteria (Indices 7-12) - Priority 5
    criteria.min_construction_year = Min_max_criteria(stoi(argv[7]), stoi(argv[8]), 5, 1);
    criteria.floor = Min_max_criteria(stoi(argv[9]), stoi(argv[10]), 5, 1);
    criteria.parking_spots = Min_max_criteria(stoi(argv[11]), stoi(argv[12]), 5, 1);

    // Extended Criteria (Indices 13-18)
    criteria.avg_price_per_sqm = Min_max_criteria(stoi(argv[13]), stoi(argv[14]), 2, 50);
    criteria.garden_area = Min_max_criteria(stoi(argv[15]), stoi(argv[16]), 5, 10);
    criteria.num_of_floors = Min_max_criteria(stoi(argv[17]), stoi(argv[18]), 5, 1, 3);

    // Single Value (Index 19)
    // We treat this as a "Maximum" distance. Lower is better.
    criteria.transport_dist = Min_max_criteria(0, stoi(argv[19]), 8, 100);

    // Booleans (Indices 20-23) - 1 is True, 0 is False
    criteria.want_kitchen_annex = Bool_criteria(string(argv[20]) == "1", 2);
    criteria.want_elevator = Bool_criteria(string(argv[21]) == "1", 5);
    criteria.prefer_for_sale = Bool_criteria(string(argv[22]) == "1", 5);
    criteria.prefer_primary_market = Bool_criteria(string(argv[23]) == "1", 5);

    // Neighbourhoods (Index 24)
    string n_input = argv[24];
    if (n_input != "NONE")
    {
        vector<string> n_list;
        stringstream ss(n_input);
        string segment;
        while (getline(ss, segment, ','))
        {
            n_list.push_back(segment);
        }
        criteria.preferred_neighbourhoods = Vector_criteria(n_list, 15);
    }

    auto results = all_homes.sort_results(criteria);

    // for (const auto &result : results)
    // {
    //     Home *home = result.first;
    //     int score = result.second;
    //     cout << "Address: " << home->address << ", Score: " << score << "%\n";
    // }

    cout << "[" << endl;
    for (size_t i = 0; i < results.size(); ++i)
    {
        Home *h = results[i].first;
        int score = results[i].second;

        Apartment *apt = dynamic_cast<Apartment *>(h);
        House *house = dynamic_cast<House *>(h);

        string raw_path = (h->image ? h->image->get_path() : "https://picsum.photos/id/1/200/140");
        string final_path = raw_path;

        // If it's a local path (e.g., "img/1.plan.png"), prefix it for Java
        if (raw_path.find("img/") == 0)
        {
            final_path = "file:" + raw_path;
        }

        cout << "  {"
             << "\"name\": \"" << h->address << "\","
             << "\"price\": " << h->price << ","
             << "\"area_sqm\": " << h->area << ","
             << "\"rooms\": " << h->num_of_rooms << ","
             << "\"year_built\": " << h->construction_year << ","
             << "\"city\": \"" << (h->neighbourhood ? h->neighbourhood->city->name : "Unknown") << "\","
             << "\"province\": \"" << (h->neighbourhood ? h->neighbourhood->name : "Unknown") << "\","
             << "\"floor\": " << (apt ? apt->floor : 0) << ","
             << "\"parking\": " << h->parking_spots << ","
             << "\"garden\": " << (house ? house->garden_area : 0) << ","
             << "\"image\": \"" << final_path << "\","
             << "\"score\": " << score
             << "}";

        if (i < results.size() - 1)
            cout << ",";
        cout << endl;
    }
    cout << "]" << endl;

    return 0;
}
