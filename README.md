# geocoder-clj
  [![Build Status](https://travis-ci.org/r0man/geocoder-clj.png)](https://travis-ci.org/r0man/geocoder-clj)
  [![Dependencies Status](http://jarkeeper.com/r0man/geocoder-clj/status.png)](http://jarkeeper.com/r0man/geocoder-clj)
  [![Gittip](http://img.shields.io/gittip/r0man.svg)](https://www.gittip.com/r0man)

A Clojure library for various geocoder services.

## Installation

Via Clojars: http://clojars.org/geocoder-clj

[![Current Version](https://clojars.org/geocoder-clj/latest-version.svg)](https://clojars.org/geocoder-clj)

## Usage

### Bing

``` clj
(require '[geocoder.bing :as bing])
(bing/geocode-address "Senefelderstraße 24, 10437 Berlin")
(bing/geocode-location "52.54258,13.42299")
```

### Geonames

``` clj
(require '[geocoder.geonames :as geonames])
(geonames/geocode-address "Senefelderstraße 24, 10437 Berlin")
(geonames/geocode-location "52.54258,13.42299")
```

### Google

``` clj
(require '[geocoder.google :as google])
(google/geocode-address "Senefelderstraße 24, 10437 Berlin")
(google/geocode-location "52.54258,13.42299")
```

### Maxmind GeoIP

``` clj
(require '[geocoder.maxmind :as maxmind])
(def db (maxmind/make-db "/usr/share/GeoIP/GeoIP.dat"))
(maxmind/geocode-ip-address db "92.229.192.11")
```

### Maxmind GeoIP2

``` clj
(require '[geocoder.maxmind2 :as maxmind2])
(def db (maxmind2/make-db "/usr/share/GeoIP/GeoLite2-City.mmdb"))
(maxmind2/geocode-ip-address db "92.229.192.11")
```

## License

Copyright (C) 2013-2014 r0man

Distributed under the Eclipse Public License, the same as Clojure.
