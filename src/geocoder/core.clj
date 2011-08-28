(ns geocoder.core
  (:require [clj-http.client :as client]
            [geocoder.provider :as provider]
            geocoder.bing
            geocoder.google
            geocoder.yahoo)
  (:use [clojure.data.json :only (read-json)]
        [inflections.core :only (hyphenize-keys)]
        geocoder.address))

(def *provider*
  (geocoder.google.Provider.))

(defn- request [request]
  (-> (client/request request) :body read-json hyphenize-keys))

(defn geocode
  "Geocode the address via the current *provider*."
  [address & options]
  (->> (provider/geocode-request *provider* address options)
       (request)
       (provider/results *provider*)
       (map to-address)))

(defn reverse-geocode
  "Reverse geocode the location via the current *provider*."
  [location & options]
  (->> (provider/reverse-geocode-request *provider* location options)
       (request)
       (provider/results *provider*)
       (map to-address)))

(defmacro with-provider
  "Evaluate body with provider bound to *provider*."
  [provider & body]
  `(binding [*provider* ~provider]
     ~@body))
