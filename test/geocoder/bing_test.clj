(ns geocoder.bing-test
  (:require [clojure.test :refer :all]
            [geo.core :refer [point point-x point-y]]
            [geocoder.bing :refer :all]))

(def test-key "AhiRsght2jQhqZaHpUAvXhCjvNfyWxb0Xb4EwAW81LUgvBa68FcnL9mOFDLKoQGl")

(def response
  {:name "Senefelderstraße 24, 10437 Berlin",
   :point {:type "Point", :coordinates [52.54254 13.423033]},
   :address
   {:address-line "Senefelderstraße 24",
    :admin-district "BE",
    :country-region "Germany",
    :formatted-address "Senefelderstraße 24, 10437 Berlin",
    :locality "Berlin",
    :postal-code "10437"},
   :confidence "High",
   :entity-type "Address"})

(deftest test-city
  (is (= "Berlin" (city response))))

(deftest test-country
  (let [country (country response)]
    (is (nil? (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location response)]
    (is (= 52.54254 (point-y location)))
    (is (= 13.423033 (point-x location)))))

(deftest test-street-name
  (is (= "Senefelderstraße 24" (street-name response))))

(deftest test-postal-code
  (is (= "10437" (postal-code response))))

(deftest test-region
  (is (nil? (region response))))

(deftest test-geocode-address
  (is (empty? (geocode-address "xxxxxxxxxxxxxxxxxxxxx" :api-key test-key)))
  (let [address (first (geocode-address "Senefelderstraße 24, Berlin" :api-key test-key))]
    (is (= "Senefelderstraße 24" (street-name address)))
    (is (= "10437" (postal-code address)))
    (is (= "Berlin" (city address)))
    (is (nil? (region address)))
    (let [country (country address)]
      (is (nil? (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (location address)]
      (is (= 52.54254 (point-y location)))
      (is (= 13.423033 (point-x location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location (point 4326 0 0) :api-key test-key)))
  (let [address (first (geocode-location (point 4326 13.423033 52.54254) :api-key test-key))]
    (is (= "24 Senefelderstraße" (street-name address)))
    (is (= "10437" (postal-code address)))
    (is (= "Berlin" (city address)))
    (is (nil? (region address)))
    (let [country (country address)]
      (is (nil? (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (location address)]
      (is (= 52.5424562394619 (point-y location)))
      (is (= 13.423220291733742 (point-x location)))))
  (is (= (geocode-location (point 4326 13.42299 52.54258) :api-key test-key)
         (geocode-location "52.54258,13.42299" :api-key test-key)
         (geocode-location {:latitude 52.54258 :longitude 13.42299} :api-key test-key)
         (geocode-location {:lat 52.54258 :lng 13.42299} :api-key test-key))))
