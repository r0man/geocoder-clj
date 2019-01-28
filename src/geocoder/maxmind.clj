(ns geocoder.maxmind
  (:import [com.maxmind.geoip2 DatabaseReader$Builder]
           [com.maxmind.geoip2.exception AddressNotFoundException]
           [java.net InetAddress])
  (:require [clojure.string :refer [lower-case]]
            [geo.core :refer [point point-x point-y]]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def default-path
  (str (System/getenv "HOME") "/.maxmind/GeoLite2-City.mmdb"))

(defn db
  "Make a Maxmind GeoIP database."
  [& [path]]
  (let [path (io/file (or path default-path))]
    (when (.exists path)
      (.build (DatabaseReader$Builder. path)))))

(defn city
  "Returns the city of `address`."
  [result]
  (some-> (.getCity result) (.getName)))

(defn country
  "Returns the country of `result`."
  [result]
  (let [country (.getCountry result)]
    {:name (.getName country)
     :iso-3166-1-alpha-2 (some-> (.getIsoCode country) str/lower-case)}))

(defn location
  "Returns the location of `address`."
  [result]
  (when-let [location (.getLocation result)]
    (point 4326 (double (.getLongitude location)) (double (.getLatitude location)))))

(defn region-id
  "Returns the region id of `address`."
  [address]
  (. address region))

(defn geocode-ip-address
  "Geocode an internet address using one of Maxmind's GeoIP
  databases."
  [db ip-address]
  (try
    (let [ip-address (InetAddress/getByName ip-address)]
      (let [result (.city db ip-address)]
        {:country (country result)
         :city (city result)
         :location (location result)
         ;; :region-id (region-id result)
         }))
    (catch AddressNotFoundException e
      nil)))

(defn wrap-maxmind
  "Wrap the Maxmind middleware around a Ring handler."
  [handler db-path]
  (let [db (db db-path)]
    (fn [request]
      (if db
        (let [address (geocode-ip-address db (:remote-addr request))]
          (handler (assoc request :maxmind address)))
        (handler request)))))
