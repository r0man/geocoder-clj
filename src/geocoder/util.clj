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

(defn- read-body [body]
  (cond
    (string? body)
    (try (parse-string body keyword)
         (catch Exception _ body))
    :else body))

(defn fetch-json
  "Send the request, parse the hyphenated JSON body of the response."
  [request]
  (try (->> (merge
             {:as :auto
              :throw-exceptions true
              :coerce :always}
             request)
            (client/request)
            :body read-body hyphenate-keys)
       (catch Exception e
         (throw (ex-info (str "Geocode request failed: " (.getMessage e))
                         (hyphenate-keys (ex-data e)))))))
