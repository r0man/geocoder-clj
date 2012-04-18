(ns geocoder.google
  (:use [clojure.string :only (lower-case)]
        geocoder.location
        geocoder.provider))

(def ^{:dynamic true} *geocoder* nil)

(defrecord Geocoder [name])

(defn make-geocoder
  "Make a Google geocoder."
  [attributes] (Geocoder. "Google"))

(defn- make-request
  "Make a Google geocode request map."
  [provider & options]
  {:method :get
   :url "http://maps.google.com/maps/api/geocode/json"
   :query-params {:sensor false :language "en"}})

(defn- extract
  "Extract the address component from response."
  [response component]
  (first (filter #(contains? (set (:types %)) component) (:address-components response))))

(defn- fetch
  "Fetch and decode the Google geocode response."
  [request provider]
  (-> (fetch-json request)
      :results (decode provider)))

(extend-type Geocoder
  IAddress
  (city [_ address]
    (:long-name (extract address "locality")))
  (country [_ address]
    {:name (:long-name (extract address "country"))
     :iso-3166-1-alpha-2 (lower-case (:short-name (extract address "country")))})
  (location [_ address]
    (make-location
     (:lat (:location (:geometry address)))
     (:lng (:location (:geometry address)))))
  (street-name [_ address]
    (:long-name (extract address "route")))
  (street-number [_ address]
    (:long-name (extract address "street_number")))
  (postal-code [_ address]
    (:long-name (extract address "postal_code")))
  (region [_ address]
    (:long-name (extract address "administrative_area_level_1"))))

(extend-type Geocoder
  IGeocodeAddress
  (geocode-address [provider address options]
    (-> (make-request provider options)
        (assoc-in [:query-params :address] address)
        (fetch provider))))

(extend-type Geocoder
  IGeocodeLocation
  (geocode-location [provider location options]
    (-> (make-request provider options)
        (assoc-in [:query-params :latlng] (to-str location))
        (fetch provider))))

(alter-var-root #'*geocoder* (constantly (make-geocoder {})))
