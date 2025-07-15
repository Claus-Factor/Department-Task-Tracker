INSERT INTO tasks (title, description, created_at, deadline, priority, status, assignee_id, assigner_id)
VALUES
('Подготовить КП для ООО "ТехноПром"', 'Сформировать коммерческое предложение по пакету "Бизнес"', CURRENT_TIMESTAMP, '2024-05-15', 'HIGH', 'IN_PROGRESS', 8, 4),
('Провести переговоры с ИП Соколов', 'Обсудить условия поставки на 2024 год', CURRENT_TIMESTAMP, '2024-05-10', 'HIGH', 'NEW', 8, 4),
('Обработать входящие заявки', 'Разобрать заявки с сайта за неделю', CURRENT_TIMESTAMP, CURRENT_DATE + INTERVAL '1 day', 'MEDIUM', 'IN_PROGRESS', 9, 4);

INSERT INTO tasks (title, description, created_at, deadline, priority, status, assignee_id, assigner_id)
VALUES
('Сформировать отчет по продажам за апрель', 'Подготовить отчет для руководства с динамикой', CURRENT_TIMESTAMP, '2024-05-05', 'HIGH', 'COMPLETED', 5, 4),
('Проанализировать конкурентов', 'Сравнить ценовую политику 5 основных конкурентов', CURRENT_TIMESTAMP, '2024-05-20', 'MEDIUM', 'NEW', 5, 4),
('Выявить причины падения продаж в регионе', 'По Центральному федеральному округу', CURRENT_TIMESTAMP, '2024-05-12', 'HIGH', 'IN_PROGRESS', 6, 4);

INSERT INTO tasks (title, description, created_at, deadline, priority, status, assignee_id, assigner_id)
VALUES
('Подготовить презентацию для выставки', '"ЭкспоТех 2024", 10 слайдов', CURRENT_TIMESTAMP, '2024-05-25', 'MEDIUM', 'NEW', 7, 4),
('Организовать вебинар для клиентов', 'Тема "Новые продукты 2024", 50 участников', CURRENT_TIMESTAMP, '2024-05-18', 'HIGH', 'IN_PROGRESS', 8, 4),
('Разослать email-рассылку', 'Акция "Летние скидки" для базы клиентов', CURRENT_TIMESTAMP, '2024-05-08', 'MEDIUM', 'COMPLETED', 8, 4);

INSERT INTO tasks (title, description, created_at, deadline, priority, status, assignee_id, assigner_id)
VALUES
('Оптимизировать CRM-систему', 'Настроить новые этапы воронки продаж', CURRENT_TIMESTAMP, '2024-05-30', 'LOW', 'NEW', 8, 4),
('Провести обучение новых менеджеров', '2 стажера, программа на 2 недели', CURRENT_TIMESTAMP, '2024-05-15', 'HIGH', 'IN_PROGRESS', 7, 4),
('Составить график дежурств на май', 'Распределить выходы в выходные дни', CURRENT_TIMESTAMP, '2024-05-01', 'LOW', 'COMPLETED', 6, 4);

INSERT INTO tasks (title, description, created_at, deadline, priority, status, assignee_id, assigner_id)
VALUES
('Вернуть потерянного клиента (ООО "СтройГарант")', 'Предложить специальные условия', CURRENT_TIMESTAMP, '2024-05-22', 'HIGH', 'NEW', 5, 4),
('Подготовить кейс успешной сделки', 'Для корпоративного портала', CURRENT_TIMESTAMP, '2024-05-17', 'MEDIUM', 'IN_PROGRESS', 5, 4),
('Разработать систему мотивации для менеджеров', 'Новый бонусный план на 3 квартал', CURRENT_TIMESTAMP, '2024-05-31', 'HIGH', 'NEW', 6, 4);