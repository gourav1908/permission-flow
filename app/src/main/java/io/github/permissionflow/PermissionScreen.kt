package io.github.permissionflow

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.permissionflow.compose.rememberPermissionState
import io.github.permissionflow.model.PermissionStatus

@Composable
fun PermissionScreen() {
    val singlePermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    val multiplePermissionState = rememberPermissionState(
        permissions = listOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.RECORD_AUDIO
        )
    )

    val singlePermissionStatus by singlePermissionState.status.collectAsState()
    val multiplePermissionStatuses by multiplePermissionState.permissions.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Single Permission: ${singlePermissionStatus.statusText()}"
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                singlePermissionState.requestPermission()
            }
        ) {
            Text("Request Single Permission")
        }

        Spacer(modifier = Modifier.height(24.dp))

        multiplePermissionStatuses.forEach { (permission, status) ->
            Text(
                text = "${permission.permissionName()}: ${status.statusText()}"
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                multiplePermissionState.requestPermissions()
            }
        ) {
            Text("Request Multiple Permissions")
        }
    }
}

private fun String.permissionName(): String {
    return substringAfterLast(".")
        .lowercase()
        .replaceFirstChar { it.titlecase() }
}

private fun PermissionStatus.statusText(): String {
    return when (this) {
        PermissionStatus.Granted -> "Granted"
        PermissionStatus.Denied -> "Denied"
        PermissionStatus.PermanentlyDenied -> "Permanently Denied"
        PermissionStatus.NotDetermined -> "Not yet requested"
    }
}
