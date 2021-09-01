create schema "matrikel";

set schema 'matrikel';
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

select * from matrikel.eventstore;

drop table matrikel.eventstore;

delete from matrikel.eventstore;

CREATE TABLE matrikel.eventstore
(
    id            uuid               DEFAULT uuid_generate_v4() primary key,
    actor         varchar(100),
    requestid     varchar(128),
    businesskey   varchar(255),
    aggregatetype varchar(255),
    version       bigint,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    data          text
)
;

create table matrikel.aggregates
(
    id            uuid DEFAULT uuid_generate_v4() primary key,
    aggregatetype varchar(200),
    actor         varchar(100),
    businesskey   varchar(255),
    version       bigint
);

create table matrikel.snapshots
(
    id            uuid               DEFAULT uuid_generate_v4() primary key,
    aggregatetype varchar(200),
    businesskey   varchar(255),
    actor         varchar(100),
    version       bigint,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    data          text
);

select * from matrikel.eventstore;