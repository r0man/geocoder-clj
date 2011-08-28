(ns geocoder.bing
  (:require [clj-http.client :as client])
  (:use [clojure.data.json :only (read-json)]
        [inflections.core :only (hyphenize-keys)]
        geocoder.helper))

(def *locations-url*
  "http://dev.virtualearth.net/REST/v1/Locations")

(def *point-url*
  "http://dev.virtualearth.net/REST/v1/Locations/point")

(defn geocode-address [address & options]
  (-> (client/get
       *locations-url*
       {:query-params
        {:query address
         :key *api-key*
         :includeEntityTypes "entityTypes"}})
      :body read-json hyphenize-keys))

(defn geocode-location [location & options]
  (-> (client/get
       *point-url*
       {:query-params
        {:key *api-key*
         :includeEntityTypes "entityTypes"
         :point (format-location location)}})
      :body read-json hyphenize-keys))

(defn to-address [address]
  {:country {:name (:country-region address)}
   :formatted-address (:formatted-address address)
   :locality (:locality address)
   :region (:admin-district address)})

(defn results
  "Returns the results in the response."
  [response] (flatten (map :resources (:resource-sets response))))

(defn addresses
  "Returns the addresses in the response."
  [response] (map to-address (map :address (results response))))

(defn address
  "Returns the first address in the response."
  [response] (first (addresses response)))

(defn locations
  "Returns the locations in the response."
  [response]
  (->> (results response)
       (map (comp :coordinates :point))
       (map #(zipmap [:latitude :longitude] %))))

(defn location
  "Returns the first location in the response."
  [response] (first (locations response)))
