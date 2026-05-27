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
import io.github.permissionflow.api.PermissionState
import io.github.permissionflow.model.PermissionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun rememberPermissionState(
    permission: String
): PermissionState {
    val context = LocalContext.current
    val activity = context.findActivity()

    val statusFlow = remember {
        MutableStateFlow<PermissionStatus>(
            PermissionStatus.NotDetermined
        )
    }

    var permissionRequested by remember {
        mutableStateOf(false)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->

        statusFlow.value = when {

            granted -> {
                PermissionStatus.Granted
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
            ) -> {
                PermissionStatus.Denied
            }

            else -> {
                if (permissionRequested) {
                    PermissionStatus.PermanentlyDenied
                } else {
                    PermissionStatus.NotDetermined
                }
            }
        }
    }

    return remember {

        object : PermissionState {

            override val status: StateFlow<PermissionStatus> =
                statusFlow.asStateFlow()

            override fun requestPermission() {
                permissionRequested = true
                launcher.launch(permission)
            }
        }
    }
}


private fun getPermissionStatus(
    context: Context,
    activity: Activity,
    permission: String
): PermissionStatus {

    return when {

        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED -> {
            PermissionStatus.Granted
        }

        ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            permission
        ) -> {
            PermissionStatus.Denied
        }

        else -> {
            PermissionStatus.PermanentlyDenied
        }
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