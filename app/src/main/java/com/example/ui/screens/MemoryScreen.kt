package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.database.DocumentKnowledgeEntity
import com.example.database.MemoryEntity
import com.example.ui.theme.ManiskAmber
import com.example.ui.theme.ManiskCrimson
import com.example.ui.theme.ManiskCyan
import com.example.ui.theme.ManiskDarkBg
import com.example.ui.theme.ManiskEmerald
import com.example.ui.theme.ManiskSurface
import com.example.ui.theme.ManiskSurfaceBorder
import com.example.ui.theme.ManiskSurfaceElevated
import com.example.ui.theme.ManiskTextMuted
import com.example.ui.theme.ManiskTextPrimary
import com.example.ui.theme.ManiskTextSecondary
import com.example.ui.theme.ManiskViolet

@Composable
fun MemoryScreen(
    memories: List<MemoryEntity>,
    knowledgeDocs: List<DocumentKnowledgeEntity>,
    onRemember: (String, String) -> Unit,
    onForget: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var rememberInput by remember { mutableStateOf("") }
    var forgetInput by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("FACTS") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ManiskDarkBg)
            .padding(16.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "MEMORY ENGINE & KNOWLEDGE STORE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = ManiskCyan,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Authorized semantic recall with retention policies and GDPR purge",
                    fontSize = 12.sp,
                    color = ManiskTextSecondary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Remember New Input Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ManiskCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "STORE AUTHORIZED FACT OR PREFERENCE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ManiskCyan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rememberInput,
                        onValueChange = { rememberInput = it },
                        placeholder = { Text("e.g. 'I prefer cold brew coffee and meetings after 10 AM'", fontSize = 12.sp, color = ManiskTextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("remember_input_field"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManiskCyan,
                            unfocusedBorderColor = ManiskSurfaceBorder,
                            focusedTextColor = ManiskTextPrimary,
                            unfocusedTextColor = ManiskTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("FACTS", "PREFERENCES", "PROJECTS").forEach { cat ->
                                val sel = selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (sel) ManiskCyan else ManiskSurfaceElevated)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        color = if (sel) Color(0xFF042F2E) else ManiskTextSecondary
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = {
                                if (rememberInput.isNotBlank()) {
                                    onRemember(rememberInput, selectedCategory)
                                    rememberInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ManiskCyan),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("save_memory_button")
                        ) {
                            Text("INGEST", color = Color(0xFF042F2E), fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Purge / Forget Tool Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ManiskSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = forgetInput,
                        onValueChange = { forgetInput = it },
                        placeholder = { Text("Forget memory by keyword...", fontSize = 11.sp, color = ManiskTextMuted) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ManiskCrimson,
                            unfocusedBorderColor = ManiskSurfaceBorder,
                            focusedTextColor = ManiskTextPrimary,
                            unfocusedTextColor = ManiskTextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            if (forgetInput.isNotBlank()) {
                                onForget(forgetInput)
                                forgetInput = ""
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ManiskCrimson),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PURGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section: Active Memories
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STORED MEMORY ENTITIES (${memories.size})",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ManiskTextMuted,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "ENCRYPTED AT REST",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = ManiskEmerald
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(memories) { mem ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ManiskSurfaceElevated),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, ManiskSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ManiskViolet.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = mem.type,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ManiskViolet
                            )
                        }

                        Text(
                            text = "Confidence ${(mem.confidence * 100).toInt()}% • ${mem.sensitivity}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            color = ManiskTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = mem.content,
                        fontSize = 13.sp,
                        color = ManiskTextPrimary,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Source: ${mem.source}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        color = ManiskTextMuted
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
