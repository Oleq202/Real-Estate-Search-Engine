#ifndef CRITERIA_H
#define CRITERIA_H

#include "Entities.h"
#include <vector>
#include <string>
#include <climits>
#include <algorithm>

using namespace std;

class Min_max_criteria {
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
        if (value >= min_value && value <= max_value) return priority;
        int distance = (value < min_value) ? (min_value - value) : (value - max_value);
        int penalty_steps = distance / step;
        int score = priority - (penalty_steps * change_iter);
        return max(score, 0);
    }
};

class Bool_criteria {
public:
    bool preferred = false;
    int priority = 5;
    Bool_criteria() = default;
    Bool_criteria(bool preferred, int priority) : preferred(preferred), priority(priority) {}
    int score(bool actual) const { return (actual == preferred) ? priority : 0; }
};

class Vector_criteria {
public:
    vector<string> list;
    int priority = 5;
    Vector_criteria() = default;
    Vector_criteria(const vector<string> &list, int priority) : list(list), priority(priority) {}
    int score(const string &value) const {
        for (auto &v : list) if (value == v) return priority;
        return 0;
    }
};

class Search_criteria {
public:
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

    template <typename Criteria, typename Value>
    inline void evaluate_criterion(const Criteria &criterion, const Value &value, int &score, int &max_score) const {
        max_score += criterion.priority;
        score += criterion.score(value);
    }

    int calculate_satisfiability(Home *home) {
        if (!home) return 0;

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

        if (apt) {
            evaluate_criterion(prefer_apartment, true, score, max_score);
            evaluate_criterion(floor, apt->floor, score, max_score);
            evaluate_criterion(want_elevator, apt->has_elevator, score, max_score);
        }

        if (house) {
            evaluate_criterion(garden_area, house->garden_area, score, max_score);
            evaluate_criterion(num_of_floors, house->num_of_floors, score, max_score);
            evaluate_criterion(prefer_house, true, score, max_score);
        }

        if (home->neighbourhood) {
            evaluate_criterion(preferred_neighbourhoods, home->neighbourhood->name, score, max_score);
            evaluate_criterion(prefer_school, home->neighbourhood->has_school, score, max_score);
            evaluate_criterion(prefer_park, home->neighbourhood->has_park, score, max_score);
            evaluate_criterion(prefer_shop, home->neighbourhood->has_shop, score, max_score);
            evaluate_criterion(prefer_pharmacy, home->neighbourhood->has_pharmacy, score, max_score);
            if (home->neighbourhood->city) {
                evaluate_criterion(preferred_cities, home->neighbourhood->city->name, score, max_score);
            }
        }
        return (max_score == 0) ? 0 : (score * 100) / max_score;
    }
};

#endif