package com.aeromedia.app.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.aeroSkyBackground

/**
 * Shows [content] once the requested media permission is granted; otherwise
 * shows an explanation and a real system permission prompt. One gate per
 * screen (Music/Videos/Pictures each ask only for the permission they
 * actually need), matching how MediaKind is scoped in MediaPermissions.kt.
 */
@Composable
fun MediaPermissionGate(
    kind: MediaKind,
    rationale: String,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(hasMediaPermission(context, kind)) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted -> granted = isGranted }

    if (granted) {
        content()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .aeroSkyBackground()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "AeroMedia needs access",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            textAlign = TextAlign.Center,
        )
        Text(
            text = rationale,
            style = MaterialTheme.typography.bodyMedium,
            color = AeroColors.textSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
        )
        Button(
            onClick = { launcher.launch(permissionFor(kind)) },
            colors = ButtonDefaults.buttonColors(
                containerColor = AeroColors.chromeMid,
                contentColor = AeroColors.surfaceCharcoal,
            ),
            shape = RoundedCornerShape(50),
        ) {
            Text("Grant access")
        }
    }
}
