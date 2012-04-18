(ns geocoder.geonames
  (:use [clojure.string :only (lower-case)]
        geocoder.config
        geocoder.location
        geocoder.provider))

(def ^{:dynamic true} *geocoder* nil)

(defrecord Geocoder [name key])

(defn make-geocoder
  "Make a Geonames geocoder."
  [attributes]
  (if (:key attributes)
    (Geocoder. "Geonames" (:key attributes))))

(defn- make-request
  "Make a Geonames geocode request map."
  [provider & options]
  {:method :get :query-params {:username (:key provider)}})

(defn- fetch
  "Fetch and decode the Geonames geocode response."
  [request provider]
  (-> (fetch-json request)
      :postal-codes
      (decode provider)))

(extend-type Geocoder
  IAddress
  (city [_ address]
    (:place-name address))
  (country [_ address]
    {:iso-3166-1-alpha-2 (lower-case (:country-code address))})
  (location [_ address]
    (make-location (:lat address) (:lng address)))
  (street-name [_ address]
    (:address-line (:address address)))
  (street-number [_ address]
    nil)
  (postal-code [_ address]
    (:postal-code address))
  (region [_ address]
    (:admin-name1 address)))

(extend-type Geocoder
  IGeocodeAddress
  (geocode-address [provider address options]
    (-> (make-request provider options)
        (assoc :url "http://api.geonames.org/postalCodeSearchJSON")
        (assoc-in [:query-params :placename] address)
        (fetch provider))))

(extend-type Geocoder
  IGeocodeLocation
  (geocode-location [provider location options]
    (-> (make-request provider options)
        (assoc :url (str "http://api.geonames.org/findNearbyPostalCodesJSON"))
        (assoc-in [:query-params :lat] (latitude location))
        (assoc-in [:query-params :lng] (longitude location))
        (fetch provider))))

(alter-var-root #'*geocoder* (constantly (make-geocoder (:geonames *config*))))
