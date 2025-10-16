-- Insert sample persons
INSERT INTO ifsw_schema.persons (personal_id, name, email, phone)
VALUES 
    ('ID12345678', 'John Smith', 'john.smith@example.com', '+1 (555) 123-4567'),
    ('ID23456789', 'Sarah Johnson', 'sarah.j@example.com', '+1 (555) 234-5678'),
    ('ID34567890', 'Michael Brown', 'michael.b@example.com', '+1 (555) 345-6789'),
    ('ID45678901', 'Emily Davis', 'emily.d@example.com', '+1 (555) 456-7890'),
    ('ID56789012', 'Robert Wilson', 'robert.w@example.com', '+1 (555) 567-8901')
ON CONFLICT (personal_id) DO NOTHING;

-- Insert sample vehicles
INSERT INTO ifsw_schema.vehicles (registration_number, vin, owner_personal_id, make, model, year, color)
VALUES
    ('ABC123', '1HGCM82633A123456', 'ID12345678', 'Toyota', 'Camry', 2020, 'Silver'),
    ('XYZ789', '5XYZU3LBXEG123456', 'ID12345678', 'Honda', 'CR-V', 2021, 'Black'),
    ('DEF456', '2HGFB6F52EH234567', 'ID23456789', 'Ford', 'Escape', 2022, 'Blue'),
    ('GHI789', '1N4AL3AP3EC345678', 'ID34567890', 'Nissan', 'Altima', 2019, 'White'),
    ('JKL012', 'WAUFFAFL3CA456789', 'ID45678901', 'Audi', 'A4', 2023, 'Gray'),
    ('MNO345', '5J6RM4H75FL567890', 'ID56789012', 'Honda', 'Pilot', 2021, 'Red')
ON CONFLICT (registration_number) DO NOTHING;

-- Insert sample pets
INSERT INTO ifsw_schema.pets (person_id, name, species, breed, birth_date, microchip_number)
SELECT
    p.id as person_id,
    pet_data.name,
    pet_data.species,
    pet_data.breed,
    pet_data.birth_date::date,
    pet_data.microchip_number
FROM (VALUES
    ('ID12345678', 'Max', 'Dog', 'Golden Retriever', '2020-05-15', 'A1234567890'),
    ('ID23456789', 'Luna', 'Cat', 'Siamese', '2021-02-20', 'B2345678901'),
    ('ID34567890', 'Buddy', 'Dog', 'Labrador', '2019-11-10', 'C3456789012')
) AS pet_data (personal_id, name, species, breed, birth_date, microchip_number)
JOIN ifsw_schema.persons p ON p.personal_id = pet_data.personal_id
ON CONFLICT (microchip_number) DO NOTHING;

-- Insert sample policies
WITH product_ids AS (
    SELECT code, id FROM ifsw_schema.insurance_products
)
INSERT INTO ifsw_schema.policies (
    person_id, 
    product_id, 
    product_code, 
    start_date, 
    end_date, 
    status, 
    monthly_price
)
SELECT 
    p.id as person_id,
    pi.id as product_id,
    pi.code as product_code,
    policy_data.start_date,
    policy_data.end_date,
    'ACTIVE',
    policy_data.monthly_price
FROM (VALUES 
    ('ID12345678', 'CAR', '2025-01-01'::date, '2025-12-31'::date, 30.00),
    ('ID23456789', 'CAR', '2025-03-15'::date, '2026-03-14'::date, 30.00),
    ('ID12345678', 'PET', '2025-01-15'::date, '2026-01-14'::date, 10.00),
    ('ID23456789', 'PET', '2025-02-01'::date, '2026-01-31'::date, 10.00),
    ('ID12345678', 'HEALTH', '2025-01-01'::date, '2025-12-31'::date, 20.00),
    ('ID34567890', 'HEALTH', '2025-02-15'::date, '2026-02-14'::date, 20.00)
) AS policy_data (personal_id, product_code, start_date, end_date, monthly_price)
JOIN ifsw_schema.persons p ON p.personal_id = policy_data.personal_id
JOIN product_ids pi ON pi.code = policy_data.product_code
ON CONFLICT (person_id, product_id, start_date) DO NOTHING;

-- Insert policy details for CAR policies
INSERT INTO ifsw_schema.policy_details (
    policy_id,
    product_code,
    vehicle_registration
)
SELECT 
    p.id as policy_id,
    p.product_code,
    v.registration_number
FROM ifsw_schema.policies p
JOIN ifsw_schema.persons per ON p.person_id = per.id
JOIN ifsw_schema.vehicles v ON v.owner_personal_id = per.personal_id
WHERE p.product_code = 'CAR'
ON CONFLICT (policy_id) DO NOTHING;

-- Insert policy details for PET policies
INSERT INTO ifsw_schema.policy_details (
    policy_id,
    product_code,
    pet_id
)
SELECT 
    p.id as policy_id,
    p.product_code,
    pet.id as pet_id
FROM ifsw_schema.policies p
JOIN ifsw_schema.persons per ON p.person_id = per.id
JOIN ifsw_schema.pets pet ON pet.person_id = per.id
WHERE p.product_code = 'PET'
ON CONFLICT (policy_id) DO NOTHING;

-- Insert policy details for HEALTH policies
INSERT INTO ifsw_schema.policy_details (
    policy_id,
    product_code,
    health_info
)
SELECT 
    p.id as policy_id,
    p.product_code,
    '{"coverage_type": "comprehensive", "pre_existing_conditions": false}'::jsonb
FROM ifsw_schema.policies p
WHERE p.product_code = 'HEALTH'
ON CONFLICT (policy_id) DO NOTHING;
