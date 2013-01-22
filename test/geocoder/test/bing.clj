(ns geocoder.test.bing
  (:use clojure.test
        geocoder.bing
        geocoder.location
        geocoder.provider))

(def geocoder (make-geocoder {:key "AhiRsght2jQhqZaHpUAvXhCjvNfyWxb0Xb4EwAW81LUgvBa68FcnL9mOFDLKoQGl"}))

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
  (is (= "Berlin" (city geocoder response))))

(deftest test-country
  (let [country (country geocoder response)]
    (is (nil? (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location geocoder response)]
    (is (= 52.54254 (:latitude location)))
    (is (= 13.423033 (:longitude location)))))

(deftest test-street-name
  (is (= "Senefelderstraße 24" (street-name geocoder response))))

(deftest test-street-number
  (is (nil? (street-number geocoder response))))

(deftest test-postal-code
  (is (= "10437" (postal-code geocoder response))))

(deftest test-region
  (is (nil? (region geocoder response))))

(deftest test-make-geocoder
  (is (nil? (make-geocoder nil)))
  (is (instance? geocoder.bing.Geocoder (make-geocoder {:key "secret"}))))

(deftest test-geocode-address
  (is (empty? (geocode-address geocoder "xxxxxxxxxxxxxxxxxxxxx" nil)))
  (let [address (first (geocode-address geocoder "Senefelderstraße 24, 10437 Berlin" nil))]
    (is (= "Bing" (:provider address)))
    (is (= "Senefelderstraße 24" (:street-name address)))
    (is (nil? (:street-number address)))
    (is (= "10437" (:postal-code address)))
    (is (= "Prenzlauer Berg" (:city address)))
    (is (nil? (:region address)))
    (let [country (:country address)]
      (is (nil? (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (:location address)]
      (is (= 52.54254 (:latitude location)))
      (is (= 13.423033 (:longitude location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location geocoder (make-location 0 0) nil)))
  (let [address (first (geocode-location geocoder (make-location 52.54254 13.423033) nil))]
    (is (= "Bing" (:provider address)))
    (is (= "24 Senefelderstraße" (:street-name address)))
    (is (nil? (:street-number address)))
    (is (= "10437" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (nil? (:region address)))
    (let [country (:country address)]
      (is (nil? (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (:location address)]
      (is (= 52.5424562394619 (:latitude location)))
      (is (= 13.423220291733742 (:longitude location))))))

(deftest test-supports-address-geocoding?
  (is (supports-address-geocoding? geocoder)))

(deftest test-supports-ip-address-geocoding?
  (is (not (supports-ip-address-geocoding? geocoder))))

(deftest test-supports-location-geocoding?
  (is (supports-location-geocoding? geocoder)))
