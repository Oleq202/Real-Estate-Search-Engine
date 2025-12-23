#ifndef ENTITIES_H
#define ENTITIES_H

#include <iostream>
#include <vector>
#include <string>
#include <memory>
using namespace std;

class Home;
class Apartment;
class House;
class Neighbourhood;
class City;
class Image;

class Image 
{
private:
    string image_path;
    string description;
public:
    Image() = default;
    void set_path(const string &p);
    string get_path() const;
    void set_description(const string &d);
    string get_description() const;    
};

enum class Home_type 
{
    Apartment,
    House
};

class Home 
{
public:
    Home_type type;
    int id;
    string address;
    int price;
    int area;
    int num_of_rooms;
    bool has_kitchen_annex;
    int construction_year;
    int transport_dist;
    int parking_spots;
    bool for_sale;
    bool is_primary_market;
    string web_path;

    Neighbourhood *neighbourhood;
    unique_ptr<Image> image;

    Home() = default;
    Home(Home_type type, int id, string address, int price, int area, int num_of_rooms, bool has_kitchen_annex, int construction_year, int transport_dist,
         int parking_spots, bool for_sale, bool is_primary_market, string web_path,
         Neighbourhood *neighbourhood = nullptr, unique_ptr<Image> image = nullptr);
    
    int calculate_price_per_sqm() const;
    virtual ~Home() = default;

    friend ostream & operator<<(ostream &os, const Home &h);
};

class Apartment : public Home 
{
public:
    int floor;
    bool has_elevator;

    Apartment() = default;
    Apartment(int id, string address, int price, int area, int num_of_rooms, 
              bool has_kitchen_annex, int construction_year, int transport_dist,
              int parking_spots, bool for_sale, bool is_primary_market, string web_path, 
              int floor, bool has_elevator,
              Neighbourhood *neighbourhood = nullptr, unique_ptr<Image> image = nullptr);
};

class House : public Home 
{
public:
    int garden_area;
    int num_of_floors;

    House() = default;
    House(int id, string address, int price, int area, int num_of_rooms, 
          bool has_kitchen_annex, int construction_year, int transport_dist,
          int parking_spots, bool for_sale, bool is_primary_market, string web_path, 
          int garden_area, int num_of_floors,
          Neighbourhood *neighbourhood = nullptr, unique_ptr<Image> image = nullptr);
};

class Neighbourhood 
{
public:
    string name;
    City *city;
    bool has_school;
    bool has_park;
    bool has_shop;
    bool has_pharmacy;
    vector<unique_ptr<Home>> home_list;

    Neighbourhood() = default;
    Neighbourhood(string name, City *city, bool has_school, bool has_park, bool has_shop, bool has_pharmacy);

    int avg_price();
    int avg_price_per_sqm();
};

class City 
{
public:
    string name;
    vector<unique_ptr<Neighbourhood>> neighbourhood_list;
    int population = 0;
    bool is_capital = false;

    City() = default;
    City(string nm, int pop, bool capital);

    int avg_price();
    int avg_price_per_sqm();
};

ostream& operator<<(ostream& os, const Home& h);

#endif