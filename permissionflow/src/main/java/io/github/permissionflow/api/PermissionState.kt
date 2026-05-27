package io.github.permissionflow.api

import io.github.permissionflow.model.PermissionStatus
import kotlinx.coroutines.flow.StateFlow

interface PermissionState {

    val status: StateFlow<PermissionStatus>

    fun requestPermission()
}