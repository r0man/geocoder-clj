(ns geocoder.core
  (:require [geocoder.provider :as provider])
  (:use geocoder.config))

(def ^{:dynamic true} *providers* [])

(defn- find-providers [predicate]
  (filter predicate *providers*))

(defn- geocode-first [providers geocode-fn what options]
  (first (filter (complement empty?) (map #(geocode-fn %1 what options) providers))))

(defn geocode-address
  "Geocode the address."
  [address & options]
  (-> (find-providers provider/supports-address-geocoding?)
      (geocode-first provider/geocode-address address options)))

(defn geocode-ip-address
  "Geocode the internet address."
  [ip-address & options]
  (-> (find-providers provider/supports-ip-address-geocoding?)
      (geocode-first provider/geocode-ip-address ip-address options)))

(defn geocode-location
  "Geocode the location."
  [location & options]
  (-> (find-providers provider/supports-location-geocoding?)
      (geocode-first provider/geocode-location location options)))

(defn init-providers []
  (doseq [provider [:google :bing :yahoo :geonames :maxmind]
          :let [ns (symbol (str "geocoder." (name provider)))]]
    (require ns)
    (if-let [geocoder ((ns-resolve ns 'make-geocoder) (provider *config*))]
      (alter-var-root #'*providers* conj geocoder))))

(init-providers)
