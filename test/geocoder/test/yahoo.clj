(ns geocoder.test.yahoo
  (:use clojure.test
        geocoder.location
        geocoder.provider
        geocoder.yahoo))

(def geocoder (make-geocoder {:key "AhiRsght2jQhqZaHpUAvXhCjvNfyWxb0Xb4EwAW81LUgvBa68FcnL9mOFDLKoQGl"}))

(def response
  {:quality 85,
   :country "Germany",
   :offsetlat "52.542553",
   :line4 "Germany",
   :longitude "13.423240",
   :state "Berlin",
   :xstreet "",
   :woetype 11,
   :radius 500,
   :uzip "10437",
   :name "",
   :countycode "BE",
   :hash "A4BD9C8F39BAE14F",
   :neighborhood "Prenzlauer Berg",
   :line1 "Senefelderstrasse 24",
   :line2 "10437 Berlin",
   :unittype "",
   :line3 "",
   :offsetlon "13.423034",
   :latitude "52.542526",
   :city "Berlin",
   :street "Senefelderstrasse",
   :countrycode "DE",
   :woeid 20065581,
   :unit "",
   :postal "10437",
   :house "24",
   :county "Berlin",
   :statecode "BE"})

(deftest test-city
  (is (= "Berlin" (city geocoder response))))

(deftest test-country
  (let [country (country geocoder response)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location geocoder response)]
    (is (= 52.542526 (:latitude location)))
    (is (= 13.423240 (:longitude location)))))

(deftest test-street-name
  (is (= "Senefelderstrasse" (street-name geocoder response))))

(deftest test-street-number
  (is (= "24" (street-number geocoder response))))

(deftest test-postal-code
  (is (= "10437" (postal-code geocoder response))))

(deftest test-region
  (is (= "Berlin" (region geocoder response))))

(deftest test-make-geocoder
  (is (nil? (make-geocoder nil)))
  (is (instance? geocoder.yahoo.Geocoder (make-geocoder {:key "secret"}))))

(deftest test-geocode-address
  (is (empty? (geocode-address geocoder "xxxxxxxxxxxxxxxxxxxxx" nil)))
  (let [address (first (geocode-address geocoder "Senefelderstraße 24, 10437 Berlin" nil))]
    (is (= "Yahoo" (:provider address)))
    (is (= "Senefelderstraße" (:street-name address)))
    (is (= "24" (:street-number address)))
    (is (= "10437" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (= "Berlin" (:region address)))
    (let [country (:country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (:location address)]
      (is (= 52.542519 (:latitude location)))
      (is (= 13.42325 (:longitude location))))))

(deftest test-geocode-location
  (is (empty? (geocode-location geocoder (make-location 0 0) nil)))
  (let [address (first (geocode-location geocoder (make-location 52.542526 13.42324) nil))]
    (is (= "Yahoo" (:provider address)))
    (is (= "Senefelderstraße" (:street-name address)))
    (is (= "24" (:street-number address)))
    (is (= "10437" (:postal-code address)))
    (is (= "Berlin" (:city address)))
    (is (= "Berlin" (:region address)))
    (let [country (:country address)]
      (is (= "de" (:iso-3166-1-alpha-2 country)))
      (is (= "Germany" (:name country))))
    (let [location (:location address)]
      (is (= 52.542517 (:latitude location)))
      (is (= 13.423235 (:longitude location))))))

(deftest test-supports-address-geocoding?
  (is (supports-address-geocoding? geocoder)))

(deftest test-supports-ip-address-geocoding?
  (is (not (supports-ip-address-geocoding? geocoder))))

(deftest test-supports-location-geocoding?
  (is (supports-location-geocoding? geocoder)))
