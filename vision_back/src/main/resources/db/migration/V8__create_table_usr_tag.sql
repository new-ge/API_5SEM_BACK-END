CREATE TABLE usr_tag (
    usr_tag_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    project_code INT NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    quant INT,
    FOREIGN KEY (project_code) REFERENCES project(project_code),
    FOREIGN KEY (task_code) REFERENCES task(task_code)
);
