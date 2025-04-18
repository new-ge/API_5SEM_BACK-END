CREATE TABLE task_status_history (
    status_history_id SERIAL PRIMARY KEY,
    task_id INT NOT NULL,
    stats_id INT NOT NULL,
    change_date TIMESTAMP NOT NULL,
    FOREIGN KEY (task_id) REFERENCES task(task_id),
    FOREIGN KEY (stats_id) REFERENCES stats(stats_id)
);
