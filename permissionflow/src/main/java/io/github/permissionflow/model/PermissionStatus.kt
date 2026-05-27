package io.github.permissionflow.model

sealed interface PermissionStatus {

    data object Granted : PermissionStatus

    data object Denied : PermissionStatus

    data object PermanentlyDenied : PermissionStatus

    data object NotDetermined : PermissionStatus // Not yet Requested
}
