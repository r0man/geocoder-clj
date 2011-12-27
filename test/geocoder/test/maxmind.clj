(ns geocoder.test.maxmind
  (:use clojure.test
        geocoder.maxmind
        geocoder.provider))

(def ip-address "92.229.192.11")

(deftest test-make-geocoder
  (is (nil? (make-geocoder {:database "NOT-EXISTING"})))
  (is (instance? geocoder.maxmind.Geocoder (make-geocoder {:database default-database}))))

(deftest test-geocode-ip-address
  (is (nil? (geocode-ip-address *geocoder* "127.0.0.1" nil)))
  (let [result (first (geocode-ip-address *geocoder* ip-address nil))]
    (is (= "Maxmind" (:provider result)))
    (is (= "Germany" (:name (:country result))))
    (is (= "de" (:iso-3166-1-alpha-2 (:country result))))
    (is (= "16" (:id (:region result))))
    (is (= "Berlin" (:city result)))
    (is (= 52.516693115234375 (:latitude (:location result))))
    (is (= 13.399993896484375 (:longitude (:location result))))))

(deftest test-supports-address-geocoding?
  (is (not (supports-address-geocoding? *geocoder*))))

(deftest test-supports-ip-address-geocoding?
  (is (supports-ip-address-geocoding? *geocoder*)))

(deftest test-supports-location-geocoding?
  (is (not (supports-location-geocoding? *geocoder*))))

(deftest test-wrap-maxmind
  (let [response ((wrap-maxmind identity) {:remote-addr ip-address})]
    (is (= (first (geocode-ip-address *geocoder* ip-address nil)) (:maxmind response)))))