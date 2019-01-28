(ns geocoder.bing
  (:require [geocoder.util :refer [fetch-json format-location]]))

(defn city
  "Returns the city of `address`."
  [address]
  (:locality (:address address)))

(defn country
  "Returns the country of `address`."
  [address]
  {:name (:country-region (:address address))})

(defn location
  "Returns the geographical location of `address`."
  [address]
  (let [[y x] (:coordinates (:point address))]
    {:lat y :lng x}))

(defn street-name
  "Returns the street name of `address`."
  [address]
  (:address-line (:address address)))

(defn postal-code
  "Returns the postal code of `address`."
  [address]
  (:postal-code (:address address)))

(defn region
  "Returns the region of `address`."
  [address]
  (:state address))

(defn- request
  "Make a Bing geocode request map."
  [geocoder & [opts]]
  {:request-method :get
   :url "http://dev.virtualearth.net/REST/v1/Locations"
   :query-params
   (assoc opts :key (:api-key geocoder))})

(defn- fetch
  "Fetch and decode the Bing geocode response."
  [request]
  (->> (fetch-json request)
       :resource-sets
       (mapcat :resources)))

(defn geocode-address
  "Geocode an address."
  [geocoder address & {:as opts}]
  (-> (request geocoder opts)
      (assoc-in [:query-params :query] address)
      (fetch)))

(defn geocode-location
  "Geocode a geographical location."
  [geocoder location & {:as opts}]
  (-> (request geocoder opts)
      (update-in [:url] #(format "%s/%s" %1 (format-location location)))
      (fetch)))

(defn geocoder
  "Returns a new Bing geocoder."
  [& [{:keys [api-key]}]]
  {:api-key api-key})
