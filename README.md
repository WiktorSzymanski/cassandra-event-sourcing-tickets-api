# Cassandra-event-sourcing-tickets-api

## Opis

Projekt powstał na przedmiot Systemy Rozproszone Dużej Skali na Politechnice Poznańskiej w semestrze zimowym 2025.
Za jego stworzenie odpowiedzialni byli [Wiktor Szymański](https://github.com/WiktorSzymanski) oraz 
[Jan Metzler](https://github.com/JanMetz). Prowadzącym przedmiotu był [doktor inżynier Tadeusz Kobus](https://pl.linkedin.com/in/tkobus).

Jego celem było stworzenie aplikacji, która korzystałaby z rozproszonego systemu baz danych Cassandra.

Projekt składa się z dwóch części - pierwsza, napisana w SpringBoot, to część serwerowa, obsługująca requesty klientów 
i komunikująca się z bazą danych. Znajduje się ona w katalogu main. Druga część to prosty klient napisany z wykorzystaniem
frameworku Retrofit, używany do testowania poprawności implementacji oraz testów obciążeniowych. 
Znajduje się ona w katalogu test.

Projekt bazy danych zakłada architekturę [Event Store](https://en.wikipedia.org/wiki/Event_store).


## Wymagania

Zainstalowany docker [instrukcje tutaj](https://docs.docker.com/engine/install/)

Zainstalowane narzędzie docker-compose [instrukcje tutaj](https://docs.docker.com/compose/install/)



## Uruchamianie poda z bazą danych
(pliki z konfiguracją znajdują się src/main/resources)

```
docker compose -f "cassandra-node-compose.yaml" up -d

docker cp ./db-schema.cql rbd-cassandra:/etc/db-schema.cql

docker exec -it rbd-cassandra cqlsh -f /etc/db-schema.cql
```




