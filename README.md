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

## Architektura bazy danych

Główną tabelą jest ```arena_event_store```, która pełni rolę event store'a. Jej kluczem są id koncertu oraz timestamp 
wydarzenia.

Dane o koncertach przechowywane są w tabeli ```concerts```, której kluczem głównym jest id koncertu.

Dodatkowo w bazie występuje tabela ```snapshot```, która przechowuje podsumowania eventów z tabeli event_store w celu
zwiększenia efektywności działania systemu.  


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

## Argumenty do uruchomienia programu do testów
W kolejności:

Adres, na którym pracuje serwer

Ilość koncertów, które chcemy utworzyć

Ilość rezerwacji, które chcemy zasymulować

Ilość anulacji rezerwacji, które chcemy zasymulować 

Czy na konsolę mają być wypisywane wszystkie czasy przetwarzania czy nie (podsumowanie i statystyki wyświetlane są zawsze)

## Przykładowy wynik działania programu do testów

```
Running with configuration: 
 apiAddr = http://localhost:8080 
 number of create jobs = 1 
 number of seat reservations = 100 
 number of seat releases = 30
 should spam the console = false
Get my seats response times [ms] avg 7.038961038961039, min 2, max 55, Q1 4, Q2 6, Q3 8
Get free seats response times [ms] avg 8.691964285714286, min 2, max 200, Q1 4, Q2 6, Q3 8
Get concert response times [ms] avg 2.068702290076336, min 1, max 21, Q1 1, Q2 2, Q3 2
Create concert response times [ms] avg 707.0, min 707, max 707, Q1 707, Q2 707, Q3 707
Release a seat response times [ms] avg 11.125, min 5, max 71, Q1 7, Q2 8, Q3 10
Reserve a seat response times [ms] avg 8.47, min 5, max 25, Q1 6, Q2 8, Q3 10
Whole operation took 22.920915257s

Process finished with exit code 0
```




