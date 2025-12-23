#ifndef FILEIO_H
#define FILEIO_H

#include "Entities.h"
#include "Criteria.h"
#include <vector>
#include <fstream>
#include <sstream>

class All_home_list {
public:
    vector<Home *> homes;
    All_home_list() = default;
    vector<pair<Home *, int>> sort_results(Search_criteria &criteria);
};

class Home_initializer {
private:
    bool parse_bool(const string &str);
    Neighbourhood *find_neighbourhood(City &city, const string &name);
    bool file_exists(const string &name);
public:
    void read_csv(const string &filename, City &city, All_home_list &all_home_list);
};

class Neighbourhood_initializer {
private:
    bool parse_bool(const string &str);
public:
    void read_csv(const string &filename, City &city);
};

#endif