CREATE TABLE usr_tag (
    usr_tag_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    project_code INT NOT NULL,
    usr_code INT NOT NULL,
    milestone_code INT NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    quant INT,
    FOREIGN KEY (project_code) REFERENCES project(project_code),
    FOREIGN KEY (task_code) REFERENCES task(task_code),
    FOREIGN KEY (usr_code) REFERENCES usr(usr_code),
    FOREIGN KEY (milestone_code) REFERENCES milestone(milestone_code)
);
