(ns geocoder.test.yahoo
  (:use clojure.test
        clojure.contrib.mock
        geocoder.address
        geocoder.location
        geocoder.provider
        geocoder.yahoo))

(def response
  {:result-set
   {:version "1.0",
    :error 0,
    :error-message "No error",
    :locale "us_US",
    :quality 87,
    :found 1,
    :results
    [{:quality 85,
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
      :statecode "BE"}]}})

(def result (merge (geocoder.yahoo.Result.) (first (:results (:result-set response)))))

(deftest test-city
  (is (= "Berlin" (city result))))

(deftest test-country
  (let [country (country result)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location result)]
    (is (= 52.542526 (:latitude location)))
    (is (= 13.423240 (:longitude location)))))

(deftest test-street-name
  (is (= "Senefelderstrasse" (street-name result))))

(deftest test-street-number
  (is (= "24" (street-number result))))

(deftest test-postal-code
  (is (= "10437" (postal-code result))))

(deftest test-region
  (is (= "Berlin" (region result))))

(deftest test-geocode-request
  (let [request (geocode-request (geocoder.yahoo.Provider. "key") "Senefelderstraße 24, 10437 Berlin" {})]
    (is (= :get (:method request)))
    (is (= "http://where.yahooapis.com/geocode" (:url request)))
    (let [query (:query-params request)]
      (is (= "key" (:appid query)))
      (is (= "J" (:flags query)))
      (is (= "Senefelderstraße 24, 10437 Berlin" (:q query))))))

(deftest test-reverse-geocode-request
  (let [request (reverse-geocode-request (geocoder.yahoo.Provider. "key") (make-location 52.54254 13.423033) {})]
    (is (= :get (:method request)))
    (is (= "http://where.yahooapis.com/geocode" (:url request)))
    (let [query (:query-params request)]
      (is (= "key" (:appid query)))
      (is (= "J" (:flags query)))
      (is (= "R" (:gflags query)))
      (is (= "52.54254,13.423033" (:location query))))))
