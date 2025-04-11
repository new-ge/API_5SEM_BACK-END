CREATE TABLE task (
    task_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    task_description VARCHAR(255) NOT NULL
);
