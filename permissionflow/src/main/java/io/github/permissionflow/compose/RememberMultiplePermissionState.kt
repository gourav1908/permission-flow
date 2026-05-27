package io.github.permissionflow.compose

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.permissionflow.api.MultiplePermissionState
import io.github.permissionflow.model.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun rememberPermissionState(
    permissions: List<String>
): MultiplePermissionState = rememberMultiplePermissionState(
    permissions = permissions
)

@Composable
fun rememberMultiplePermissionState(
    permissions: List<String>
): MultiplePermissionState {
    val context = LocalContext.current
    val activity = context.findActivity()

    val uniquePermissions = remember(permissions) {
        permissions.distinct()
    }

    val permissionsFlow = remember(uniquePermissions) {
        MutableStateFlow(
            uniquePermissions.associateWith { permission ->
                getPermissionStatus(
                    context = context,
                    activity = activity,
                    permission = permission,
                    permissionRequested = false
                )
            }
        )
    }

    var permissionsRequested by remember {
        mutableStateOf(false)
    }

    fun updatePermissionStatuses(results: Map<String, Boolean> = emptyMap()) {
        permissionsFlow.value = uniquePermissions.associateWith { permission ->
            val granted = results[permission]

            when {
                granted == true -> PermissionStatus.Granted

                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED -> PermissionStatus.Granted

                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    permission
                ) -> PermissionStatus.Denied

                permissionsRequested -> PermissionStatus.PermanentlyDenied

                else -> PermissionStatus.NotDetermined
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        updatePermissionStatuses(results)
    }

    return remember(uniquePermissions) {
        object : MultiplePermissionState {

            override val permissions: StateFlow<Map<String, PermissionStatus>> =
                permissionsFlow.asStateFlow()

            override fun requestPermissions() {
                permissionsRequested = true

                if (uniquePermissions.isEmpty()) {
                    permissionsFlow.value = emptyMap()
                    return
                }

                launcher.launch(uniquePermissions.toTypedArray())
            }
        }
    }
}

private fun getPermissionStatus(
    context: Context,
    activity: Activity,
    permission: String,
    permissionRequested: Boolean
): PermissionStatus {
    return when {
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> PermissionStatus.Granted

        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permission
        ) -> PermissionStatus.Denied

        permissionRequested -> PermissionStatus.PermanentlyDenied

        else -> PermissionStatus.NotDetermined
    }
}

private fun Context.findActivity(): Activity {
    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }

        context = context.baseContext
    }

    error("Activity not found")
}
