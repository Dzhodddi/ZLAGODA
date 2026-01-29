INSERT INTO employee (
    id_employee, empl_surname, empl_name, empl_patronymic,
    empl_role, empl_salary, date_of_birth, date_of_start,
    phone_number, city, street, zip_code, password
) VALUES
      (
          'EMP0001', 'Smith', 'John', 'A.',
          'MANAGER', 5000.00, '1980-01-01', '2020-01-01',
          '+380501234567', 'Kyiv', 'Shevchenko', '01001',
          '$2a$10$wmxUmwJ8q0k1wdySl5StRuQKY0dtTbRDRLKfZ3CJKjHLhHIXp8qKO'
      ),

      (
          'EMP0002', 'Doe', 'Jane', 'B.',
          'CASHIER', 3000.00, '1990-05-15', '2022-06-01',
          '+380501234568', 'Lviv', 'Franko', '79000',
          '$2a$10$wmxUmwJ8q0k1wdySl5StRuQKY0dtTbRDRLKfZ3CJKjHLhHIXp8qKO'
      );