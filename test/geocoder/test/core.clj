(ns geocoder.test.core
  (:require [clojure.test :refer :all]
            [geo.core :refer [point]]
            [geocoder.core :refer :all]
            [geocoder.maxmind :as maxmind]
            [geocoder.provider :as provider]))

(def ^:dynamic *geocoder* (first *providers*))

(deftest test-geocode-address
  (is (nil? (geocode-address nil)))
  (let [address "Berlin, Germany"]
    (is (= (geocode-address address)
           (provider/geocode-address *geocoder* address {})))))

(deftest test-geocode-ip-address
  (is (nil? (geocode-ip-address nil)))
  (let [ip-address "92.229.192.11"]
    (is (= (geocode-ip-address ip-address)
           (provider/geocode-ip-address maxmind/*geocoder* ip-address {})))))

(deftest test-geocode-location
  (is (nil? (geocode-location nil)))
  (let [location (point 4326 13.4060912 52.519171)]
    (is (= (geocode-location location)
           (provider/geocode-location *geocoder* location {})))))
