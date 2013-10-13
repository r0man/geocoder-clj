(ns geocoder.util
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as client]
            [clojure.string :refer [split]]
            [geo.core :refer [IPoint point point-x point-y]]
            [inflections.core :refer [hyphenize-keys]]
            [no.en.core :refer [parse-double]]))

(defn parse-location [s]
  (let [parts (->> (split (str s) #"\s*,\s*")
                   (map parse-double)
                   (remove nil?))]
    (if (= 2 (count parts))
      (apply point 4326 (reverse parts)))))

(defn format-point
  "Format `point` in latitude, longitude order."
  [point]
  (format "%s,%s" (point-y point) (point-x point)))

(defn fetch-json
  "Send the request, parse the hyphenized JSON body of the response."
  [request]
  (let [read-json #(parse-string %1 true)]
    (->> (client/request request)
         :body read-json hyphenize-keys)))

(extend-protocol IPoint
  String
  (point-x [s]
    (point-x (parse-location s)))
  (point-y [s]
    (point-y (parse-location s)))
  clojure.lang.IPersistentMap
  (point-x [m]
    (or (:longitude m)
        (:lng m)))
  (point-y [m]
    (or (:latitude m)
        (:lat m))))
