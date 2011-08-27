(ns geocode.test.bing
  (:use clojure.test
        geocode.bing))

(def response
  {:resource-sets
   [{:resources
     [{:name "Mundaka, Basque Country, Spain",
       :point
       {:type "Point",
        :coordinates [43.40943145751953 -2.7003700733184814]},
       :address
       {:admin-district "Basque Country",
        :admin-district2 "VZ",
        :country-region "Spain",
        :formatted-address "Mundaka, Basque Country, Spain",
        :locality "Mundaka"}}
      {:name "Mundaca, Basque Country, Spain",
       :point
       {:type "Point",
        :coordinates [43.40989999473095 -2.6983994990587234]},
       :address
       {:admin-district "Basque Country",
        :admin-district2 "VZ",
        :country-region "Spain",
        :formatted-address "Mundaca, Basque Country, Spain",
        :locality "Mundaca"}}]}]})

(deftest test-address
  (let [address (address response)]
    (is (= "Mundaka, Basque Country, Spain" (:formatted-address address)))
    (is (= "Mundaka" (:locality address)))
    (is (= "Basque Country" (:region address)))
    (let [country (:country address)]
      (is (= "Spain" (:name country))))))

(deftest test-addresses
  (let [addresses (addresses response)]
    (let [address (first addresses)]
      (is (= "Mundaka, Basque Country, Spain" (:formatted-address address)))
      (is (= "Mundaka" (:locality address)))
      (is (= "Basque Country" (:region address)))
      (let [country (:country address)]
        (is (= "Spain" (:name country)))))
    (let [address (second addresses)]
      (is (= "Mundaca, Basque Country, Spain" (:formatted-address address)))
      (is (= "Mundaca" (:locality address)))
      (is (= "Basque Country" (:region address)))
      (let [country (:country address)]
        (is (= "Spain" (:name country)))))))

(deftest test-location
  (is (= {:longitude -2.7003700733184814, :latitude 43.40943145751953}
         (location response))))

(deftest test-locations
  (is (= [{:longitude -2.7003700733184814, :latitude 43.40943145751953}
          {:longitude -2.6983994990587234, :latitude 43.40989999473095}]
         (locations response))))
