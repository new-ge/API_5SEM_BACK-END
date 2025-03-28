CREATE TABLE project (
    project_id SERIAL PRIMARY KEY,
    project_code INT NOT NULL,  -- Identificador do projeto no sistema origem
    project_name VARCHAR(250) NOT NULL
);