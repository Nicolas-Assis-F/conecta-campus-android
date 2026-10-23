package br.com.conectacampus.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.conectacampus.model.HelpPost
import br.com.conectacampus.ui.components.PostCard

@Composable
fun MyPostsScreen(
    posts: List<HelpPost>,
    onEdit: (HelpPost) -> Unit,
    onDelete: (HelpPost) -> Unit,
    onToggleResolved: (HelpPost) -> Unit,
) {
    var deleteTarget by remember { mutableStateOf<HelpPost?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Minhas publicações", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(
                "Acompanhe o que você compartilhou.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Surface(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(18.dp),
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Icon(Icons.Default.TaskAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("${posts.size} ${if (posts.size == 1) "publicação" else "publicações"}", fontWeight = FontWeight.Bold)
                        Text("${posts.count { it.resolved }} resolvidas", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
        if (posts.isEmpty()) {
            item {
                EmptyState(
                    title = "Você ainda não publicou",
                    description = "Use o botão Publicar para pedir ajuda ou oferecer algo à comunidade.",
                )
            }
        } else {
            items(posts, key = { it.id }) { post ->
                PostCard(post = post) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { onEdit(post) }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Edit, contentDescription = null)
                                Text(" Editar")
                            }
                            OutlinedButton(
                                onClick = { deleteTarget = post },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Text(" Excluir")
                            }
                        }
                        Button(onClick = { onToggleResolved(post) }, modifier = Modifier.fillMaxWidth()) {
                            Text(if (post.resolved) "Reabrir publicação" else "Marcar como resolvida")
                        }
                    }
                }
            }
        }
    }

    deleteTarget?.let { post ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text("Excluir publicação?") },
            text = { Text("Esta ação remove “${post.title}” do mural.") },
            confirmButton = {
                TextButton(onClick = { onDelete(post); deleteTarget = null }) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancelar") }
            },
        )
    }
}

