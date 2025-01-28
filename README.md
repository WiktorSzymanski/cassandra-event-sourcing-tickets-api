# Cassandra-event-sourcing-tickets-api

## Wymagania:

Zainstalowany docker

Zainstalowany docker-compose



## Uruchamianie:

docker compose -f "cassandra-node-compose.yaml" up -d

docker cp ./db-schema.cql rbd-cassandra:/etc/db-schema.cql

docker exec -it rbd-cassandra cqlsh -f /etc/db-schema.cql


