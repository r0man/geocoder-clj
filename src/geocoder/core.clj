(ns geocoder.core
  (:require [clj-http.client :as client]
            geocoder.bing
            geocoder.google
            geocoder.maxmind
            geocoder.yahoo)
  (:use [clojure.data.json :only (read-json)]
        [inflections.core :only (hyphenize)]
        geocoder.address
        geocoder.provider))

(def ^{:dynamic true} *provider*
  (geocoder.google.Provider.))

(defn- fetch [request]
  (->> (client/request request)
       :body read-json
       hyphenize
       (results *provider*)
       (map to-address)))

(defn geocode
  "Geocode the address via the current *provider*."
  [address & options]
  (fetch (geocode-request *provider* address options)))

(defn reverse-geocode
  "Reverse geocode the location via the current *provider*."
  [location & options]
  (fetch (reverse-geocode-request *provider* location options)))

(defmacro with-provider
  "Evaluate body with provider bound to *provider*."
  [provider & body]
  `(binding [*provider* ~provider]
     ~@body))
