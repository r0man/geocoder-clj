(ns geocoder.yahoo
  (:use [clojure.string :only (blank? lower-case)]
        geocoder.config
        geocoder.location
        geocoder.provider))

(def ^{:dynamic true} *geocoder* nil)

(defrecord Geocoder [name key])

(defn make-geocoder
  "Make a Google geocoder."
  [attributes]
  (if (:key attributes)
    (map->Geocoder (assoc attributes :name "Yahoo"))))

(defn- make-request
  "Make a Yahoo geocoder request map."
  [provider & options]
  {:method :get
   :url "http://where.yahooapis.com/geocode"
   :query-params {:appid (:api-key provider) :flags "J"}})

(defn- fetch
  "Fetch and decode the Yahoo geocoder response."
  [request provider]
  (let [response
        (-> (fetch-json request)
            :result-set :results)]
    (if (not (blank? (:country (first response))))
      (decode response provider))))

(extend-type Geocoder
  IAddress
  (city [_ address]
    (:city address))
  (country [_ address]
    {:name (:country address)
     :iso-3166-1-alpha-2 (lower-case (:countrycode address))})
  (location [_ address]
    (make-location
     (:latitude address)
     (:longitude address)))
  (street-name [_ address]
    (:street address))
  (street-number [_ address]
    (:house address))
  (postal-code [_ address]
    (:postal address))
  (region [_ address]
    (:state address)))

(extend-type Geocoder
  IGeocodeAddress
  (geocode-address [provider address options]
    (-> (make-request provider options)
        (assoc-in [:query-params :q] address)
        (fetch provider))))

(extend-type Geocoder
  IGeocodeLocation
  (geocode-location [provider location options]
    (-> (make-request provider options)
        (assoc-in [:query-params :location] (to-str location))
        (assoc-in [:query-params :gflags] "R")
        (fetch provider))))

(alter-var-root #'*geocoder* (constantly (make-geocoder (:yahoo *config*))))
