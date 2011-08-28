(ns geocoder.test.location
  (:use clojure.test
        geocoder.location))

(deftest test-format-location
  (is (= "18.1586,-76.3553" (format-location {:latitude 18.1586 :longitude -76.3553}))))

(deftest test-latitude
  (is (= 43.4094 (latitude {:latitude 43.4094 :longitude -2.7003})))
  (is (= 43.4094 (latitude (make-location 43.4094 -2.7003)))))

(deftest test-longitude
  (is (= -2.7003 (longitude {:latitude 43.4094 :longitude -2.7003})))
  (is (= -2.7003 (longitude (make-location 43.4094 -2.7003)))))

(deftest test-make-location
  (let [location (make-location 43.4094 -2.7003)]
    (is (= 43.4094 (:latitude location)))
    (is (= -2.7003 (:longitude location))))
  (is (= (make-location 43.4094 -2.7003)
         (make-location "43.4094" "-2.7003"))))
