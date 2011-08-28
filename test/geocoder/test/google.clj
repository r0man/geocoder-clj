(ns geocoder.test.google
  (:use clojure.test
        clojure.contrib.mock
        geocoder.address
        geocoder.google
        geocoder.location
        geocoder.provider))

(def response
  {:status "OK",
   :results
   [{:types ["street_address"],
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
      {:long-name "10437", :short-name "10437", :types ["postal_code"]}]}]})

(def result (merge (geocoder.google.Result.) (first (:results response))))

(deftest test-city
  (is (= "Berlin" (city result))))

(deftest test-country
  (let [country (country result)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location result)]
    (is (= 52.54258 (:latitude location)))
    (is (= 13.42299 (:longitude location)))))

(deftest test-street-name
  (is (= "Senefelderstraße" (street-name result))))

(deftest test-street-number
  (is (= "24" (street-number result))))

(deftest test-postal-code
  (is (= "10437" (postal-code result))))

(deftest test-region
  (is (= "Berlin" (region result))))

(deftest test-geocode-request
  (let [request (geocode-request (geocoder.google.Provider.) "Senefelderstraße 24, 10437 Berlin" {})]
    (is (= :get (:method request)))
    (is (= "http://maps.google.com/maps/api/geocode/json" (:url request)))
    (let [query (:query-params request)]
      (is (= "en" (:language query)))
      (is (= false (:sensor query)))
      (is (= "Senefelderstraße 24, 10437 Berlin" (:address query))))))

(deftest test-reverse-geocode-request
  (let [request (reverse-geocode-request (geocoder.google.Provider.) (make-location 52.54254 13.423033) {})]
    (is (= :get (:method request)))
    (is (= "http://maps.google.com/maps/api/geocode/json" (:url request)))
    (let [query (:query-params request)]
      (is (= "en" (:language query)))
      (is (= false (:sensor query)))
      (is (= "52.54254,13.423033" (:latlng query))))))

