package com.example.incidentmonitor.data

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Хранилище данных приложения (лабораторная работа № 2).
 *
 * Инциденты хранятся локально в базе SQLite (Room) и не исчезают
 * после закрытия приложения. При первом запуске база заполняется
 * демонстрационными данными.
 */
object IncidentRepository {

    private lateinit var dao: IncidentDao

    private val _incidents = MutableStateFlow<List<Incident>>(emptyList())
    val incidents: StateFlow<List<Incident>> = _incidents.asStateFlow()

    /** Инициализация. Вызывается один раз в MainActivity. */
    fun init(context: Context) {
        dao = AppDatabase.get(context).incidentDao()
        CoroutineScope(Dispatchers.IO).launch {
            // Демонстрационные данные добавляются только в пустую базу,
            // чтобы записи пользователя сохранялись между запусками.
            if (dao.count() == 0) {
                dao.insertAll(demoIncidents().map { it.toEntity() })
            }
            dao.getAll().collect { entities ->
                _incidents.value = entities.map { it.toDomain() }
            }
        }
    }

    fun get(id: Int): Incident? = _incidents.value.firstOrNull { it.id == id }

    suspend fun add(incident: Incident) {
        dao.insert(incident.toEntity())
    }

    suspend fun update(incident: Incident) {
        dao.update(incident.toEntity())
    }

    suspend fun delete(incident: Incident) {
        dao.delete(incident.toEntity())
    }

    /** Новый уникальный идентификатор = максимальный текущий + 1. */
    fun newId(): Int = (_incidents.value.maxOfOrNull { it.id } ?: 0) + 1

    private fun demoIncidents() = listOf(
        Incident(
            id = 1,
            title = "Недоступна 1С:ЗУП для бухгалтерии",
            description = "Пользователи не могут авторизоваться в системе. Ошибка соединения с сервером БД.",
            severity = Severity.CRITICAL,
            status = IncidentStatus.OPEN,
            system = "1С:ЗУП",
            reportedAt = "12.05.2025 09:15"
        ),
        Incident(
            id = 2,
            title = "Медленная работа корпоративного портала",
            description = "Страницы портала загружаются более 10 секунд. Наблюдается с понедельника.",
            severity = Severity.HIGH,
            status = IncidentStatus.IN_PROGRESS,
            system = "SharePoint",
            reportedAt = "12.05.2025 10:40"
        ),
        Incident(
            id = 3,
            title = "Сбой резервного копирования БД",
            description = "Ночная копия БД CRM не была создана. Задача завершилась с ошибкой 0x80070002.",
            severity = Severity.HIGH,
            status = IncidentStatus.OPEN,
            system = "CRM / SQL Server",
            reportedAt = "12.05.2025 06:05"
        ),
        Incident(
            id = 4,
            title = "Не печатает сетевой принтер на 3 этаже",
            description = "Принтер HP LaserJet недоступен с рабочих станций отдела кадров.",
            severity = Severity.MEDIUM,
            status = IncidentStatus.IN_PROGRESS,
            system = "Сетевая печать",
            reportedAt = "11.05.2025 14:22"
        ),
        Incident(
            id = 5,
            title = "Отсутствует интернет в переговорной 204",
            description = "Проводное подключение не работает, Wi-Fi ловит нестабильно.",
            severity = Severity.MEDIUM,
            status = IncidentStatus.OPEN,
            system = "Локальная сеть",
            reportedAt = "11.05.2025 11:50"
        ),
        Incident(
            id = 6,
            title = "Ошибка отправки почты через Outlook",
            description = "У нескольких сотрудников письма застревали в папке «Исходящие». После перезапуска Exchange работа восстановлена.",
            severity = Severity.LOW,
            status = IncidentStatus.RESOLVED,
            system = "Exchange / Outlook",
            reportedAt = "10.05.2025 16:30"
        )
    )
}
