package io.github.permissionflow.internal

import io.github.permissionflow.model.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow

internal class PermissionStateImpl {
    private val _status =
        MutableStateFlow<PermissionStatus>(PermissionStatus.Denied)
}