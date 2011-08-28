(ns geocoder.provider)

(defprotocol IProvider
  (geocode-request [provider address options]
    "Returns the geocode request.")
  (results [provider response]
    "Returns the results in the response.")
  (reverse-geocode-request [provider location options]
    "Returns the reverse geocode request."))
