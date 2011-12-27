(ns geocoder.maxmind
  (:import [com.maxmind.geoip LookupService Location])
  (:use [clojure.string :only (lower-case)]
        [clojure.java.io :only (file)]
        geocoder.config
        geocoder.location
        geocoder.provider))

(def ^{:dynamic true} *geocoder* nil)

(def default-database
  (str (System/getenv "HOME") "/.maxmind/GeoLiteCity.dat"))

(defrecord Geocoder [name service])

(defn make-geocoder
  "Make a Maxmind geocoder."
  [attributes]
  (if-let [database (file (or (:database attributes) default-database))]
    (if (.exists database)
      (Geocoder. "Maxmind" (LookupService. database)))))

(defn wrap-maxmind
  "Wrap the Maxmind middleware around a Ring handler."
  [handler]
  (fn [request]
    (handler
     (if *geocoder*
       (assoc request :maxmind (first (geocode-ip-address *geocoder* (:remote-addr request) nil)))
       request))))

(extend-type Geocoder
  IAddress
  (city [_ address]
    (. address city))
  (country [_ address]
    {:name (. address countryName)
     :iso-3166-1-alpha-2 (if-let [code (. address countryCode)] (lower-case code))})
  (location [_ address]
    (make-location
     (double (. address latitude))
     (double (. address longitude))))
  (street-name [_ address]
    nil)
  (street-number [_ address]
    nil)
  (postal-code [_ address]
    nil)
  (region [_ address]
    {:id (. address region)}))

(extend-type Geocoder
  IGeocodeInternetAddress
  (geocode-ip-address [provider ip-address options]
    (if-let [location (.getLocation (:service provider) ip-address)]
      (if (not (=  -180.0 (. location latitude)))
        (decode [location] provider)))))

(alter-var-root #'*geocoder* (constantly (make-geocoder (:maxmind *config*))))
