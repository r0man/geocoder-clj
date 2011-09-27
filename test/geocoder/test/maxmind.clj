(ns geocoder.test.maxmind
  (:use geocoder.maxmind
        clojure.test))

(deftest test-geocode
  (let [database "GeoLiteCity.dat"]
    (if (.exists (java.io.File. database))
      (with-database database
        (is (nil? (geocode "127.0.0.1")))
        (let [result (geocode "92.229.192.11")]
          (is (= "Germany" (:name (:country result))))
          (is (= "de" (:iso-3166-1-alpha-2 (:country result))))
          (is (= "16" (:id (:region result))))
          (is (= "Berlin" (:city result)))
          (is (= 52.516693115234375 (:latitude (:location result))))
          (is (= 13.399993896484375 (:longitude (:location result))))
          (is (= 0 (:area-code result)))
          (is (= 0 (:dma-code result)))
          (is (= 0 (:metro-code result))))))))
