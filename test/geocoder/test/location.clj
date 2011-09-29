(ns geocoder.test.location
  (:use clojure.test
        geocoder.location))

(deftest test-to-str
  (is (= "18.1586,-76.3553" (to-str {:latitude 18.1586 :longitude -76.3553}))))

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

(deftest test-distance-in-km
  (let [berlin (make-location 52.54258 13.42299)
        paris (make-location 48.856614 2.3522219)]
    (is (= 0.0 (distance-in-km berlin berlin)))
    (is (= 879.5734420795757 (distance-in-km berlin paris)))
    (is (= (distance-in-km berlin paris)
           (distance-in-km paris berlin)))))