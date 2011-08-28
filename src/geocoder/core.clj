(ns geocoder.core
  (:require [geocoder.provider :as provider]
            geocoder.bing
            geocoder.google
            geocoder.yahoo)
  (:use geocoder.address
        geocoder.helper))

(def *provider*
  (geocoder.google.Provider.))

(defn geocode
  "Geocode the address via the current *provider*."
  [address & options]
  (->> (provider/geocode-request *provider* address options)
       (json-request)
       (provider/results *provider*)
       (map to-address)))

(defn reverse-geocode
  "Reverse geocode the location via the current *provider*."
  [location & options]
  (->> (provider/reverse-geocode-request *provider* location options)
       (json-request)
       (provider/results *provider*)
       (map to-address)))

(defmacro with-provider
  "Evaluate body with provider bound to *provider*."
  [provider & body]
  `(binding [*provider* ~provider]
     ~@body))
