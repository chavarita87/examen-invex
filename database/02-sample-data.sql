-- =====================================================================
-- examen-invex: sample data (MySQL 8)
-- Inserts ten employees to try the API. Run after 01-schema.sql.
-- Ages match the birth dates as of November 2026.
--
--   mysql -u root -p < database/02-sample-data.sql
-- =====================================================================

USE examen_invex;

INSERT INTO employees
    (first_name, middle_name, paternal_last_name, maternal_last_name, age, gender, birth_date, position, active)
VALUES
    ('Juan',      'Carlos',  'Perez',     'Lopez',     31, 'MALE',   '1995-03-15', 'Developer',          b'1'),
    ('Maria',     NULL,      'Gonzalez',  'Juarez',    28, 'FEMALE', '1998-07-02', 'Analyst',            b'1'),
    ('José',      'Luis',    'Hernández', 'Martínez',  45, 'MALE',   '1981-05-20', 'Engineering Manager', b'1'),
    ('Ana',       'Sofía',   'Ramírez',   'Torres',    34, 'FEMALE', '1992-09-10', 'QA Engineer',        b'1'),
    ('Luis',      NULL,      'García',    'Ruiz',      26, 'MALE',   '2000-01-28', 'Junior Developer',   b'1'),
    ('Fernanda',  NULL,      'Morales',   NULL,        39, 'FEMALE', '1987-11-03', 'Product Owner',      b'1'),
    ('Ricardo',   'Alberto', 'Díaz',      'Castro',    52, 'MALE',   '1974-02-14', 'Architect',          b'1'),
    ('Patricia',  NULL,      'Vargas',    'Mendoza',   30, 'FEMALE', '1996-06-30', 'Scrum Master',       b'0'),
    ('Alex',      NULL,      'Romero',    'Silva',     24, 'OTHER',  '2002-08-12', 'Data Analyst',       b'1'),
    ('Daniela',   'María',   'Ortiz',     'Navarro',   41, 'FEMALE', '1985-04-25', 'DevOps Engineer',    b'1');
