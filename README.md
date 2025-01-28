# Cassandra-event-sourcing-tickets-api

## Opis

Projekt powstał na przedmiot Systemy Rozproszone Dużej Skali na Politechnice Poznańskiej w semestrze zimowym 2025.
Za jego stworzenie odpowiedzialni byli [Wiktor Szymański](https://github.com/WiktorSzymanski) oraz 
[Jan Metzler](https://github.com/JanMetz).

Jego celem było stworzenie aplikacji, która korzystałaby z rozproszonego systemu baz danych Cassandra.

Projekt składa się z dwóch części - pierwsza, napisana w SpringBoot, to część serwerowa, obsługująca requesty klientów 
i komunikująca się z bazą danych. Znajduje się ona w katalogu main. Druga część to prosty klient napisany z wykorzystaniem
frameworku Retrofit, używany do testowania poprawności implementacji oraz testów obciążeniowych. 
Znajduje się ona w katalogu test.

Projekt bazy danych zakłada architekturę [Event Store](https://en.wikipedia.org/wiki/Event_store).


## Wymagania:

Zainstalowany docker

Zainstalowany docker-compose



## Uruchamianie poda z bazą danych:

```
docker compose -f "cassandra-node-compose.yaml" up -d

docker cp ./db-schema.cql rbd-cassandra:/etc/db-schema.cql

docker exec -it rbd-cassandra cqlsh -f /etc/db-schema.cql
```




