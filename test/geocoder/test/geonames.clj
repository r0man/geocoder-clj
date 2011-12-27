(ns geocoder.test.geonames
  (:use clojure.test
        geocoder.geonames
        geocoder.location
        geocoder.provider))

(def geocoder (make-geocoder {:key "demo"}))

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
  (is (= "Berlin" (city geocoder response))))

(deftest test-country
  (let [country (country geocoder response)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))))

(deftest test-location
  (let [location (location geocoder response)]
    (is (= 52.5161166666667 (:latitude location)))
    (is (= 13.38735 (:longitude location)))))

(deftest test-street-name
  (is (nil? (street-name geocoder response))))

(deftest test-street-number
  (is (nil? (street-number geocoder response))))

(deftest test-postal-code
  (is (= "10117" (postal-code geocoder response))))

(deftest test-region
  (is (= "Berlin" (region geocoder response))))

(deftest test-make-geocoder
  (is (nil? (make-geocoder nil)))
  (is (instance? geocoder.geonames.Geocoder (make-geocoder {:key "secret"}))))

(deftest test-geocode-address
  (is (empty? (geocode-address geocoder "xxxxxxxxxxxxxxxxxxxxx" nil)))
  (let [address (first (geocode-address geocoder "Berlin" nil))]
    (is (= "Geonames" (:provider address)))
    (is (nil? (:street-name address)))
    (is (nil? (:street-number address)))
    (is (= "10117" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (= "Berlin" (:region address)))
    (let [country (:country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (nil? (:name country))))
    (let [location (:location address)]
      (is (= 52.5161166666667 (:latitude location)))
      (is (= 13.38735 (:longitude location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location geocoder (make-location 0 0) nil)))
  (let [address (first (geocode-location geocoder (make-location 52.5161166666667 13.38735) nil))]
    (is (= "Geonames" (:provider address)))
    (is (nil? (:street-name address)))
    (is (nil? (:street-number address)))
    (is (= "10117" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (= "Berlin" (:region address)))
    (let [country (:country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (nil? (:name country))))
    (let [location (:location address)]
      (is (= 52.5161166666667 (:latitude location)))
      (is (= 13.38735 (:longitude location))))))

(deftest test-supports-address-geocoding?
  (is (supports-address-geocoding? geocoder)))

(deftest test-supports-ip-address-geocoding?
  (is (not (supports-ip-address-geocoding? geocoder))))

(deftest test-supports-location-geocoding?
  (is (supports-location-geocoding? geocoder)))
