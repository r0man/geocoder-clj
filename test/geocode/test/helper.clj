(ns geocode.test.helper
  (:use clojure.test
        geocode.helper))

(deftest test-format-location
  (is (= "18.1586,-76.3553" (format-location {:latitude 18.1586 :longitude -76.3553}))))