(ns geocoder.geonames-test
  (:require [clojure.test :refer :all]
            [geocoder.utils :refer [approx=]]
            [geocoder.geonames :as geocoder]))

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
  (is (= "Berlin" (geocoder/city response))))

(deftest test-country
  (let [country (geocoder/country response)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))))

(deftest test-location
  (let [location (geocoder/location response)]
    (is (= 52.5161166666667 (:lat location)))
    (is (= 13.38735 (:lng location)))))

(deftest test-street-name
  (is (nil? (geocoder/street-name response))))

(deftest test-postal-code
  (is (= "10117" (geocoder/postal-code response))))

(deftest test-region
  (is (= "Berlin" (geocoder/region response))))

(deftest test-geocode-address
  (let [geocoder (geocoder/geocoder {:api-key test-key})]
    (is (empty? (geocoder/geocode-address geocoder "xxxxxxxxxxxxxxxxxxxxx")))
    (let [address (first (geocoder/geocode-address geocoder "Berlin"))]
      (is (nil? (geocoder/street-name address)))
      (is (= "10587" (geocoder/postal-code address)))
      (is (= "Berlin" (geocoder/city address)))
      (is (= "Berlin" (geocoder/region address)))
      (let [country (geocoder/country address)]
        (is (= "de" (:iso-3166-1-alpha-2 country)))
        (is (nil? (:name country))))
      (let [location (geocoder/location address)]
        (is (approx= 52.5161166666667 (:lat location)))
        (is (approx= 13.319519141740923 (:lng location)))))))

(deftest test-geocode-location
  (let [geocoder (geocoder/geocoder {:api-key test-key})]
    (is (empty? (geocoder/geocode-location geocoder {:lat 0 :lng 0})))
    (let [address (first (geocoder/geocode-location geocoder {:lng 13.38735 :lat 52.5161166666667}))]
      (is (nil? (geocoder/street-name address)))
      (is (= "10117" (geocoder/postal-code address)))
      (is (= "Berlin" (geocoder/city address)))
      (is (= "Berlin" (geocoder/region address)))
      (let [country (geocoder/country address)]
        (is (= "de" (:iso-3166-1-alpha-2 country)))
        (is (nil? (:name country))))
      (let [location (geocoder/location address)]
        (is (approx= 52.5161166666667 (:lat location)))
        (is (approx= 13.38735 (:lng location)))))))
