(ns geocoder.bing
  (:use geocoder.address
        geocoder.helper
        geocoder.location
        geocoder.provider))

(defn location-request [provider]
  {:method :get
   :url "http://dev.virtualearth.net/REST/v1/Locations"
   :query-params {:key (:api-key provider)}})

(defn point-request [provider]
  {:method :get
   :url "http://dev.virtualearth.net/REST/v1/Locations/point"
   :query-params {:key (:api-key provider)}})

(defrecord Result []
  IAddress
  (city [result]
    (:locality (:address result)))
  (country [result]
    {:name (:country-region (:address result))})
  (location [result]
    (apply make-location (:coordinates (:point result))))
  (street-name [result]
    (:address-line (:address result)))
  (street-number [result]
    nil)
  (postal-code [result]
    (:postal-code (:address result)))
  (region [result]
    (:state result)))

(defrecord Provider [api-key]
  IProvider
  (geocode [provider address options]
    (->> (assoc-in (location-request provider) [:query-params :query] address)
         json-request
         ((partial results provider))))
  (results [provider response]
    (map #(merge (Result.) %) (mapcat :resources (:resource-sets response))))
  (reverse-geocode [provider location options]
    (->> (assoc-in (point-request provider) [:query-params :point] (format-location location))
         json-request
         ((partial results provider)))))

