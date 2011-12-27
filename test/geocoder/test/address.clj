(ns geocoder.test.address
  (:use clojure.test
        geocoder.address
        geocoder.location))

(def address
  (make-address
   :city "Berlin"
   :country {:name "Germany" :iso-3166-1-alpha-2 "de"}
   :location (make-location 52.54254 13.423033)
   :street-name "Senefelderstraße"
   :street-number "24"
   :postal-code "10437"
   :region "Berlin"))

(deftest test-make-address
  (let [country (:country address)]
    (is (= "Germany" (:name country)))
    (is (= "de" (:iso-3166-1-alpha-2 country))))
  (is (= "Berlin" (:city address)))
  (is (= "Senefelderstraße" (:street-name address)))
  (is (= "24" (:street-number address)))
  (is (= "10437" (:postal-code address)))
  (is (= "Berlin" (:region address))))
