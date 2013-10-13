(ns geocoder.maxmind-test
  (:require [clojure.test :refer :all]
            [geo.core :refer [point-x point-y]]
            [geocoder.maxmind :refer :all]))

(def ip-address "92.229.192.11")

(def db (make-db default-path))

(deftest test-geocode-ip-address
  (is (nil? (geocode-ip-address db "127.0.0.1")))
  (let [result (geocode-ip-address db ip-address)]
    (is (= "Germany" (:name (:country result))))
    (is (= "de" (:iso-3166-1-alpha-2 (:country result))))
    (is (number? (point-y (:location result))))
    (is (number? (point-x (:location result))))))

(deftest test-wrap-maxmind
  (let [response ((wrap-maxmind identity default-path) {:remote-addr ip-address})]
    (is (= (geocode-ip-address db ip-address) (:maxmind response)))))
