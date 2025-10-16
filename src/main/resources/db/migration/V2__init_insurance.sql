-- Pets (for pet insurance)
create table if not exists ifsw_schema.pets (
    id bigserial primary key,
    person_id bigint not null references ifsw_schema.persons (id) on delete cascade,
    name varchar(100) not null,
    species varchar(50),
    breed varchar(100),
    birth_date date,
    microchip_number varchar(100) unique,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);
create index if not exists idx_pets_person on ifsw_schema.pets (person_id);

-- Insurance products (catalog)
create table if not exists ifsw_schema.insurance_products (
    id bigserial primary key,
    code varchar(50) not null unique,
    name varchar(100) not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

create table if not exists ifsw_schema.insurance_product_prices (
    id bigserial primary key,
    product_code varchar(50) not null references ifsw_schema.insurance_products (code) on delete cascade,
    monthly_price numeric(10,2) not null,
    valid_from timestamptz not null default now(),
    valid_to timestamptz,
    active boolean not null default false,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

-- Ensure at most one active price per product (partial unique index)
create unique index if not exists ux_insurance_product_prices_active
    on ifsw_schema.insurance_product_prices (product_code)
    where active;

create index if not exists idx_insurance_products_code on ifsw_schema.insurance_products (code);

-- Policies (generic policy metadata; product snapshot and price snapshot)
create table if not exists ifsw_schema.policies (
    id bigserial primary key,
    person_id bigint not null references ifsw_schema.persons (id) on delete cascade,
    product_id bigint not null references ifsw_schema.insurance_products (id),
    product_code varchar(50) not null, -- snapshot of product code at issuance (redundant but useful)
    start_date date not null,
    end_date date,
    status varchar(20) not null default 'ACTIVE',
    monthly_price numeric(10,2) not null, -- snapshot of product price at issuance
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_insurance_policies on ifsw_schema.policies (person_id, product_id);

-- Add unique constraint to prevent duplicate policies for the same person, product and start date
ALTER TABLE ifsw_schema.policies
    ADD CONSTRAINT uq_policy_person_product_start_date
        UNIQUE (person_id, product_id, start_date);

-- Policy details: product-specific information (one-to-one with policies)
create table if not exists ifsw_schema.policy_details (
    id bigserial primary key,
    policy_id bigint not null unique references ifsw_schema.policies (id) on delete cascade,
    product_code varchar(50) not null, -- snapshot of product code at issuance (duplicate of policies.product_code)
    vehicle_registration varchar(50),   -- used to call Vehicles service for CAR
    pet_id bigint,                      -- FK to local pets table for PET
    health_info jsonb,                  -- free-form health-specific details for health product
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
    );

alter table ifsw_schema.policy_details
    add constraint fk_policy_details_pet foreign key (pet_id) references ifsw_schema.pets (id) on delete restrict;

-- Constraints enforcing required detail fields by product_code
alter table ifsw_schema.policy_details
    add constraint chk_policy_details_product_code_not_empty check (length(trim(product_code)) > 0),
    add constraint chk_policy_details_car_vehicle_required check (
        product_code <> 'CAR' OR (vehicle_registration IS NOT NULL AND length(trim(vehicle_registration)) > 0)
    ),
    add constraint chk_policy_details_pet_required check (
        product_code <> 'PET' OR (pet_id IS NOT NULL)
    );

create index if not exists idx_policy_details_policy_id on ifsw_schema.policy_details (policy_id);
create index if not exists idx_policy_details_vehicle_registration on ifsw_schema.policy_details (vehicle_registration);
create index if not exists idx_policy_details_pet_id on ifsw_schema.policy_details (pet_id);
create index if not exists idx_policy_details_product_code on ifsw_schema.policy_details (product_code);

-- Seed insurance_products with required products and prices
insert into ifsw_schema.insurance_products (code, name)
values
    ('PET', 'Pet insurance'),
    ('HEALTH', 'Personal health insurance'),
    ('CAR', 'Car insurance')
    on conflict (code) do nothing;

insert into ifsw_schema.insurance_product_prices (product_code, monthly_price, valid_from, valid_to, active)
values
    ('PET', 10.00, '2025-01-01', '2026-01-01', true),
    ('HEALTH', 20.00, '2025-01-01', '2026-01-01', true),
    ('CAR', 30.00, '2025-01-01', '2026-01-01', true);
