(ns geocoder.maxmind2
  (:import [com.maxmind.geoip2 DatabaseReader$Builder]
           [com.maxmind.geoip2.record AbstractNamedRecord]
           [com.maxmind.geoip2 GeoIp2Provider]
           [com.maxmind.db Reader$FileMode]
           [java.net InetAddress])
  (:require [clojure.java.io :as io]
            [clojure.string :refer [lower-case]]
            [geo.core :refer [point point-x point-y]]
            [gavagai.core :as g]))

(def default-path
  (str (System/getenv "HOME") "/.maxmind/GeoLite2-City.mmdb"))

(def file-modes
  "Get database file modes by name"
  {:memory-mapped (Reader$FileMode/valueOf "MEMORY_MAPPED")
   :memory (Reader$FileMode/valueOf "MEMORY")})

(defn- names-as-kw
  "Make keywords of Java response names"
  [^AbstractNamedRecord o]
  (persistent!
   (reduce
    (fn [acc [k v]]
      (assoc! acc (keyword k) v))
    (transient {}) (.getNames o))))

(def translator
  "Translate Java data structures to map"
  (g/register-converters
    {:exclude [:class]}
    [["com.maxmind.geoip2.model.CityResponse"]
     ["com.maxmind.geoip2.model.ConnectionTypeResponse"]
     ["com.maxmind.geoip2.model.IspResponse"]
     ["com.maxmind.geoip2.model.DomainResponse"]
     ["com.maxmind.geoip2.record.City" :exclude [:names] :add {:names names-as-kw}]
     ["com.maxmind.geoip2.record.Continent" :exclude [:names] :add {:names names-as-kw}]
     ["com.maxmind.geoip2.record.Subdivision" :exclude [:names] :add {:names names-as-kw}]
     ["com.maxmind.geoip2.record.RepresentedCountry" :exclude [:names] :add {:names names-as-kw}]
     ["com.maxmind.geoip2.record.Location"]
     ["com.maxmind.geoip2.record.MaxMind"]
     ["com.maxmind.geoip2.record.Postal"]
     ["com.maxmind.geoip2.record.Country" :exclude [:names] :add {:names names-as-kw}]
     ["com.maxmind.geoip2.record.Traits"]]))

(defn make-db
  "Returns a locator from either a String file path, a File object or an InputStream.
  This locator is threadsafe. Takes 3 optional arguments:
 + db-path - defaults to $HOME/.maxmind/GeoLite2-City.mmdb
 + **:locales** List of recognised locales, for example [:en :fr], used for default :name
 + **:file-mode** Either *:memory-mapped* (default) or *:memory*"
  ([]
   make-db default-path)
  ([db-path & {:keys [locales file-mode]}]
    (let [path (cond (instance? java.io.InputStream db-path) db-path
              :else (io/file (or db-path default-path)))
        builder (DatabaseReader$Builder. path)]
    (-> builder
        (cond-> locales (.locales (map name locales)))
        (cond-> file-mode (.fileMode (get file-modes file-mode)))
        (.build)))))

(defn city
  "Returns the city of `address`."
  [address]
  (:name (:city address)))

(defn country
  "Returns the country of `address`."
  [address]
  {:name (:name (:country address))
   :iso-3166-1-alpha-2 (if-let [code (:iso-code (:country address))] (lower-case code))})

(defn location
  "Returns the location (WGS 84 point) of `address`."
  [address]
  (point
   4326 ; Spatial Reference System for World Geodetic System 1984
   (double (:longitude (:location address)))
   (double (:latitude (:location address)))))

(defn latitude
  "Returns the latitude of `address`."
  [address]
  (:latitude (:location address)))

(defn region-id
  "Returns the region id of `address`."
  [address]
  (:geo-name-id (:most-specific-subdivision address)))

(defn geocode-ip-address-full
  "Lookup an IP given as a String in the given db and returns a full results map"
  [^GeoIp2Provider db ip]
  (if-let [city (.city db (InetAddress/getByName ip))]
    (g/translate translator city {:lazy? false})))

(defn geocode-ip-address
  "Geocode an internet address using one of Maxmind's GeoIP2 databases."
  [db ip-address]
  (if-let [result (geocode-ip-address-full db ip-address)]
    (if (not (= -180.0 (latitude result)))
      {:country (country result)
       :city (city result)
       :location (location result)
       :region-id (region-id result)})))

(defn wrap-maxmind
  "Wrap the Maxmind GeoIP2 middleware around a Ring handler."
  [handler db-path]
  (let [db (make-db db-path)]
    (fn [request]
      (if db
        (let [address (geocode-ip-address db (:remote-addr request))]
          (handler (assoc request :maxmind address)))
        (handler request)))))
