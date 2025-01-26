CREATE KEYSPACE tickets_api WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 2};

USE tickets_api;

CREATE TABLE arena_event_store (
    arena_id UUID,
    time_stamp text,
    event_type text,
    event_data text,
    PRIMARY KEY (arena_id, time_stamp)
);

CREATE TABLE concerts (
    id text PRIMARY KEY,
    name text,
    arena_id UUID
);
