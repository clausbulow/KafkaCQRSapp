set schema 'inventory';

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
drop table inventory.eventstore;
drop table inventory.aggregates;
drop table inventory.snapshots;

CREATE TABLE inventory.eventstore (
  id uuid  DEFAULT uuid_generate_v4() primary key,
  sequencenumber serial,
  actor varchar(100),
  requestid varchar(128),
  businesskey varchar(255),
  version bigint,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  data text
)
;

create table inventory.aggregates (
  id uuid  DEFAULT uuid_generate_v4() primary key,
  type varchar(200),
  businesskey varchar(255),
  version bigint
);

create table inventory.snapshots (
                                      id uuid  DEFAULT uuid_generate_v4() primary key,
                                      type varchar(200),
                                      businesskey varchar(255),
                                      actor varchar(100),
                                      version bigint,
                                      created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                      data text
);

select * from inventory.aggregates;

select * from inventory.eventstore;

select * from inventory.snapshots;
delete from inventory.snapshots;

