package com.example.incidentmonitor.data.api

import kotlinx.serialization.Serializable

/** Модели ответа REST API jsonplaceholder.typicode.com (учебный веб-сервис). */
@Serializable
data class ApiUser(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val company: ApiCompany
)

@Serializable
data class ApiCompany(val name: String)
