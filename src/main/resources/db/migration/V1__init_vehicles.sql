-- Vehicles service schema & table
create schema if not exists ifsw_schema;

-- Insurance service: persons
create table if not exists ifsw_schema.persons (
    id bigserial primary key,
    personal_id varchar(50) not null unique, -- used by external clients / cross-service linkage
    name varchar(100),
    email varchar(100),
    phone varchar(50),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index if not exists idx_persons_personal_id on ifsw_schema.persons (personal_id);

-- Vehicles service: vehicles table
create table if not exists ifsw_schema.vehicles (
    id bigserial primary key,
    registration_number varchar(50) not null unique,
    vin varchar(50),
    owner_personal_id varchar(50), -- links to person's personal_id in insurance service (no FK)
    make varchar(50),
    model varchar(50),
    year int,
    color varchar(30),
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index if not exists idx_vehicles_registration on ifsw_schema.vehicles (registration_number);