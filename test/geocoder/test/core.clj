(ns geocoder.test.core
  (:use clojure.test
        geocoder.core))

(deftest test-geocode-via-bing
  (with-provider (geocoder.bing.Provider. "AhiRsght2jQhqZaHpUAvXhCjvNfyWxb0Xb4EwAW81LUgvBa68FcnL9mOFDLKoQGl")
    (let [address (first (geocode "Senefelderstraße 24, 10437 Berlin"))]
      (is (= "Senefelderstraße 24" (:street-name address)))
      (is (nil? (:street-number address)))
      (is (= "10437" (:postal-code address)))
      (is (= "Berlin" (:city address)))
      (is (nil? (:region address)))
      (let [country (:country address)]
        (is (nil? (:iso-3166-1-alpha-2 country)))
        (is (= "Germany" (:name country))))
      (let [location (:location address)]
        (is (= 52.54254 (:latitude location)))
        (is (= 13.423033 (:longitude location)))))))

(deftest test-geocode-via-google
  (with-provider (geocoder.google.Provider.)
    (let [address (first (geocode "Senefelderstraße 24, 10437 Berlin"))]
      (is (= "Senefelderstraße" (:street-name address)))
      (is (= "24" (:street-number address)))
      (is (= "10437" (:postal-code address)))
      (is (= "Berlin" (:city address)))
      (is (= "Berlin" (:region address)))
      (let [country (:country address)]
        (is (= "de" (:iso-3166-1-alpha-2 country)))
        (is (= "Germany" (:name country))))
      (let [location (:location address)]
        (is (= 52.54258 (:latitude location)))
        (is (= 13.42299 (:longitude location)))))))

(deftest test-geocode-via-yahoo
  (with-provider (geocoder.yahoo.Provider. "dj0yJmk9TUhNRlZOM2JseHphJmQ9WVdrOVdXTnZVRTV5Tm1jbWNHbzlNakEyTWpVek1UUTJNZy0tJnM9Y29uc3VtZXJzZWNyZXQmeD1hMw--")
    (let [address (first (geocode "Senefelderstraße 24, 10437 Berlin"))]
      (is (= "Senefelderstrasse" (:street-name address)))
      (is (= "24" (:street-number address)))
      (is (= "10437" (:postal-code address)))
      (is (= "Berlin" (:city address)))
      (is (= "Berlin" (:region address)))
      (let [country (:country address)]
        (is (= "de" (:iso-3166-1-alpha-2 country)))
        (is (= "Germany" (:name country))))
      (let [location (:location address)]
        (is (= 52.542526 (:latitude location)))
        (is (= 13.42324 (:longitude location)))))))
