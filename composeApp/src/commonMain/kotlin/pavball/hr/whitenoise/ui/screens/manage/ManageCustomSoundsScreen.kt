package pavball.hr.whitenoise.ui.screens.manage

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.domain.model.CustomSound
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_delete
import whitenoise.composeapp.generated.resources.ic_edit

@Composable
fun ManageCustomSoundsScreen(
    navController: NavController,
    customSounds: List<CustomSound>,
    onRename: (CustomSound, String) -> Unit,
    onDelete: (CustomSound) -> Unit
) {
    var editingSound by remember { mutableStateOf<CustomSound?>(null) }
    var editingName by remember { mutableStateOf("") }

    // ---- Rename Dialog ----
    if (editingSound != null) {
        AlertDialog(
            onDismissRequest = { editingSound = null },
            title = { Text("Rename Sound") },
            text = {
                OutlinedTextField(
                    value = editingName,
                    onValueChange = { editingName = it },
                    singleLine = true,
                    label = { Text("Sound name") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRename(editingSound!!, editingName.trim())
                        editingSound = null
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { editingSound = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ---- Main Screen Content ----
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Manage Custom Sounds",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        if (customSounds.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No custom sounds added yet.")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(customSounds) { sound ->

                    Surface(
                        tonalElevation = 3.dp,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            // Sound name + URI small text
                            Column {
                                Text(sound.displayName, fontWeight = FontWeight.Medium)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    sound.id,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }

                            Row {
                                IconButton(
                                    onClick = {
                                        editingSound = sound
                                        editingName = sound.displayName
                                    }
                                ) {
                                    Icon(painter = painterResource(Res.drawable.ic_edit), contentDescription = "Rename")
                                }

                                IconButton(
                                    onClick = { onDelete(sound) }
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.ic_delete),
                                        contentDescription = "Delete",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
