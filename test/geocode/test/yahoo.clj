(ns geocode.test.yahoo
  (:use clojure.test
        geocode.yahoo))

(def response
  {:result-set
   {:results
    [{:country "Spain",
      :offsetlat "43.409430",
      :line4 "Spain",
      :longitude "-2.700369",
      :state "Basque",
      :xstreet "",
      :woetype 9,
      :radius 57700,
      :uzip "48360",
      :name "",
      :countycode "",
      :hash "",
      :neighborhood "",
      :line1 "",
      :line2 "Mundaka",
      :unittype "",
      :line3 "",
      :offsetlon "-2.700369",
      :latitude "43.409430",
      :city "Mundaka",
      :street "",
      :countrycode "ES",
      :woeid 12602136,
      :unit "",
      :postal "",
      :house "",
      :county "Bizkaia",
      :statecode ""}]}})

(deftest test-address
  (let [address (address response)]
    (is (= "Mundaka, Basque, Spain" (:formatted-address address)))
    (is (= "Mundaka" (:locality address)))
    (is (= "Basque" (:region address)))
    (let [country (:country address)]
      (is (= "es" (:iso-3166-1-alpha-2 country)))
      (is (= "Spain" (:name country))))))

(deftest test-addresses
  (let [addresses (addresses response)]
    (let [address (first addresses)]
      (is (= "Mundaka, Basque, Spain" (:formatted-address address)))
      (is (= "Mundaka" (:locality address)))
      (is (= "Basque" (:region address)))
      (let [country (:country address)]
        (is (= "es" (:iso-3166-1-alpha-2 country)))
        (is (= "Spain" (:name country)))))))

(deftest test-location
  (is (= {:longitude -2.700369 :latitude 43.409430}
         (location response))))

(deftest test-locations
  (is (= [{:longitude -2.700369 :latitude 43.409430}]
         (locations response))))
