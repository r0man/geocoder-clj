(ns geocoder.maxmind
  (:import [com.maxmind.geoip LookupService])
  (:use [clojure.string :only (lower-case)]))

(def ^:dynamic *service* nil)

(defn make-service [path]
  (LookupService. path))

(defn- decode [result]
  (if (and result (not (=  -180.0 (. result latitude))))
    {:country
     {:name (. result countryName)
      :iso-3166-1-alpha-2 (if-let [code (. result countryCode)] (lower-case code))}
     :region {:id (. result region)}
     :city (. result city)
     :location
     {:latitude (double (. result latitude))
      :longitude (double (. result longitude))}
     :area-code (. result area_code)
     :dma-code (. result dma_code)
     :metro-code (. result metro_code)}))

(defn geocode
  "Geocode the ip address."
  [ip-address] (decode (.getLocation *service* ip-address)))

(defmacro with-database [database & body]
  `(with-open [database# (make-service ~database)]
     (binding [*service* database#]
       ~@body)))
