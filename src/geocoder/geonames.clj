(ns geocoder.geonames
  (:require [clojure.string :as str]
            [geocoder.util :as util]))

(defn city
  [address]
  (:place-name address))

(defn country
  [address]
  {:iso-3166-1-alpha-2 (str/lower-case (:country-code address))})

(defn location [address]
  (select-keys address [:lat :lng]))

(defn street-name [address]
  (:address-line (:address address)))

(defn postal-code [address]
  (:postal-code address))

(defn region [address]
  (:admin-name1 address))

(defn- request
  "Make a geocode request map."
  [geocoder & [opts]]
  {:request-method :get
   :query-params
   (assoc opts :username (:api-key geocoder))})

(defn- fetch
  "Fetch and decode the Geonames geocode response."
  [request]
  (-> (util/fetch-json request)
      :postal-codes))

(defn geocode-address [geocoder address & [opts]]
  (-> (request geocoder opts)
      (assoc :url "http://api.geonames.org/postalCodeSearchJSON")
      (assoc-in [:query-params :placename] address)
      (fetch)))

(defn geocode-location [geocoder location & [opts]]
  (-> (request geocoder opts)
      (assoc :url (str "http://api.geonames.org/findNearbyPostalCodesJSON"))
      (assoc-in [:query-params :lat] (:lat location))
      (assoc-in [:query-params :lng] (:lng location))
      (fetch)))

(defn geocoder
  "Returns a new Geonames geocoder."
  [& [{:keys [api-key]}]]
  {:api-key api-key})
