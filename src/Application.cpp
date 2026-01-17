#include "Application.h"
#include "Entities.h"
#include "Criteria.h"
#include "FileIO.h"
#include <iostream>

using namespace std;

void Application::run(int argc, char *argv[]) {
    if (argc < 29) {
        cerr << "ERROR: Missing arguments. Expected 28 filters + program name." << endl;
        return;
    }

    All_home_list all_homes;
    auto Poznan = make_unique<City>("Poznan", 538633, false);

    Neighbourhood_initializer neighbourhood_initializer;
    neighbourhood_initializer.read_csv("csv/Neighbourhoods.csv", *Poznan);

    Home_initializer initializer;
    initializer.read_csv("csv/Homes.csv", *Poznan, all_homes);

    Search_criteria criteria;

    criteria.price = Min_max_criteria(stoi(argv[1]), stoi(argv[2]), 10, 25000);
    criteria.area = Min_max_criteria(stoi(argv[3]), stoi(argv[4]), 10, 2);
    criteria.num_of_rooms = Min_max_criteria(stoi(argv[5]), stoi(argv[6]), 10, 1, 3);
    criteria.min_construction_year = Min_max_criteria(stoi(argv[7]), stoi(argv[8]), 5, 1);
    criteria.floor = Min_max_criteria(stoi(argv[9]), stoi(argv[10]), 5, 1);
    criteria.parking_spots = Min_max_criteria(stoi(argv[11]), stoi(argv[12]), 10, 1);
    criteria.avg_price_per_sqm = Min_max_criteria(stoi(argv[13]), stoi(argv[14]), 2, 50);
    criteria.garden_area = Min_max_criteria(stoi(argv[15]), stoi(argv[16]), 5, 10);
    criteria.num_of_floors = Min_max_criteria(stoi(argv[17]), stoi(argv[18]), 5, 1, 3);
    criteria.transport_dist = Min_max_criteria(0, stoi(argv[19]), 8, 100);

    criteria.want_kitchen_annex = Bool_criteria(string(argv[20]) == "1", 2);
    criteria.want_elevator = Bool_criteria(string(argv[21]) == "1", 5);
    criteria.prefer_for_sale = Bool_criteria(string(argv[22]) == "1", 25);
    criteria.prefer_primary_market = Bool_criteria(string(argv[23]) == "1", 25);

    criteria.prefer_school = Bool_criteria(string(argv[24]) == "1", 8);
    criteria.prefer_park = Bool_criteria(string(argv[25]) == "1", 8);
    criteria.prefer_shop = Bool_criteria(string(argv[26]) == "1", 8);
    criteria.prefer_pharmacy = Bool_criteria(string(argv[27]) == "1", 8);

    string n_input = argv[28];
    if (n_input != "NONE") {
        vector<string> n_list;
        stringstream ss(n_input);
        string segment;
        while (getline(ss, segment, ',')) n_list.push_back(segment);
        criteria.preferred_neighbourhoods = Vector_criteria(n_list, 20);
    }

    auto results = all_homes.sort_results(criteria);

    cout << "[" << endl;
    for (size_t i = 0; i < results.size(); ++i){
        cout << "{" << *results[i].first << "," << "\"score\": " << results[i].second << "}";
        if (i < results.size() - 1) cout << ",";
        cout << endl; 
    }
    cout << "]" << endl;
}