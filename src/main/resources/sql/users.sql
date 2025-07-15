-- Администраторы
INSERT INTO users (username, password, full_name, email, role, department_id) VALUES
-- Руководители
('manager1', '$2a$10$zK8L9M0nJ1kL2mN3oP4q5.rS6t7uV8wX9yZ0A1B2C3D4E5F6G7H', 'Менеджеров Алексей Алексеевич', 'alexey.2000@company.com', 'ROLE_MANAGER', 1),
('manager2', '$2a$10$aB1C2D3E4F5G6H7I8J9K0.lM1N2oP3qR4sT5uV6wX7yZ8A9B0C1D', 'Зеленов Иван Иванович', 'ivan_ivan@company.com', 'ROLE_MANAGER', 2),
('manager4', '$2a$10$bC2D3E4F5G6H7I8J9K0L1.mN2oP3qR4sT5uV6wX7yZ8A9B0C1D2E', 'Волков Дмитрий Иванович', 'dmitry.volkov@company.com', 'ROLE_MANAGER', 4),
-- Сотрудники
('emp1', '$2a$10$cD3E4F5G6H7I8J9K0L1M2.nO3pP4qR5sT6uV7wX8yZ9A0B1C2D3E', 'Володин Василий Павлович', 'vol.vas@company.com', 'ROLE_EMPLOYEE', 4),
('emp2', '$2a$10$dE4F5G6H7I8J9K0L1M2N3.oP4qR5sT6uV7wX8yZ9A0B1C2D3E4F', 'Белов Сергей Анатольевич', 'sergey.belov@company.com', 'ROLE_EMPLOYEE', 4),
('emp3', '$2a$10$eF5G6H7I8J9K0L1M2N3O4.pQ5rS6tU7vW8xY9Z0A1B2C3D4E5F6G', 'Морозова Татьяна Владимировна', 'tatyana.morozova@company.com', 'ROLE_EMPLOYEE', 4),
('emp4', '$2a$10$fG6H7I8J9K0L1M2N3O4P5.qR6sT7uV8wX9yZ0A1B2C3D4E5F6G7H', 'Иванов Артем Владимирович', 'artem.ivanov@company.com', 'ROLE_EMPLOYEE', 4),
('emp5', '$2a$10$gH7I8J9K0L1M2N3O4P5Q6.rS7tU8vW9xY0Z1A2B3C4D5E6F7G8H', 'Семенов Павел Николаевич', 'pavel.semenov@company.com', 'ROLE_EMPLOYEE', 4),
('emp6', '$2a$10$hI8J9K0L1M2N3O4P5Q6R7.sT8uV9wX0yZ1A2B3C4D5E6F7G8H9I', 'Петрова Наталья Ивановна', 'natalia.petrova@company.com', 'ROLE_EMPLOYEE', 1),
('emp7', '$2a$10$iJ9K0L1M2N3O4P5Q6R7S8.tU9vW0xY1Z2A3B4C5D6E7F8G9H0I1J', 'Федоров Михаил Михайлович', 'mikhail.fedorov@company.com', 'ROLE_EMPLOYEE', 1),
('emp8', '$2a$10$jK0L1M2N3O4P5Q6R7S8T9.uV0wX1yZ2A3B4C5D6E7F8G9H0I1J2K', 'Павлов Виктор Викторович', 'viktor.pavlov@company.com', 'ROLE_EMPLOYEE', 1),
('emp9', '$2a$10$kL1M2N3O4P5Q6R7S8T9U0.vW1xY2Z3A4B5C6D7E8F9G0H1I2J3K', 'Соколов Галина Семеновна', 'galina.sokolova@company.com', 'ROLE_EMPLOYEE', 2),
('emp10', '$2a$10$lM2N3O4P5Q6R7S8T9U0V1.wX2yZ3A4B5C6D7E8F9G0H1I2J3K4L', 'Смит Иван Петрович', 'john.smith@company.com', 'ROLE_EMPLOYEE', 2);