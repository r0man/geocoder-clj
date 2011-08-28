(ns geocoder.test.bing
  (:use clojure.test
        geocoder.address
        geocoder.bing
        geocoder.location
        geocoder.provider))

(def response
  {:authentication-result-code "ValidCredentials",
   :brand-logo-uri
   "http://dev.virtualearth.net/Branding/logo_powered_by.png",
   :copyright
   "Copyright © 2011 Microsoft and its suppliers. All rights reserved. This API cannot be accessed and the content and any results may not be used, reproduced or transmitted in any manner without express written permission from Microsoft Corporation.",
   :resource-sets
   [{:estimated-total 1,
     :resources
     [{:--type
       "Location:http://schemas.microsoft.com/search/local/ws/rest/v1",
       :bbox
       [52.538677282429326
        13.414565272038299
        52.54640271757068
        13.431500727961701],
       :name "Senefelderstraße 24, 10437 Berlin",
       :point {:type "Point", :coordinates [52.54254 13.423033]},
       :address
       {:address-line "Senefelderstraße 24",
        :admin-district "BE",
        :country-region "Germany",
        :formatted-address "Senefelderstraße 24, 10437 Berlin",
        :locality "Berlin",
        :postal-code "10437"},
       :confidence "High",
       :entity-type "Address"}]}],
   :status-code 200,
   :status-description "OK",
   :trace-id
   "1b311004704a41f5900a09efca50de61|AMSM001403|02.00.126.3000|AMSMSNVM001956, AMSMSNVM001865, AMSMSNVM001321, AMSMSNVM001852"})

(def result (merge (geocoder.bing.Result.) (first (mapcat :resources (:resource-sets response)))))

(deftest test-city
  (is (= "Berlin" (city result))))

(deftest test-country
  (let [country (country result)]
    (is (nil? (:iso-3166-1-alpha-2 country)))
    (is (= "Germany" (:name country)))))

(deftest test-location
  (let [location (location result)]
    (is (= 52.54254 (:latitude location)))
    (is (= 13.423033 (:longitude location)))))

(deftest test-street-name
  (is (= "Senefelderstraße 24" (street-name result))))

(deftest test-street-number
  (is (nil? (street-number result))))

(deftest test-postal-code
  (is (= "10437" (postal-code result))))

(deftest test-region
  (is (nil? (region result))))

(deftest test-geocode-request
  (let [request (geocode-request (geocoder.bing.Provider. "key") "Senefelderstraße 24, 10437 Berlin" {})]
    (is (= :get (:method request)))
    (is (= "http://dev.virtualearth.net/REST/v1/Locations" (:url request)))
    (let [query (:query-params request)]
      (is (= "key" (:key query)))
      (is (= "Senefelderstraße 24, 10437 Berlin" (:query query))))))

(deftest test-reverse-geocode-request
  (let [request (reverse-geocode-request (geocoder.bing.Provider. "key") (make-location 52.54254 13.423033) {})]
    (is (= :get (:method request)))
    (is (= "http://dev.virtualearth.net/REST/v1/Locations/point" (:url request)))
    (let [query (:query-params request)]
      (is (= "key" (:key query)))
      (is (= "52.54254,13.423033" (:point query))))))
