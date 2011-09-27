(ns geocoder.maxmind
  (:import [com.maxmind.geoip LookupService])
  (:use [clojure.string :only (lower-case)]))

(def default-database
  (str (System/getenv "HOME") "/.maxmind/GeoLiteCity.dat"))

(defn make-service
  "Make a new Maxmind lookup service."
  [path] (LookupService. path))

(def ^:dynamic *service*
  (try (make-service default-database)
       (catch Exception _ nil)))

(defn- decode
  "Decode the Maxmind result into a map."
  [result]
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

(defn geocode-ip
  "Geocode the ip address."
  [ip-address] (decode (.getLocation *service* ip-address)))

(defmacro with-maxmind
  "Evaluate body with the Maxmind lookup service bound to *service*."
  [database & body]
  `(with-open [database# (make-service ~database)]
     (binding [*service* database#]
       ~@body)))

(defn wrap-maxmind
  "Wrap the Maxmind middleware around a Ring handler."
  [handler]
  (fn [request]
    (handler
     (if *service*
       (assoc request :maxmind (geocode-ip (:remote-addr request)))
       request))))
