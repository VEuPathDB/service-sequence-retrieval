#!/bin/bash

docker compose -p seqret_local -f docker-compose.yml -f docker-compose.dev.yml down -v
docker compose -p seqret_local -f docker-compose.yml -f docker-compose.dev.yml up
