package io.github.permissionflow.api

import io.github.permissionflow.model.PermissionStatus
import kotlinx.coroutines.flow.StateFlow

interface MultiplePermissionState {

    val permissions:
            StateFlow<Map<String, PermissionStatus>>

    fun requestPermissions()
}