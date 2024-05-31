package com.example.dooit

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dooit.ui.doitviewmodels.PermissionsViewModel
import com.example.dooit.ui.navigation.TodoNav
import com.example.dooit.ui.theme.DooitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            DooitTheme {
                val permissionsViewModel: PermissionsViewModel = viewModel()
                val permissionsList by permissionsViewModel.deniedPermissions.collectAsStateWithLifecycle()
                val multiplePermissionRequest =
                    rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { perm ->
                        perm.forEach { permission ->
                            permissionsViewModel.addToDeniedPermissions(
                                permission.key,
                                permission.value
                            )
                        }
                    }
                LaunchedEffect(key1 = multiplePermissionRequest) {
                    multiplePermissionRequest.launch(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            arrayOf(
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.READ_MEDIA_IMAGES,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            )
                        }else arrayOf(
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    TodoNav(modifier = Modifier.padding(innerPadding))
                    permissionsList.forEach {
                        PermissionDialog(
                            permission = it,
                            onDismiss = {
                                multiplePermissionRequest.launch(
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        arrayOf(
                                            Manifest.permission.RECORD_AUDIO,
                                            Manifest.permission.READ_MEDIA_IMAGES
                                        )
                                    } else arrayOf(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    )

                                )
                            },
                            onOk = { },
                            onLaunchPermission = {
                                multiplePermissionRequest.launch(
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                        arrayOf(
                                            Manifest.permission.RECORD_AUDIO,
                                            Manifest.permission.READ_MEDIA_IMAGES
                                        )
                                    } else arrayOf(
                                        Manifest.permission.RECORD_AUDIO,
                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                    )

                                )
                            },

                            )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    permission: String,
    onDismiss: () -> Unit,
    onOk: () -> Unit,
    onLaunchPermission: () -> Unit,

    ) {

    AlertDialog(onDismissRequest = onDismiss, confirmButton = {
        Button(onClick = {

            onLaunchPermission()

        }) {
            Text(text = permission)
        }
    },
        title = {
            Text(text = "Permission Denied")
        },
        text = {
            Text(text = "Grant the app the following permissions in order for the app to perform well")
        }, modifier = modifier)
}

