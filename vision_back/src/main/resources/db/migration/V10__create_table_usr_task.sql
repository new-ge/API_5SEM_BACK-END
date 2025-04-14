CREATE TABLE usr_task (
    usr_task_id SERIAL PRIMARY KEY,
    task_code INT NOT NULL,
    project_code INT NOT NULL,
    usr_code INT NOT NULL,
    period_code INT NOT NULL,
    stats_code INT NOT NULL,
    role_code INT NOT NULL,
    start_date TIMESTAMP NOT NULL,  -- Data de início do card
    end_date TIMESTAMP,  -- Data de finalização do card (pode ser NULL até ser concluído)
    quant INT,
    rework INT,
    average_time INT GENERATED ALWAYS AS (EXTRACT(EPOCH FROM (end_date - start_date)) / 3600) STORED, -- Cálculo automático do tempo médio (em horas)
    FOREIGN KEY (project_code) REFERENCES project(project_code),
    FOREIGN KEY (usr_code) REFERENCES usr(usr_code),
    FOREIGN KEY (period_code) REFERENCES period(period_code),
    FOREIGN KEY (stats_code) REFERENCES stats(stats_code),
    FOREIGN KEY (task_code) REFERENCES task(task_code),
    FOREIGN KEY (role_code) REFERENCES role(role_code)
);
