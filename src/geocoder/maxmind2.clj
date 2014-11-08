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

(def ^:no-doc file-modes
  {:memory-mapped (Reader$FileMode/valueOf "MEMORY_MAPPED")
   :memory (Reader$FileMode/valueOf "MEMORY")})

(defn- ^:no-doc names-as-kw
  [^AbstractNamedRecord o]
  (persistent!
   (reduce
    (fn [acc [k v]]
      (assoc! acc (keyword k) v))
    (transient {}) (.getNames o))))

(def ^:no-doc translator
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

;(defn make-db-old
;  "Make a Maxmind GeoIP2 database."
;  [& [path]]
;  (let [path (or path default-path)]
;    (if (.exists (file path))
;      (LookupService. path))))

(defn make-db
  "Returns a locator from either a String file path, a File object or an InputStream.
  This locator is threadsafe. Takes 3 optional arguments:
 + **:db-path** - defaults to $HOME/.maxmind/GeoLite2-City.mmdb
 + **:locales** List of recognised locales, for example [:en :fr], used for default :name
 + **:file-mode** Either *:memory-mapped* (default) or *:memory*"
  [& {:keys [db-path locales file-mode]}]
  (let [path (if (instance? java.io.InputStream db-path) db-path (io/file (or db-path default-path)))
        builder (DatabaseReader$Builder. path)]
    (-> builder
        (cond-> locales (.locales (map name locales)))
        (cond-> file-mode (.fileMode (get file-modes file-mode)))
        (.build))))

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

;(defn geocode-ip-address-old
;  "Geocode an internet address using one of Maxmind's GeoIP2
;  databases."
;  [db ip-address]
;  (if-let [result (.getLocation db ip-address)]
;    (if (not (=  -180.0 (. result latitude)))
;      {:country (country result)
;       :city (city result)
;       :location (location result)
;       :region-id (region-id result)})))

(defn geocode-ip-address
  "Lookup an IP given as a String in the given locator and returns a map with results"
  [^GeoIp2Provider db ip]
  (if-let [city (.city db (InetAddress/getByName ip))]
    (g/translate translator city {:lazy? false})))

(defn wrap-maxmind
  "Wrap the Maxmind GeoIP2 middleware around a Ring handler."
  [handler db-path]
  (let [db (make-db db-path)]
    (fn [request]
      (if db
        (let [address (geocode-ip-address db (:remote-addr request))]
          (handler (assoc request :maxmind address)))
        (handler request)))))
