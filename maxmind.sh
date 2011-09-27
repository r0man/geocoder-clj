#!/usr/bin/env bash
mkdir -p ~/.maxmind
wget -c http://geolite.maxmind.com/download/geoip/database/GeoLiteCity.dat.gz -O ~/.maxmind/GeoLiteCity.dat.gz
gunzip -f ~/.maxmind/GeoLiteCity.dat.gz
