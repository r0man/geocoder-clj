(ns geocoder.google-test
  (:require [clojure.test :refer :all]
            [geocoder.google :as google :refer :all]
            [geocoder.util :as util]
            [geocoder.utils :refer [approx=]]))

(def api-key
  "MY-API-KEY")

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

(def geocode-address-response-ok
  {:results
   [{:address-components
     [{:long-name "24", :short-name "24", :types ["street_number"]}
      {:long-name "Senefelderstraße",
       :short-name "Senefelderstraße",
       :types ["route"]}
      {:long-name "Bezirk Pankow",
       :short-name "Bezirk Pankow",
       :types ["political" "sublocality" "sublocality_level_1"]}
      {:long-name "Berlin",
       :short-name "Berlin",
       :types ["locality" "political"]}
      {:long-name "Berlin",
       :short-name "Berlin",
       :types ["administrative_area_level_1" "political"]}
      {:long-name "Germany",
       :short-name "DE",
       :types ["country" "political"]}
      {:long-name "10437", :short-name "10437", :types ["postal_code"]}],
     :formatted-address "Senefelderstraße 24, 10437 Berlin, Germany",
     :geometry
     {:location {:lat 52.54258, :lng 13.42299},
      :location-type "ROOFTOP",
      :viewport
      {:northeast {:lat 52.5439289802915, :lng 13.4243389802915},
       :southwest {:lat 52.5412310197085, :lng 13.4216410197085}}},
     :partial-match true,
     :place-id "ChIJf9QNQf5NqEcRVo31O1xb20c",
     :plus-code
     {:compound-code "GCVF+25 Berlin, Germany",
      :global-code "9F4MGCVF+25"},
     :types ["street_address"]}],
   :status "OK"})

(def geocoder-result-zero
  {:results [] :status "ZERO_RESULTS"})

(deftest test-city
  (is (= "Berlin" (city address))))

(deftest test-country
  (let [country (country address)]
    (is (= "de" (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location address)]
    (is (approx= 52.54258 (:lat location)))
    (is (approx= 13.42299 (:lng location)))))

(deftest test-street-name
  (is (= "Senefelderstraße" (street-name address))))

(deftest test-street-number
  (is (= "24" (street-number address))))

(deftest test-postal-code
  (is (= "10437" (postal-code address))))

(deftest test-region
  (is (= "Berlin" (region address))))

(deftest test-geocode-address
  (let [geocoder (google/geocoder {:api-key api-key})]
    (with-redefs [util/fetch-json (constantly geocoder-result-zero)]
      (is (empty? (geocode-address geocoder "xxxxxxxxxxxxxxxxxxxxx"))))
    (with-redefs [util/fetch-json (constantly geocode-address-response-ok)]
      (let [[address] (geocode-address geocoder "Senefelderstraße 24, 10437 Berlin")]
        (is (= "Senefelderstraße" (street-name address)))
        (is (= "24" (street-number address)))
        (is (= "10437" (postal-code address)))
        (is (= "Berlin" (city address)))
        (is (= "Berlin" (region address)))
        (let [country (country address)]
          (is (= "de" (:iso-3166-1-alpha-2 country)))
          (is (= "Germany" (:name country))))
        (let [location (location address)]
          (is (= 52.54258 (:lat location)))
          (is (= 13.42299 (:lng location))))))))

(deftest test-geocode-location
  (let [geocoder (google/geocoder {:api-key api-key})]
    (with-redefs [util/fetch-json (constantly geocoder-result-zero)]
      (is (empty? (geocode-location geocoder {:lat 0 :lng 0}))))
    (with-redefs [util/fetch-json (constantly geocode-address-response-ok)]
      (let [[address] (geocode-location geocoder {:lat 52.54258 :lng 13.42299})]
        (is (= "Senefelderstraße" (street-name address)))
        (is (= "24" (street-number address)))
        (is (= "10437" (postal-code address)))
        (is (= "Berlin" (city address)))
        (is (= "Berlin" (region address)))
        (let [country (country address)]
          (is (= "de" (:iso-3166-1-alpha-2 country)))
          (is (= "Germany" (:name country))))
        (let [location (location address)]
          (is (approx= 52.54258 (:lat location)))
          (is (approx= 13.42299 (:lng location))))))))
