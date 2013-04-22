(ns geocoder.maxmind
  (:import [com.maxmind.geoip LookupService Location])
  (:require [clojure.java.io :refer [file]]
            [clojure.string :refer [lower-case]]
            [geo.core :refer [point point-x point-y]]))

(def default-path
  (str (System/getenv "HOME") "/.maxmind/GeoLiteCity.dat"))

(defn make-db
  "Make a Maxmind geocoder."
  [& [path]]
  (let [path (or path default-path)]
    (if (.exists (file path))
      (LookupService. path))))

(defn city
  "Returns the city of `address`."
  [address]
  (. address city))

(defn country
  "Returns the country of `address`."
  [address]
  {:name (. address countryName)
   :iso-3166-1-alpha-2 (if-let [code (. address countryCode)] (lower-case code))})

(defn location
  "Returns the location of `address`."
  [address]
  (point
   4326
   (double (. address longitude))
   (double (. address latitude))))

(defn region-id
  "Returns the region id of `address`."
  [address]
  (. address region))

(defn geocode-ip-address
  [db ip-address]
  (if-let [result (.getLocation db ip-address)]
    (if (not (=  -180.0 (. result latitude)))
      {:country (country result)
       :city (city result)
       :location (location result)
       :region-id (region-id result)})))

(defn wrap-maxmind
  "Wrap the Maxmind middleware around a Ring handler."
  [handler db-path]
  (let [db (make-db db-path)]
    (fn [request]
      (if db
        (let [address (geocode-ip-address db (:remote-addr request))]
          (handler (assoc request :maxmind address)))
        (handler request)))))
