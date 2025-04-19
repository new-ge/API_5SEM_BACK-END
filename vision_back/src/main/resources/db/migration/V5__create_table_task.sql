CREATE TABLE task (
    task_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL UNIQUE,
    task_description VARCHAR(255)
);
