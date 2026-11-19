package com.example.incidentmonitor.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.incidentmonitor.data.Incident
import com.example.incidentmonitor.data.IncidentRepository
import com.example.incidentmonitor.data.IncidentStatus
import com.example.incidentmonitor.data.Severity
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Форма ввода данных. Работает в двух режимах:
 * - incidentId == null  -> добавление нового инцидента;
 * - incidentId != null  -> редактирование существующего.
 *
 * Использует системную функцию устройства (лабораторная работа № 3):
 * камера — фотофиксация места инцидента с запросом разрешения ОС.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentFormScreen(
    incidentId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val existing = incidentId?.let { IncidentRepository.get(it) }
    val isEditMode = existing != null
    val context = LocalContext.current

    var title by remember { mutableStateOf(existing?.title ?: "") }
    var system by remember { mutableStateOf(existing?.system ?: "") }
    var description by remember { mutableStateOf(existing?.description ?: "") }
    var severity by remember { mutableStateOf(existing?.severity ?: Severity.MEDIUM) }
    var status by remember { mutableStateOf(existing?.status ?: IncidentStatus.OPEN) }
    var photoPath by remember { mutableStateOf(existing?.photoPath) }
    var titleError by remember { mutableStateOf(false) }
    var cameraMessage by remember { mutableStateOf<String?>(null) }
    var severityMenuOpen by remember { mutableStateOf(false) }
    var statusMenuOpen by remember { mutableStateOf(false) }
    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()

    // Съёмка фото: системное приложение камеры сохраняет снимок в файл приложения
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoPath = pendingPhotoUri?.toString()
            cameraMessage = null
        } else {
            cameraMessage = "Съёмка отменена — фото не сделано"
        }
    }

    /** Создание файла и запуск камеры. */
    fun launchTakePhoto() {
        val dir = File(context.getExternalFilesDir(null), "photos").apply { mkdirs() }
        val file = File(
            dir,
            "incident_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.jpg"
        )
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        pendingPhotoUri = uri
        takePictureLauncher.launch(uri)
    }

    // Запрос разрешения на камеру (обрабатывается и отказ пользователя)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchTakePhoto()
        } else {
            cameraMessage = "Разрешение на камеру не выдано. " +
                "Функция фотофиксации недоступна, но инцидент можно сохранить без фото."
        }
    }

    /** Проверка разрешения перед запуском камеры. */
    fun startTakePhoto() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            launchTakePhoto()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Редактирование инцидента" else "Новый инцидент") },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it; titleError = false },
                label = { Text("Название инцидента *") },
                isError = titleError,
                supportingText = {
                    if (titleError) Text("Поле обязательно для заполнения")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = system,
                onValueChange = { system = it },
                label = { Text("Затронутая система") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Описание") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            // Фотофиксация с места инцидента (камера устройства)
            Text(
                text = "Фотофиксация",
                style = MaterialTheme.typography.titleMedium
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (photoPath != null) {
                    AsyncImage(
                        model = photoPath,
                        contentDescription = "Фото инцидента",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(84.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Column {
                    Button(onClick = { startTakePhoto() }) {
                        Text(if (photoPath == null) "Сделать фото" else "Переснять")
                    }
                    if (photoPath != null) {
                        TextButton(onClick = { photoPath = null; cameraMessage = null }) {
                            Text("Убрать фото", color = Color(0xFFC62828))
                        }
                    }
                }
            }
            cameraMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFFC62828),
                    fontSize = 13.sp
                )
            }

            // Выбор критичности
            ExposedDropdownMenuBox(
                expanded = severityMenuOpen,
                onExpandedChange = { severityMenuOpen = it }
            ) {
                OutlinedTextField(
                    value = severity.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Критичность") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = severityMenuOpen)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = severityMenuOpen,
                    onDismissRequest = { severityMenuOpen = false }
                ) {
                    Severity.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = { severity = option; severityMenuOpen = false }
                        )
                    }
                }
            }

            // Выбор статуса
            ExposedDropdownMenuBox(
                expanded = statusMenuOpen,
                onExpandedChange = { statusMenuOpen = it }
            ) {
                OutlinedTextField(
                    value = status.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Статус") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusMenuOpen)
                    },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = statusMenuOpen,
                    onDismissRequest = { statusMenuOpen = false }
                ) {
                    IncidentStatus.entries.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            onClick = { status = option; statusMenuOpen = false }
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) {
                        titleError = true
                        return@Button
                    }
                    val now = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
                        .format(Date())
                    scope.launch {
                        if (existing != null) {
                            IncidentRepository.update(
                                existing.copy(
                                    title = title.trim(),
                                    description = description.trim(),
                                    severity = severity,
                                    status = status,
                                    system = system.trim(),
                                    photoPath = photoPath
                                )
                            )
                        } else {
                            IncidentRepository.add(
                                Incident(
                                    id = IncidentRepository.newId(),
                                    title = title.trim(),
                                    description = description.trim(),
                                    severity = severity,
                                    status = status,
                                    system = system.trim(),
                                    reportedAt = now,
                                    photoPath = photoPath
                                )
                            )
                        }
                        onSaved()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (isEditMode) "Сохранить изменения" else "Создать инцидент")
            }
        }
    }
}
