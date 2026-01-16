CREATE TABLE IF NOT EXISTS employee(
    id_employee VARCHAR(10) PRIMARY KEY,
    empl_surname VARCHAR(50) NOT NULL UNIQUE,
    empl_name VARCHAR(50) NOT NULL,
    empl_patronymic VARCHAR(50),
    role_id BIGINT NOT NULL,
    salary DECIMAL(13,4) NOT NULL DEFAULT 0,
    date_of_birth DATE NOT NULL,
    date_of_start DATE NOT NULL,
    phone_number VARCHAR(13) NOT NULL,
    city VARCHAR(50) NOT NULL,
    street VARCHAR(50) NOT NULL,
    zip_code VARCHAR(9) NOT NULL,
    password VARCHAR(255) NOT NULL,
    CONSTRAINT fk_employee_role FOREIGN KEY (role_id) REFERENCES role(id)
);