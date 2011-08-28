(ns geocoder.yahoo
  (:use [clojure.string :only (lower-case)]
        geocoder.address
        geocoder.location
        geocoder.provider))

(defn request [provider & options]
  {:method :get
   :url "http://where.yahooapis.com/geocode"
   :query-params {:appid (:api-key provider) :flags "J"}})

(defrecord Result []
  IAddress
  (city [result]
    (:city result))
  (country [result]
    {:name (:country result)
     :iso-3166-1-alpha-2 (lower-case (:countrycode result))})
  (location [result]
    (make-location
     (:latitude result)
     (:longitude result)))
  (street-name [result]
    (:street result))
  (street-number [result]
    (:house result))
  (postal-code [result]
    (:postal result))
  (region [result]
    (:state result)))

(defrecord Provider [api-key]
  IProvider
  (geocode-request [provider address options]
    (-> (request provider options)
        (assoc-in [:query-params :q] address)))
  (results [provider response]
    (map #(merge (Result.) %) (:results (:result-set response))))
  (reverse-geocode-request [provider location options]
    (-> (request provider options)
        (assoc-in [:query-params :location] (format-location location))
        (assoc-in [:query-params :gflags] "R"))))
