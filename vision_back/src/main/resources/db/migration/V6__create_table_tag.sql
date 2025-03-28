CREATE TABLE tag (
    tag_id SERIAL PRIMARY KEY,
    tag_code INT NOT NULL,
    tag_description VARCHAR(50) NOT NULL
);
