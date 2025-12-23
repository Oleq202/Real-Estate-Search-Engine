#include <iostream>
#include <cassert>
#include <cmath>
#include <memory>
#include "Entities.h"
#include "Criteria.h"

using namespace std;

void assert_test(bool condition, const string &test_name) {
    if (condition) {
        cout << "[PASS] " << test_name << endl;
    } else {
        cout << "[FAIL] " << test_name << endl;
        exit(1);
    }
}

void test_home_logic() {
    cout << "--- Testing Home Logic ---" << endl;

    auto apt = make_unique<Apartment>();
    apt->price = 500000;
    apt->area = 50;
    assert_test(apt->calculate_price_per_sqm() == 10000, "Calculate Price Per Sqm");

    apt->area = 0;
    assert_test(apt->calculate_price_per_sqm() == 0, "Price Per Sqm (Zero Area)");
}

void test_city_neighbourhood_logic() {
    cout << "--- Testing City/Neighbourhood Logic ---" << endl;

    City poznan("Poznan", 500000, false);
    auto n1 = make_unique<Neighbourhood>("Centrum", &poznan, true, true, true, true);
    
    auto h1 = make_unique<Apartment>();
    h1->price = 400000; 
    h1->area = 40;

    auto h2 = make_unique<House>();
    h2->price = 800000;
    h2->area = 100;

    n1->home_list.push_back(move(h1));
    n1->home_list.push_back(move(h2));

    poznan.neighbourhood_list.push_back(move(n1));

    assert_test(poznan.avg_price() == 600000, "City Average Price");

    assert_test(poznan.avg_price_per_sqm() == 9000, "City Average Price Per Sqm");
}

void test_criteria_logic() {
    cout << "--- Testing Criteria Logic ---" << endl;

    Min_max_criteria price_crit(100, 200, 10, 10); 
    assert_test(price_crit.score(150) == 10, "Min_Max Score (In Range)");

    assert_test(price_crit.score(50) == 5, "Min_Max Score (Below Range)");

    Bool_criteria elevator_crit(true, 5);
    assert_test(elevator_crit.score(true) == 5, "Bool Criteria (Match)");
    assert_test(elevator_crit.score(false) == 0, "Bool Criteria (Mismatch)");

    vector<string> preferred = {"Wilda", "Jezyce"};
    Vector_criteria hood_crit(preferred, 20);
    assert_test(hood_crit.score("Wilda") == 20, "Vector Criteria (Match)");
    assert_test(hood_crit.score("Rataje") == 0, "Vector Criteria (Mismatch)");
}

void test_search_engine() {
    cout << "--- Testing Search Engine ---" << endl;

    auto home = new Apartment();
    home->price = 300000;
    home->area = 50;
    home->num_of_rooms = 2;
    home->has_elevator = true;

    Search_criteria criteria;
    criteria.price = Min_max_criteria(200000, 400000, 10, 1000); 
    criteria.area = Min_max_criteria(40, 60, 10, 1);             
    criteria.want_elevator = Bool_criteria(true, 5);             

    int result = criteria.calculate_satisfiability(home);
    
    assert_test(result > 0, "Search Algorithm Integration");
    assert_test(result <= 100, "Search Result Valid Percentage");

    delete home;
}

int main() {
    cout << "==========================" << endl;
    cout << "   RUNNING UNIT TESTS     " << endl;
    cout << "==========================" << endl;

    test_home_logic();
    test_city_neighbourhood_logic();
    test_criteria_logic();
    test_search_engine();

    cout << "==========================" << endl;
    cout << "   ALL TESTS PASSED       " << endl;
    cout << "==========================" << endl;
    return 0;
}