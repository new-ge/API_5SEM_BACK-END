CREATE TABLE milestone (
    milestone_id SERIAL PRIMARY KEY,
    milestone_code INT NOT NULL UNIQUE,
    milestone_name VARCHAR(250) NOT NULL,
    estimated_start DATE NOT NULL,
    estimated_end DATE NOT NULL,
    project_code INTEGER NOT NULL,
    FOREIGN KEY (project_code) REFERENCES project(project_code)
);