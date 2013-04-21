(ns geocoder.bing
  (:require [geo.core :refer [point point-x point-y]]
            [geocoder.config :refer [*config*]]
            [geocoder.provider :refer :all]))

(def ^{:dynamic true} *geocoder* nil)

(defrecord Geocoder [name key])

(defn make-geocoder
  "Make a Bing geocoder."
  [attributes]
  (if (:key attributes)
    (Geocoder. "Bing" (:key attributes))))

(defn- make-request
  "Make a Bing geocode request map."
  [provider & options]
  {:method :get :query-params {:key (:key provider)}})

(defn- fetch
  "Fetch and decode the Bing geocode response."
  [request provider]
  (decode
   (->> (fetch-json request)
        :resource-sets
        (mapcat :resources))
   provider))

(extend-type Geocoder
  IAddress
  (city [_ address]
    (:locality (:address address)))
  (country [_ address]
    {:name (:country-region (:address address))})
  (location [_ address]
    (let [[y x] (:coordinates (:point address))]
      (point 4326 x y)))
  (street-name [_ address]
    (:address-line (:address address)))
  (street-number [_ address]
    nil)
  (postal-code [_ address]
    (:postal-code (:address address)))
  (region [_ address]
    (:state address)))

(extend-type Geocoder
  IGeocodeAddress
  (geocode-address [provider address options]
    (-> (make-request provider options)
        (assoc :url "http://dev.virtualearth.net/REST/v1/Locations")
        (assoc-in [:query-params :query] address)
        (fetch provider))))

(extend-type Geocoder
  IGeocodeLocation
  (geocode-location [provider location options]
    (-> (make-request provider options)
        (assoc :url (str "http://dev.virtualearth.net/REST/v1/Locations/" (point-y location) "," (point-x location)))
        (fetch provider))))

(alter-var-root #'*geocoder* (constantly (make-geocoder (:bing *config*))))
