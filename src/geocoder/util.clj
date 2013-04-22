(ns geocoder.util
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as client]
            [geo.core :refer [point-x point-y]]
            [inflections.core :refer [hyphenize]]))

(defn format-point
  "Format `point` in latitude, longitude order."
  [point]
  (format "%s,%s" (point-y point)) (point-x point))

(defn fetch-json
  "Send the request, parse the hyphenized JSON body of the response."
  [request]
  (let [read-json #(parse-string %1 true)]
    (->> (client/request request)
         :body read-json hyphenize)))
