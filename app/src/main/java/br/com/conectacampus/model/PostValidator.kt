package br.com.conectacampus.model

object PostValidator {
    fun validate(draft: PostDraft): String? = when {
        draft.title.trim().length < 5 -> "O título precisa ter pelo menos 5 caracteres."
        draft.description.trim().length < 15 -> "Explique o pedido em pelo menos 15 caracteres."
        draft.location.isBlank() -> "Informe o local ou campus."
        draft.contact.isBlank() -> "Informe uma forma de contato."
        else -> null
    }
}

