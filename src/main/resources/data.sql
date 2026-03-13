-- ============================================================
-- Initial seed data (migrated from DB2 insertion-1 & insertion-2)
-- Runs automatically on H2 startup (spring.sql.init.mode=always)
-- ============================================================

-- Departments (insertion-1)
INSERT INTO department (dept_id, name) VALUES (1,  'CEO')              ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (2,  'Commander')        ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (3,  'Copilote')         ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (4,  'Flight Attendant') ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (5,  'Human Resources')  ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (6,  'IT Support')       ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (7,  'Sales')            ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (8,  'Legal')            ON CONFLICT DO NOTHING;
INSERT INTO department (dept_id, name) VALUES (9,  'Schedule')         ON CONFLICT DO NOTHING;

-- Airports (insertion-2)
INSERT INTO airport (airport_id, name, address, city, country, zipcode)
    VALUES ('CDG', 'Charles de Gaulle Airport', 'BP 20101', 'Roissy-en-France', 'France', '95711')
    ON CONFLICT DO NOTHING;
INSERT INTO airport (airport_id, name, address, city, country, zipcode)
    VALUES ('BOD', 'Bordeaux–Mérignac Airport', 'Avenue des Pèlerins', 'Mérignac', 'France', '33700')
    ON CONFLICT DO NOTHING;
INSERT INTO airport (airport_id, name, address, city, country, zipcode)
    VALUES ('FCO', 'Leonardo da Vinci International Airport', 'Via dell''Aeroporto di Fiumicino', 'Fiumicino', 'Italy', '00054')
    ON CONFLICT DO NOTHING;
INSERT INTO airport (airport_id, name, address, city, country, zipcode)
    VALUES ('LIS', 'Lisbon Humberto Delgado Airport', 'Alameda das Comunidades Portuguesas', 'Lisbon', 'Portugal', '1749-078')
    ON CONFLICT DO NOTHING;

-- Airplanes (insertion-2)
INSERT INTO airplane (airplane_id, type, num_seats, total_fuel)
    VALUES ('BOEING01', '737-200', 130, 26000) ON CONFLICT DO NOTHING;
INSERT INTO airplane (airplane_id, type, num_seats, total_fuel)
    VALUES ('AIRBUS01', 'A320',    150, 24000)  ON CONFLICT DO NOTHING;
INSERT INTO airplane (airplane_id, type, num_seats, total_fuel)
    VALUES ('AIRBUS02', 'A340',    260, 75000)  ON CONFLICT DO NOTHING;
