# Permission Flow

Permission Flow is a Compose-first Android permission manager SDK. It exposes small
state holders for single and multiple runtime permissions, backed by `StateFlow`, so
Compose screens can request permissions and react to status changes cleanly.

## Installation

Add Maven Central to your repositories:

```kotlin
repositories {
    mavenCentral()
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("io.github.gourav1908:permission-flow:1.0.0")
}
```

## Usage

Declare any runtime permissions your app needs in `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
```

Request a single permission from a composable:

```kotlin
import android.Manifest
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.permissionflow.compose.rememberPermissionState
import io.github.permissionflow.model.PermissionStatus

@Composable
fun CameraPermissionButton() {
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    val status by permissionState.status.collectAsState()

    Button(
        onClick = permissionState::requestPermission
    ) {
        Text(
            when (status) {
                PermissionStatus.Granted -> "Camera granted"
                PermissionStatus.Denied -> "Camera denied"
                PermissionStatus.PermanentlyDenied -> "Open settings"
                PermissionStatus.NotDetermined -> "Request camera"
            }
        )
    }
}
```

Request multiple permissions:

```kotlin
import android.Manifest
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import io.github.permissionflow.compose.rememberMultiplePermissionState

@Composable
fun MediaPermissionButton() {
    val permissionsState = rememberMultiplePermissionState(
        permissions = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO
        )
    )
    val permissions by permissionsState.permissions.collectAsState()

    Button(
        onClick = permissionsState::requestPermissions
    ) {
        Text("Request permissions (${permissions.size})")
    }
}
```

## Permission Statuses

`PermissionStatus` can be:

- `Granted`
- `Denied`
- `PermanentlyDenied`
- `NotDetermined`

## Requirements

- Android minSdk 24+
- Kotlin
- Jetpack Compose

## Publishing

The Maven coordinates are:

```text
io.github.gourav1908:permission-flow:1.0.0
```

Before publishing a public release, make sure the repository has:

- A public GitHub repository
- This `README.md`
- The Apache-2.0 `LICENSE`
- A version tag such as `v1.0.0`
- A GitHub release for the same version

Signing is configured through external properties. Provide `signing.key`,
`signing.password`, and `signing.keyId` in your local Gradle properties, or set
`SIGNING_KEY` and `SIGNING_PASSWORD` environment variables before publishing.

## License

Permission Flow is released under the Apache License 2.0. See [LICENSE](LICENSE).
