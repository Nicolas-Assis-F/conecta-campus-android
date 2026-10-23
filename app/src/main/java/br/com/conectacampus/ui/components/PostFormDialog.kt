package br.com.conectacampus.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import br.com.conectacampus.model.HelpPost
import br.com.conectacampus.model.PostCategory
import br.com.conectacampus.model.PostDraft

@Composable
fun PostFormDialog(
    post: HelpPost?,
    onDismiss: () -> Unit,
    onSave: (PostDraft) -> Unit,
) {
    var title by remember(post?.id) { mutableStateOf(post?.title.orEmpty()) }
    var description by remember(post?.id) { mutableStateOf(post?.description.orEmpty()) }
    var category by remember(post?.id) { mutableStateOf(post?.let { PostCategory.from(it.category) } ?: PostCategory.MATERIAL) }
    var location by remember(post?.id) { mutableStateOf(post?.location.orEmpty()) }
    var contact by remember(post?.id) { mutableStateOf(post?.contact.orEmpty()) }
    val canSave = title.trim().length >= 5 && description.trim().length >= 15 && location.isNotBlank() && contact.isNotBlank()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(
                modifier = Modifier.padding(22.dp).verticalScroll(rememberScrollState()),
            ) {
                Text(
                    if (post == null) "Nova publicação" else "Editar publicação",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Conte o que você precisa ou pode oferecer.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(18.dp))
                Text("Categoria", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PostCategory.entries.forEach { item ->
                        FilterChip(
                            selected = category == item,
                            onClick = { category = item },
                            label = { Text("${item.emoji} ${item.label}") },
                        )
                    }
                }
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it.take(80) },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Default.Title, contentDescription = null) },
                    label = { Text("Título") },
                    placeholder = { Text("Ex.: Grupo de estudos de Matemática") },
                    supportingText = { Text("${title.length}/80") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it.take(500) },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.Notes, contentDescription = null) },
                    label = { Text("Descrição") },
                    placeholder = { Text("Explique os detalhes para quem puder ajudar") },
                    supportingText = { Text("Mínimo de 15 caracteres  •  ${description.length}/500") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Default.LocationOn, contentDescription = null) },
                    label = { Text("Local ou campus") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = contact,
                    onValueChange = { contact = it },
                    leadingIcon = { androidx.compose.material3.Icon(Icons.Default.AlternateEmail, contentDescription = null) },
                    label = { Text("Contato") },
                    placeholder = { Text("E-mail, telefone ou sala") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = { onSave(PostDraft(title, description, category, location, contact)) },
                        enabled = canSave,
                        modifier = Modifier.weight(1f),
                    ) { Text("Salvar") }
                }
            }
        }
    }
}
