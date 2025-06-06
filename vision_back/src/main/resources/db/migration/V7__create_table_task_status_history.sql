CREATE TABLE task_status_history (
    status_history_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    project_code INT NOT NULL,
    milestone_code INT NOT NULL,
    usr_code INT NOT NULL,
    last_status INT NOT NULL,
    actual_status INT NOT NULL,
    change_date TIMESTAMP NOT NULL,
    FOREIGN KEY (task_code) REFERENCES task(task_code),
    FOREIGN KEY (project_code) REFERENCES project(project_code),    
    FOREIGN KEY (milestone_code) REFERENCES milestone(milestone_code),    
    FOREIGN KEY (usr_code) REFERENCES usr(usr_code)
);
ALTER TABLE task_status_history ADD CONSTRAINT unique_task_status_history_once UNIQUE (usr_code, milestone_code, project_code, task_code, last_status, actual_status, change_date);
