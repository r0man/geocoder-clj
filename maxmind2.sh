#!/usr/bin/env bash
mkdir -p ~/.maxmind
wget -c http://geolite.maxmind.com/download/geoip/database/GeoLite2-City.mmdb.gz -O ~/.maxmind/GeoLite2-City.mmdb.gz
gunzip -f ~/.maxmind/GeoLite2-City.mmdb.gz
