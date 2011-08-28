(ns geocoder.yahoo
  (:require [clj-http.client :as client])
  (:use [clojure.string :only (lower-case join)]
        [clojure.data.json :only (read-json)]
        [inflections.core :only (hyphenize-keys)]
        geocoder.address
        geocoder.helper
        geocoder.location
        geocoder.provider))

(def *endpoint*
  "http://where.yahooapis.com/geocode")

(defn request [provider]
  {:method :get
   :url *endpoint*
   :query-params {:appid (:api-key provider) :flags "J"}})

(defrecord Result []
  IAddress
  (city [result]
    (:city result))
  (country [result]
    {:name (:country result)
     :iso-3166-1-alpha-2 (lower-case (:countrycode result))})
  (location [result]
    (make-location
     (:latitude result)
     (:longitude result)))
  (street-name [result]
    (:street result))
  (street-number [result]
    (:house result))
  (postal-code [result]
    (:postal result))
  (region [result]
    (:state result)))

(defrecord Provider [api-key]
  IProvider
  (geocode [provider address options]
    (results
     provider
     (-> (request provider)
         (assoc-in [:query-params :q] address)
         json-request)))
  (results [provider response]
    (map #(merge (Result.) %) (:results (:result-set response))))
  (reverse-geocode [provider location options]
    (results
     provider
     (-> (request provider)
         (assoc-in [:query-params :location] (format-location location))
         (assoc-in [:query-params :gflags] "R")
         json-request))))
