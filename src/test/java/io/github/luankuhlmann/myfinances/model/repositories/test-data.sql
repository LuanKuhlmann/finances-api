CREATE TABLE IF NOT EXISTS finance.entries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    description VARCHAR(255),
    month INTEGER,
    year INTEGER,
    value DECIMAL(19, 2),
    type VARCHAR(255),
    status VARCHAR(255),
    user_id BIGINT,
    register_date DATE
);