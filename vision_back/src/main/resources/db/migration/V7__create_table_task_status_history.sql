CREATE TABLE task_status_history (
    status_history_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    last_status INT NOT NULL,
    actual_status INT NOT NULL,
    change_date TIMESTAMP NOT NULL,
    FOREIGN KEY (task_code) REFERENCES task(task_code)
);
