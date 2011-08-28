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
  (map to-address (provider/geocode *provider* address options)))

(defn reverse-geocode
  "Reverse geocode the location via the current *provider*."
  [location & options]
  (map to-address (provider/reverse-geocode *provider* location options)))

(defmacro with-provider
  "Evaluate body with provider bound to *provider*."
  [provider & body]
  `(binding [*provider* ~provider]
     ~@body))
