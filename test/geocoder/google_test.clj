(ns geocoder.google-test
  (:require [clojure.test :refer :all]
            [geo.core :refer [point point-x point-y]]
            [geocoder.google :refer :all]))

(def address
  {:types ["street_address"],
   :geometry
   {:location {:lat 52.54258, :lng 13.42299},
    :location-type "ROOFTOP",
    :viewport
    {:northeast {:lat 52.5439289802915, :lng 13.4243389802915},
     :southwest {:lat 52.5412310197085, :lng 13.4216410197085}}},
   :formatted-address "Senefelderstraße 24, 10437 Berlin, Germany",
   :address-components
   [{:long-name "24", :short-name "24", :types ["street_number"]}
    {:long-name "Senefelderstraße",
     :short-name "Senefelderstraße",
     :types ["route"]}
    {:long-name "Berlin",
     :short-name "Berlin",
     :types ["sublocality" "political"]}
    {:long-name "Berlin",
     :short-name "Berlin",
     :types ["locality" "political"]}
    {:long-name "Berlin",
     :short-name "Berlin",
     :types ["administrative_area_level_2" "political"]}
    {:long-name "Berlin",
     :short-name "Berlin",
     :types ["administrative_area_level_1" "political"]}
    {:long-name "Germany",
     :short-name "DE",
     :types ["country" "political"]}
    {:long-name "10437", :short-name "10437", :types ["postal_code"]}]})

(deftest test-city
  (is (= "Berlin" (city address))))

(deftest test-country
  (let [country (country address)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location address)]
    (is (= 52.54258 (point-y location)))
    (is (= 13.42299 (point-x location)))))

(deftest test-street-name
  (is (= "Senefelderstraße" (street-name address))))

(deftest test-street-number
  (is (= "24" (street-number address))))

(deftest test-postal-code
  (is (= "10437" (postal-code address))))

(deftest test-region
  (is (= "Berlin" (region address))))

(deftest test-geocode-address
  (is (empty? (geocode-address "xxxxxxxxxxxxxxxxxxxxx")))
  (let [address (first (geocode-address "Senefelderstraße 24, 10437 Berlin"))]
    (is (= "Senefelderstraße" (street-name address)))
    (is (= "24" (street-number address)))
    (is (= "10437" (postal-code address)))
    (is (= "Berlin" (city address)))
    (is (= "Berlin" (region address)))
    (let [country (country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (location address)]
      (is (= 52.54258 (point-y location)))
      (is (= 13.42299 (point-x location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location (point 4326 0 0))))
  (let [address (first (geocode-location (point 4326 13.42299 52.54258)))]
    (is (= "Senefelderstraße" (street-name address)))
    (is (= "24" (street-number address)))
    (is (= "10437" (postal-code address)))
    (is (= "Berlin" (city address)))
    (is (= "Berlin" (region address)))
    (let [country (country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (location address)]
      (is (= 52.54258 (point-y location)))
      (is (= 13.42299 (point-x location)))))
  (is (= (geocode-location (point 4326 13.42299 52.54258))
         (geocode-location "52.54258,13.42299")
         (geocode-location {:latitude 52.54258 :longitude 13.42299})
         (geocode-location {:lat 52.54258 :lng 13.42299}))))
