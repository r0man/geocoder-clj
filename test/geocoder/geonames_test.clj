(ns geocoder.geonames-test
  (:require [clojure.test :refer :all]
            [geo.core :refer [point point-x point-y]]
            [geocoder.utils :refer [approx=]]
            [geocoder.geonames :refer :all]))

(def test-key "geonamesclj")

(def response
  {:admin-code1 "BE"
   :admin-name1 "Berlin"
   :admin-name3 "Kreisfreie Stadt Berlin"
   :country-code "DE"
   :lat 52.5161166666667
   :lng 13.38735
   :place-name "Berlin"
   :postal-code "10117"})

(deftest test-city
  (is (= "Berlin" (city response))))

(deftest test-country
  (let [country (country response)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))))

(deftest test-location
  (let [location (location response)]
    (is (= 52.5161166666667 (point-y location)))
    (is (= 13.38735 (point-x location)))))

(deftest test-street-name
  (is (nil? (street-name response))))

(deftest test-postal-code
  (is (= "10117" (postal-code response))))

(deftest test-region
  (is (= "Berlin" (region response))))

(deftest test-geocode-address
  (is (empty? (geocode-address "xxxxxxxxxxxxxxxxxxxxx" :api-key test-key)))
  (let [address (first (geocode-address "Berlin" :api-key test-key))]
    (is (nil? (street-name address)))
    (is (= "10587" (postal-code address)))
    (is (= "Berlin" (city address)))
    (is (= "Berlin" (region address)))
    (let [country (country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (nil? (:name country))))
    (let [location (location address)]
      (is (approx= 52.5161166666667 (point-y location)))
      (is (approx= 13.319519141740923 (point-x location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location (point 4326 0 0) :api-key test-key)))
  (let [address (first (geocode-location (point 4326 13.38735 52.5161166666667) :api-key test-key))]
    (is (nil? (street-name address)))
    (is (= "10117" (postal-code address)))
    (is (= "Berlin" (city address)))
    (is (= "Berlin" (region address)))
    (let [country (country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (nil? (:name country))))
    (let [location (location address)]
      (is (approx= 52.5161166666667 (point-y location)))
      (is (approx= 13.38735 (point-x location)))))
  (is (= (geocode-location (point 4326 13.42299 52.54258) :api-key test-key)
         (geocode-location "52.54258,13.42299" :api-key test-key)
         (geocode-location {:latitude 52.54258 :longitude 13.42299} :api-key test-key)
         (geocode-location {:lat 52.54258 :lng 13.42299} :api-key test-key))))
