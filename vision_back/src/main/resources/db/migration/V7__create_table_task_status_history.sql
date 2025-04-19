CREATE TABLE task_status_history (
    status_history_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    last_status INT NOT NULL,
    actual_status INT NOT NULL,
    change_date TIMESTAMP NOT NULL,
    FOREIGN KEY (task_code) REFERENCES task(task_code)
);
ALTER TABLE task_status_history ADD CONSTRAINT unique_task_status_history_once UNIQUE (task_code, last_status, actual_status, change_date);
