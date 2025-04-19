CREATE TABLE usr (
    usr_id SERIAL PRIMARY KEY,
    usr_code INT NOT NULL UNIQUE,  -- Identificador do usuário no sistema origem
    usr_name VARCHAR(100) NOT NULL,
    usr_role VARCHAR(100)[] NOT NULL,  -- Controle de acesso
    usr_email VARCHAR(100) NOT NULL
);