package io.github.permissionflow.extensions

import android.content.Context
import io.github.permissionflow.model.PermissionStatus


val PermissionStatus.isGranted: Boolean
    get() = this is PermissionStatus.Granted

val PermissionStatus.isDenied: Boolean
    get() = this is PermissionStatus.Denied

val PermissionStatus.isPermanentlyDenied: Boolean
    get() = this is PermissionStatus.PermanentlyDenied

val PermissionStatus.isNotDetermined: Boolean
    get() = this is PermissionStatus.NotDetermined

fun Context.openAppSettings() {

}