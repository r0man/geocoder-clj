(ns geocoder.test.google
  (:use clojure.test
        geocoder.google
        geocoder.location
        geocoder.provider))

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
  (is (= "Berlin" (city *geocoder* address))))

(deftest test-country
  (let [country (country *geocoder* address)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location *geocoder* address)]
    (is (= 52.54258 (:latitude location)))
    (is (= 13.42299 (:longitude location)))))

(deftest test-street-name
  (is (= "Senefelderstraße" (street-name *geocoder* address))))

(deftest test-street-number
  (is (= "24" (street-number *geocoder* address))))

(deftest test-postal-code
  (is (= "10437" (postal-code *geocoder* address))))

(deftest test-region
  (is (= "Berlin" (region *geocoder* address))))

(deftest test-make-geocoder
  (is (instance? geocoder.google.Geocoder (make-geocoder nil)))
  (is (instance? geocoder.google.Geocoder (make-geocoder {}))))

(deftest test-geocode-address
  (is (empty? (geocode-address *geocoder* "xxxxxxxxxxxxxxxxxxxxx" nil)))
  (let [address (first (geocode-address *geocoder* "Senefelderstraße 24, 10437 Berlin" nil))]
    (is (= "Senefelderstraße" (:street-name address)))
    (is (= "24" (:street-number address)))
    (is (= "10437" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (= "Berlin" (:region address)))
    (let [country (:country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (:location address)]
      (is (= 52.54258 (:latitude location)))
      (is (= 13.42299 (:longitude location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location *geocoder* (make-location 0 0) nil)))
  (let [address (first (geocode-location *geocoder* (make-location 52.54258 13.42299) nil))]
    (is (= "Google" (:provider address)))
    (is (= "Senefelderstraße" (:street-name address)))
    (is (= "24" (:street-number address)))
    (is (= "10437" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (= "Berlin" (:region address)))
    (let [country (:country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (:location address)]
      (is (= 52.54258 (:latitude location)))
      (is (= 13.42299 (:longitude location))))))

(deftest test-supports-address-geocoding?
  (is (supports-address-geocoding? *geocoder*)))

(deftest test-supports-ip-address-geocoding?
  (is (not (supports-ip-address-geocoding? *geocoder*))))

(deftest test-supports-location-geocoding?
  (is (supports-location-geocoding? *geocoder*)))
