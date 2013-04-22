# GEOCODE-CLJ [![Build Status](https://travis-ci.org/r0man/geocoder-clj.png)](https://travis-ci.org/r0man/geocoder-clj)

A Clojure library for various geocoder services.

## Installation

Via Clojars: http://clojars.org/geocoder-clj

## Usage

### Google

    (require '[geocoder.google :as google])

    (google/geocode-address "Senefelderstraße 24, 10437 Berlin")

    ;=> ({:types ("street_address"),
    ;=>   :geometry
    ;=>   {:viewport
    ;=>    {:southwest {:lng 13.4216410197085, :lat 52.5412310197085},
    ;=>     :northeast {:lng 13.4243389802915, :lat 52.5439289802915}},
    ;=>    :location-type "ROOFTOP",
    ;=>    :location {:lng 13.42299, :lat 52.54258}},
    ;=>   :formatted-address "Senefelderstraße 24, 10437 Berlin, Germany",
    ;=>   :address-components
    ;=>   ({:types ("street_number"), :short-name "24", :long-name "24"}
    ;=>    {:types ("route"),
    ;=>     :short-name "Senefelderstraße",
    ;=>     :long-name "Senefelderstraße"}
    ;=>    {:types ("sublocality" "political"),
    ;=>     :short-name "Prenzlauer Berg",
    ;=>     :long-name "Prenzlauer Berg"}
    ;=>    {:types ("sublocality" "political"),
    ;=>     :short-name "Pankow",
    ;=>     :long-name "Pankow"}
    ;=>    {:types ("locality" "political"),
    ;=>     :short-name "Berlin",
    ;=>     :long-name "Berlin"}
    ;=>    {:types ("administrative_area_level_1" "political"),
    ;=>     :short-name "Berlin",
    ;=>     :long-name "Berlin"}
    ;=>    {:types ("country" "political"),
    ;=>     :short-name "DE",
    ;=>     :long-name "Germany"}
    ;=>    {:types ("postal_code"), :short-name "10437", :long-name "10437"})})

## License

Copyright (C) 2013 Roman Scherer

Distributed under the Eclipse Public License, the same as Clojure.
