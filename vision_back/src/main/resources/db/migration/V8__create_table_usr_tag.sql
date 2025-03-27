CREATE TABLE usr_tag (
    usr_tag_id SERIAL PRIMARY KEY,
    task_id INT NOT NULL,
    tag_id INT NOT NULL,
    project_id INT NOT NULL,
    quant INT,
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (tag_id) REFERENCES tag(tag_id),
    FOREIGN KEY (task_id) REFERENCES task(task_id)
);
