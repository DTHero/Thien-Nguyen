#!/bin/sh
echo "##########Start docker##########"
docker-compose -f docker-compose.local.yml up -d --build --force-recreate
