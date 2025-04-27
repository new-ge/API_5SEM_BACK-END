CREATE TABLE role (
    role_id SERIAL PRIMARY KEY,
    role_code INT NOT NULL UNIQUE,
    role_name VARCHAR(255) NOT NULL,
    project_code INT NOT NULL,
    FOREIGN KEY (project_code) REFERENCES project(project_code)
);
