(ns geocoder.util
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as client]
            [geo.core :refer [IPoint point-x point-y]]
            [inflections.core :refer [hyphenize]]
            [inflections.util :refer [parse-location]]))

(defn format-point
  "Format `point` in latitude, longitude order."
  [point]
  (format "%s,%s" (point-y point) (point-x point)))

(defn fetch-json
  "Send the request, parse the hyphenized JSON body of the response."
  [request]
  (let [read-json #(parse-string %1 true)]
    (->> (client/request request)
         :body read-json hyphenize)))

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
