#!/usr/bin/env bash
mkdir -p ~/.maxmind
cd ~/.maxmind
wget -c http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.tar.gz -O GeoLite2-City.tar.gz
tar xvfz GeoLite2-City.tar.gz
mv GeoLite2-City_*/GeoLite2-City.mmdb .
