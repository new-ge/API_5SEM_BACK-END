CREATE TABLE stats (
    stats_id SERIAL PRIMARY KEY,
    stats_code INT NOT NULL UNIQUE,
    stats_name VARCHAR(50) NOT NULL
);