(ns geocoder.test.core
  (:require [geocoder.maxmind :as maxmind]
            [geocoder.provider :as provider])
  (:use clojure.test
        geocoder.core))

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
  (let [location {:latitude 52.519171 :longitude 13.4060912}]
    (is (= (geocode-location location)
           (provider/geocode-location *geocoder* location {})))))
