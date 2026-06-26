--For creation of SQLite database used in the Glassfish WAR demonstration program.

-- 1. Create Projects Table
CREATE TABLE IF NOT EXISTS projects (
    project_id INTEGER PRIMARY KEY AUTOINCREMENT,
    project_name TEXT NOT NULL,
    status TEXT DEFAULT 'Planning',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Create Users Table
CREATE TABLE IF NOT EXISTS users (
    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    age INTEGER CHECK(age >= 18),
    role TEXT DEFAULT 'developer',
    project_id INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE SET NULL
);

-- 3. Insert Data into Projects
INSERT INTO projects (project_name, status) 
VALUES 
    ('Alpha Core App', 'In Progress'),
    ('Beta Data Pipeline', 'Planning');

-- 4. Insert Data into Users
-- Note: Omitting user_id and created_at allows SQLite to auto-generate them.
INSERT INTO users (first_name, last_name, email, age, role, project_id) 
VALUES 
    ('Alice', 'Smith', 'alice.smith@example.com', 28, 'lead', 1),
    ('Bob', 'Jones', 'bob.jones@example.com', 32, 'backend', 1),
    ('Charlie', 'Brown', 'charlie.brown@example.com', 24, 'frontend', 2),
    ('Diana', 'Prince', 'diana.prince@example.com', 30, 'devops', 2);

-- 5. Create the cars table
CREATE TABLE cars (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    year INTEGER,
    make TEXT,
    model TEXT,
    color TEXT,
    style TEXT
);

-- 6. Insert the car rows
INSERT INTO cars (year, make, model, color, style) VALUES
(1980, 'Dodge', 'Omni', 'Red', 'Four door hatchback'),
(1986, 'Plymouth', 'Sundance', 'Silver', 'Two door coupe'),
(1994, 'Ford', 'Escort GT', 'Black', 'Two door hatchback'),
(2000, 'Oldsmobile', 'Alero', 'Green', 'Four door sedan'),
(2006, 'Honda', 'Civic', 'Black', 'Two door coupe'),
(2011, 'Hyundai', 'Elantra', 'Tan', 'Four door sedan'),
(2012, 'Hyundai', 'Veloster', 'Red', 'Three door hatchback'),
(2015, 'Hyundai', 'Sonata', 'Unknown', 'Four door sedan'),
(2019, 'Volvo', 'S60', 'Red', 'Four door sedan'),
(2026, 'Honda', 'Civic Sport Touring', 'Boost Blue', 'Four door hatchback');

-- 7. Create the people table
CREATE TABLE people (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT,
    email TEXT,
    comment TEXT
);
