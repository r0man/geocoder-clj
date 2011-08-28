(ns geocoder.provider)

(defprotocol IProvider
  (geocode [provider address options]
    "Geocode the address.")
  (results [provider response]
    "Returns the results in the response.")
  (reverse-geocode [provider location options]
    "Reverse geocode the location."))
