(ns geocoder.bing-test
  (:require [clojure.test :refer :all]
            [geo.core :refer [point point-x point-y]]
            [geocoder.utils :refer [approx=]]
            [geocoder.bing :as bing :refer :all]))

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
    (is (approx= 52.54254 (point-y location)))
    (is (approx= 13.423033 (point-x location)))))

(deftest test-street-name
  (is (= "Senefelderstraße 24" (street-name response))))

(deftest test-postal-code
  (is (= "10437" (postal-code response))))

(deftest test-region
  (is (nil? (region response))))

(deftest test-geocode-address
  (let [geocoder (bing/geocoder {:api-key test-key})]
    (is (empty? (geocode-address geocoder "xxxxxxxxxxxxxxxxxxxxx" :api-key test-key)))
    (let [address (first (geocode-address geocoder "Senefelderstraße 24, Berlin" :api-key test-key))]
      (is (= "Senefelderstraße 24" (street-name address)))
      (is (= "10437" (postal-code address)))
      (is (= "Berlin" (city address)))
      (is (nil? (region address)))
      (let [country (country address)]
        (is (nil? (:iso-3166-1-alpha-2 country)))
        (is (= "Germany" (:name country))))
      (let [location (location address)]
        (is (approx= 13.42306 (point-x location)))
        (is (approx= 52.54255 (point-y location)))))))

(deftest test-geocode-location
  (let [geocoder (bing/geocoder {:api-key test-key})]
    (is (empty? (geocode-location geocoder (point 4326 0 0) :api-key test-key)))
    (let [address (first (geocode-location geocoder (point 4326 13.423033 52.54254) :api-key test-key))]
      (is (= "SenefelderStraße 24" (street-name address)))
      (is (= "10437" (postal-code address)))
      (is (= "Berlin" (city address)))
      (is (nil? (region address)))
      (let [country (country address)]
        (is (nil? (:iso-3166-1-alpha-2 country)))
        (is (= "Germany" (:name country))))
      (let [location (location address)]
        (is (approx= 13.423033 (point-x location)))
        (is (approx= 52.54254 (point-y location)))))
    (is (= (geocode-location geocoder (point 4326 13.42299 52.54258) :api-key test-key)
           (geocode-location geocoder "52.54258,13.42299" :api-key test-key)
           (geocode-location geocoder {:latitude 52.54258 :longitude 13.42299} :api-key test-key)
           (geocode-location geocoder {:lat 52.54258 :lng 13.42299} :api-key test-key)))))
