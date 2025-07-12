package ru.nicholas.smarttracker.util;

/**
 * Роли сотрудников.
 */
public enum Role {
    ROLE_ADMIN,     // Полный доступ
    ROLE_MANAGER,   // Может создавать задачи и видеть свои
    ROLE_EMPLOYEE   // Видит только назначенные задачи
}