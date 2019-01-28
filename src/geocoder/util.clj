(ns geocoder.util
  (:require [cheshire.core :refer [parse-string]]
            [clj-http.client :as client]
            [clojure.string :refer [split]]
            [inflections.core :refer [hyphenate-keys]]
            [no.en.core :refer [parse-double]]))

(defn parse-location [s]
  (let [parts (->> (split (str s) #"\s*,\s*")
                   (map parse-double)
                   (remove nil?))]
    (if (= 2 (count parts))
      (zipmap [:lat :lng] parts ))))

(defn format-location
  "Format `location` in latitude, longitude order."
  [location]
  (format "%s,%s" (:lat location) (:lng location)))

(defn fetch-json
  "Send the request, parse the hyphenated JSON body of the response."
  [request]
  (let [read-json #(parse-string %1 keyword)]
    (->> (merge
          {:throw-exceptions true}
          request)
         (client/request)
         :body read-json hyphenate-keys)))
