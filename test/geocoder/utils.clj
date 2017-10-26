(ns geocoder.utils
  (:require [clojure.test :refer :all]))

(def tolerance 0.01)

(defn approx=
  [a b]
  (<= (- a tolerance) b (+ a tolerance)))

(deftest approx=-test
  (is (true? (approx= 1 (+ 1 tolerance))))
  (is (false? (approx= 1 2))))
