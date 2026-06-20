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
