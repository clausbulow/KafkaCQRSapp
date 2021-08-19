set schema 'inventory';

CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";
drop table inventory.eventstore;
drop table inventory.aggregates;
drop table inventory.snapshots;

CREATE TABLE inventory.eventstore
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

create table inventory.aggregates
(
    id            uuid DEFAULT uuid_generate_v4() primary key,
    aggregatetype varchar(200),
    actor         varchar(100),
    businesskey   varchar(255),
    version       bigint
);

create table inventory.snapshots
(
    id            uuid               DEFAULT uuid_generate_v4() primary key,
    aggregatetype varchar(200),
    businesskey   varchar(255),
    actor         varchar(100),
    version       bigint,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    data          text
);

delete
from inventory.aggregates;
delete
from inventory.eventstore;
delete
from inventory.snapshots;

select *
from inventory.aggregates;

select *
from inventory.eventstore;

select *
from inventory.snapshots;

select a
from inventory.snapshots a
where a.version =
      (select max(b.version) from inventory.snapshots b where b.businesskey = a.businesskey group by b.businesskey);

delete
from inventory.snapshots;

