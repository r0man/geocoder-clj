(ns geocoder.bing
  (:use geocoder.address
        geocoder.helper
        geocoder.location
        geocoder.provider))

(defn request [provider]
  {:method :get
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
  (geocode-request [provider address options]
    (-> (request provider)
        (assoc :url "http://dev.virtualearth.net/REST/v1/Locations")
        (assoc-in [:query-params :query] address)))
  (results [provider response]
    (map #(merge (Result.) %) (mapcat :resources (:resource-sets response))))
  (reverse-geocode-request [provider location options]
    (-> (request provider)
        (assoc :url "http://dev.virtualearth.net/REST/v1/Locations/point")
        (assoc-in [:query-params :point] (format-location location)))))
