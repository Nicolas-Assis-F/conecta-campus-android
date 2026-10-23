package br.com.conectacampus.model

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class PostValidatorTest {
    @Test
    fun `publicacao completa e valida`() {
        val draft = PostDraft(
            title = "Grupo de estudos",
            description = "Vamos estudar juntos para a próxima prova.",
            location = "Biblioteca",
            contact = "aluno@campus.edu",
        )
        assertNull(PostValidator.validate(draft))
    }

    @Test
    fun `descricao curta e rejeitada`() {
        val draft = PostDraft(
            title = "Carona",
            description = "Preciso",
            location = "Centro",
            contact = "99999-0000",
        )
        assertNotNull(PostValidator.validate(draft))
    }
}
