package br.com.conectacampus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.conectacampus.model.HelpPost
import br.com.conectacampus.model.PostCategory
import br.com.conectacampus.ui.components.PostCard
import br.com.conectacampus.ui.theme.CampusGreen
import br.com.conectacampus.ui.theme.CampusGreenDark

@Composable
fun HomeScreen(
    posts: List<HelpPost>,
    favoriteIds: Set<String>,
    userName: String,
    onToggleFavorite: (String) -> Unit,
) {
    var search by rememberSaveable { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<PostCategory?>(null) }
    var onlyFavorites by rememberSaveable { mutableStateOf(false) }
    val visiblePosts = posts.filter { post ->
        val matchesText = search.isBlank() || listOf(post.title, post.description, post.location, post.authorName)
            .any { it.contains(search.trim(), ignoreCase = true) }
        val matchesCategory = selectedCategory == null || post.category == selectedCategory?.name
        val matchesFavorite = !onlyFavorites || post.id in favoriteIds
        matchesText && matchesCategory && matchesFavorite
    }
    val firstName = userName.trim().substringBefore(" ").ifBlank { "estudante" }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, top = 18.dp, end = 18.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CampusGreen),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.linearGradient(listOf(CampusGreenDark, CampusGreen)))
                        .padding(22.dp),
                ) {
                    Column {
                        Text(
                            "Olá, $firstName 👋",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "O que você precisa hoje? A comunidade pode ajudar.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = .82f),
                        )
                        Spacer(Modifier.height(18.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).background(MaterialTheme.colorScheme.onPrimary.copy(alpha = .14f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(Icons.Default.Groups, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(20.dp))
                            }
                            Text(
                                "  ${posts.size} ${if (posts.size == 1) "publicação ativa" else "publicações no mural"}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Text("Explore o mural", style = MaterialTheme.typography.titleLarge)
            Text(
                "Busque por assunto, pessoa ou local.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                label = { Text("Buscar no mural") },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = selectedCategory == null && !onlyFavorites,
                    onClick = { selectedCategory = null; onlyFavorites = false },
                    label = { Text("Todos") },
                )
                FilterChip(
                    selected = onlyFavorites,
                    onClick = { onlyFavorites = !onlyFavorites },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    label = { Text("Favoritos") },
                )
                PostCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                            onlyFavorites = false
                        },
                        label = { Text("${category.emoji} ${category.label}") },
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "${visiblePosts.size} ${if (visiblePosts.size == 1) "resultado" else "resultados"}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (visiblePosts.isEmpty()) {
            item {
                EmptyState(
                    title = "Nenhuma publicação encontrada",
                    description = "Tente outro termo ou filtro. Você também pode criar a primeira publicação.",
                )
            }
        } else {
            items(visiblePosts, key = { it.id }) { post ->
                PostCard(
                    post = post,
                    isFavorite = post.id in favoriteIds,
                    onToggleFavorite = { onToggleFavorite(post.id) },
                )
            }
        }
    }
}
