(ns geocoder.google
  (:use [clojure.string :only (lower-case)]
        geocoder.address
        geocoder.helper
        geocoder.location
        geocoder.provider))

(def *endpoint*
  "http://maps.google.com/maps/api/geocode/json")

(defn default-request []
  {:method :get :url *endpoint* :query-params {:sensor false :language "en"}})

(defn- extract
  "Extract the component from result."
  [result component]
  (first (filter #(contains? (set (:types %)) component) (:address-components result))))

(defrecord Result []
  IAddress
  (city [result]
    (:long-name (extract result "locality")))
  (country [result]
    {:name (:long-name (extract result "country"))
     :iso-3166-1-alpha-2 (lower-case (:short-name (extract result "country")))})
  (location [result]
    (make-location
     (:lat (:location (:geometry result)))
     (:lng (:location (:geometry result)))))
  (street-name [result]
    (:long-name (extract result "route")))
  (street-number [result]
    (:long-name (extract result "street_number")))
  (postal-code [result]
    (:long-name (extract result "postal_code")))
  (region [result]
    (:long-name (extract result "administrative_area_level_1"))))

(defrecord Provider []
  IProvider
  (geocode [provider address options]
    (->> (assoc-in (default-request) [:query-params :address] address)
         json-request
         ((partial results provider))))
  (results [provider response]
    (map #(merge (Result.) %) (:results response)))
  (reverse-geocode [provider location options]
    (->> (assoc-in (default-request) [:query-params :latlng] (format-location location))
         json-request
         ((partial results provider)))))
