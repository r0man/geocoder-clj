(ns geocode.yahoo
  (:require [clj-http.client :as client])
  (:use [clojure.string :only (lower-case join)]
        [clojure.data.json :only (read-json)]
        [inflections.core :only (hyphenize-keys)]
        geocode.helper))

(def *endpoint*
  "http://where.yahooapis.com/geocode")

(defn geocode-address [address & options]
  (-> (client/get
       *endpoint*
       {:query-params
        {:appid *api-key*
         :flags "J"
         :q address}})
      :body read-json hyphenize-keys))

(defn geocode-location [location & options]
  (-> (client/get
       *endpoint*
       {:query-params
        {:appid *api-key*
         :flags "J"
         :gflags "R"
         :location (format-location location)}})
      :body read-json hyphenize-keys))

(defn to-address [address]
  {:country {:name (:country address) :iso-3166-1-alpha-2 (lower-case (:countrycode address))}
   :formatted-address (join ", " (remove nil? [(:city address) (:state address) (:country address)]))
   :locality (:city address)
   :region (:state address)
   :street-address (:street address)})

(defn results
  "Returns the results in the response."
  [response] (:results (:result-set response)))

(defn addresses
  "Returns the addresses in the response."
  [response] (map to-address (results response)))

(defn address
  "Returns the first address in the response."
  [response] (first (addresses response)))

(defn locations
  "Returns the locations in the response."
  [response]
  (map (fn [result]
         {:latitude (Double/parseDouble (:latitude result))
          :longitude (Double/parseDouble (:longitude result))})
       (results response)))

(defn location
  "Returns the first location in the response."
  [response] (first (locations response)))
