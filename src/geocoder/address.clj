(ns geocoder.address)

(defprotocol IAddress
  (city [address]
    "Returns the city of the address.")
  (country [address]
    "Returns the country of the address.")
  (location [address]
    "Returns the location of the address.")
  (street-name [address]
    "Returns the street name of the address.")
  (street-number [address]
    "Returns the street number of the address.")
  (postal-code [address]
    "Returns the postal code of the address.")
  (region [address]
    "Returns the region of the address."))

(defrecord Address [country city location street-name street-number postal-code region]
  IAddress
  (city [address]
    (:city address))
  (country [address]
    (:country address))
  (location [address]
    (:location address))
  (street-name [address]
    (:street-name address))
  (street-number [address]
    (:street-number address))
  (postal-code [address]
    (:postal-code address))
  (region [address]
    (:region address)))

(defn make-address
  "Make a new address."
  [& {:keys [country city location street-name street-number postal-code region]}]
  (Address. country city location street-name street-number postal-code region))

(defn to-address
  "Convert result into an address."
  [result]
  (make-address
   :city (city result)
   :country (country result)
   :location (location result)
   :street-name (street-name result)
   :street-number (street-number result)
   :postal-code (postal-code result)
   :region (region result)))
