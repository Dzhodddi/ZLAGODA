CREATE TABLE IF NOT EXISTS role (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE
);

INSERT INTO role (id, name) VALUES (1, 'MANAGER'), (2, 'CASHIER')
    ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('role', 'id'), (SELECT MAX(id) FROM role));