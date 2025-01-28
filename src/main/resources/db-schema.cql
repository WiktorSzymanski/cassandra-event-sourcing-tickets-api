CREATE KEYSPACE tickets_api WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 2};

USE tickets_api;

CREATE TABLE arena_event_store (
    arena_id UUID,
    timestamp text,
    eventtype text,
    eventdata text,
    PRIMARY KEY (arena_id, timestamp)
);

CREATE TABLE concerts (
    id text PRIMARY KEY,
    name text,
    arena_id UUID
);

CREATE TABLE snapshot (
    snapshot_id UUID PRIMARY KEY,
    seatsdata text
);