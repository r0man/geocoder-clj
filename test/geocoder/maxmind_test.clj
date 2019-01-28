(ns geocoder.maxmind-test
  (:require [clojure.test :refer :all]
            [geocoder.maxmind :as geocoder]))

(def ip-address "92.229.192.11")

(def db (geocoder/db geocoder/default-path))

(deftest test-geocode-ip-address
  (is (nil? (geocoder/geocode-ip-address db "127.0.0.1")))
  (let [result (geocoder/geocode-ip-address db ip-address)]
    (is (= nil (:city result)))
    (is (= "Germany" (:name (:country result))))
    (is (= "de" (:iso-3166-1-alpha-2 (:country result))))
    (is (number? (:lat (:location result))))
    (is (number? (:lng (:location result))))))

(deftest test-wrap-maxmind
  (let [response ((geocoder/wrap-maxmind identity geocoder/default-path) {:remote-addr ip-address})]
    (is (= (geocoder/geocode-ip-address db ip-address) (:maxmind response)))))
