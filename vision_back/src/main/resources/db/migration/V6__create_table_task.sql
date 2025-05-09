CREATE TABLE task (
    task_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL UNIQUE,
    task_description VARCHAR(255),
    milestone_code INTEGER NOT NULL,
    FOREIGN KEY (milestone_code) REFERENCES milestone(milestone_code)
);
