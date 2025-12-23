#include "Entities.h"

void Image::set_path(const string &p) { image_path = p; }
string Image::get_path() const { return image_path; }
void Image::set_description(const string &d) { description = d; }
string Image::get_description() const { return description; }

Home::Home(Home_type type, int id, string address, int price, int area, int num_of_rooms, 
           bool has_kitchen_annex, int construction_year, int transport_dist,
           int parking_spots, bool for_sale, bool is_primary_market, string web_path,
           Neighbourhood *neighbourhood, unique_ptr<Image> image)
    : type(type), id(id), address(address), price(price), area(area), num_of_rooms(num_of_rooms), 
      has_kitchen_annex(has_kitchen_annex), construction_year(construction_year), 
      transport_dist(transport_dist), parking_spots(parking_spots),
      for_sale(for_sale), is_primary_market(is_primary_market), web_path(web_path),
      neighbourhood(neighbourhood), image(move(image)) {}

int Home::calculate_price_per_sqm() const {
    if (area == 0) return 0;
    return price / area;
}

Apartment::Apartment(int id, string address, int price, int area, int num_of_rooms, 
                     bool has_kitchen_annex, int construction_year, int transport_dist,
                     int parking_spots, bool for_sale, bool is_primary_market, string web_path, 
                     int floor, bool has_elevator,
                     Neighbourhood *neighbourhood, unique_ptr<Image> image)
    : Home(Home_type::Apartment, id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist,
           parking_spots, for_sale, is_primary_market, web_path,
           neighbourhood, move(image)),
      floor(floor), has_elevator(has_elevator) {}

House::House(int id, string address, int price, int area, int num_of_rooms, 
             bool has_kitchen_annex, int construction_year, int transport_dist,
             int parking_spots, bool for_sale, bool is_primary_market, string web_path, 
             int garden_area, int num_of_floors,
             Neighbourhood *neighbourhood, unique_ptr<Image> image)
    : Home(Home_type::House, id, address, price, area, num_of_rooms, has_kitchen_annex, construction_year, transport_dist,
           parking_spots, for_sale, is_primary_market, web_path,
           neighbourhood, move(image)),
      garden_area(garden_area), num_of_floors(num_of_floors) {}

Neighbourhood::Neighbourhood(string nm, City *cty, bool school, bool park, bool shop, bool pharmacy)
    : name(nm), city(cty), has_school(school), has_park(park), has_shop(shop), has_pharmacy(pharmacy) {}

int Neighbourhood::avg_price() {
    if (home_list.empty()) return 0;
    int total_price = 0;
    for (auto &home : home_list) total_price += home->price;
    return total_price / home_list.size();
}

int Neighbourhood::avg_price_per_sqm() {
    if (home_list.empty()) return 0;
    int total_price_per_sqm = 0;
    for (auto &home : home_list) total_price_per_sqm += home->calculate_price_per_sqm();
    return total_price_per_sqm / home_list.size();
}

City::City(string nm, int pop, bool capital) : name(nm), population(pop), is_capital(capital) {}

int City::avg_price() {
    int total_price = 0;
    int total_homes = 0;
    for (auto &neighbourhood : neighbourhood_list) {
        for (auto &home : neighbourhood->home_list) {
            total_price += home->price;
            total_homes++;
        }
    }
    return (total_homes == 0) ? 0 : total_price / total_homes;
};

int City::avg_price_per_sqm() {
    int total_price_per_sqm = 0;
    int total_homes = 0;
    for (auto &neighbourhood : neighbourhood_list) {
        for (auto &home : neighbourhood->home_list) {
            total_price_per_sqm += home->calculate_price_per_sqm();
            total_homes++;
        }
    }
    return (total_homes == 0) ? 0 : total_price_per_sqm / total_homes;
};

ostream& operator<<(ostream& os, const Home& h)
{
    const Apartment* apt = dynamic_cast<const Apartment*>(&h);
    const House* house = dynamic_cast<const House*>(&h);
    string raw_path = (h.image ? h.image->get_path() : "https://picsum.photos/id/1/200/140");
    string home_type = apt ? "Apartment" : (house ? "House" : "Unknown");

    os 
    //    << "\"type\": \"" << home_type << "\","
       << "\"name\": \"" << h.address << "\","
       << "\"price\": " << h.price << ","
       << "\"area_sqm\": " << h.area << ","
       << "\"rooms\": " << h.num_of_rooms << ","
       << "\"year_built\": " << h.construction_year << ","
       << "\"city\": \"" << (h.neighbourhood ? h.neighbourhood->city->name : "Unknown") << "\","
       << "\"province\": \"" << (h.neighbourhood ? h.neighbourhood->name : "Unknown") << "\","
       << "\"floor\": " << (apt ? apt->floor : 0) << ","
       << "\"parking\": " << h.parking_spots << ","
       << "\"garden\": " << (house ? house->garden_area : 0) << ","
       << "\"image\": \"" << raw_path << "\"";
    return os;
};