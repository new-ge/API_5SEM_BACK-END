CREATE TABLE usr_task (
    usr_task_id SERIAL PRIMARY KEY,
    task_id INT NOT NULL,
    project_id INT NOT NULL,
    usr_id INT NOT NULL,
    period_id INT NOT NULL,
    stats_id INT NOT NULL,
    start_date TIMESTAMP NOT NULL,  -- Data de início do card
    end_date TIMESTAMP,  -- Data de finalização do card (pode ser NULL até ser concluído)
    quant INT,
    rework INT,
    average_time INT GENERATED ALWAYS AS (EXTRACT(EPOCH FROM (end_date - start_date)) / 3600) STORED, -- Cálculo automático do tempo médio (em horas)
    FOREIGN KEY (project_id) REFERENCES project(project_id),
    FOREIGN KEY (usr_id) REFERENCES usr(usr_id),
    FOREIGN KEY (period_id) REFERENCES period(period_id),
    FOREIGN KEY (stats_id) REFERENCES stats(stats_id),
    FOREIGN KEY (task_id) REFERENCES task(task_id)
);
