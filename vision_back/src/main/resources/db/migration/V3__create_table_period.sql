CREATE TABLE period (
    period_id INT PRIMARY KEY AUTO_INCREMENT,
    period_code INT NOT NULL, -- Identificador do período no sistema origem
    period_date INT NOT NULL, -- Exemplo: 20250317 (YYYYMMDD)
    period_month INT NOT NULL,
    period_year INT NOT NULL,
    period_hour INT NOT NULL  -- Apenas a hora (0-23)
);