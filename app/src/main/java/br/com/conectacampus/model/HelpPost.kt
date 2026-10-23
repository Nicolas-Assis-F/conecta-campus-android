package br.com.conectacampus.model

data class HelpPost(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = PostCategory.MATERIAL.name,
    val location: String = "",
    val contact: String = "",
    val createdAt: Long = 0L,
    val resolved: Boolean = false,
)

data class PostDraft(
    val title: String = "",
    val description: String = "",
    val category: PostCategory = PostCategory.MATERIAL,
    val location: String = "",
    val contact: String = "",
)

enum class PostCategory(val label: String, val emoji: String) {
    MATERIAL("Material de estudo", "📚"),
    CARONA("Carona", "🚗"),
    ACHADOS("Achados e perdidos", "🔎"),
    DOACAO("Doação", "🤝"),
    OUTROS("Outros", "💬");

    companion object {
        fun from(value: String): PostCategory = entries.find { it.name == value } ?: OUTROS
    }
}

data class CampusUser(
    val uid: String,
    val name: String,
    val email: String,
    val isDemo: Boolean = false,
)

