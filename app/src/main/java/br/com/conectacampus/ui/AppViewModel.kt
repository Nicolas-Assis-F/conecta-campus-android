package br.com.conectacampus.ui

import androidx.lifecycle.ViewModel
import br.com.conectacampus.BuildConfig
import br.com.conectacampus.model.CampusUser
import br.com.conectacampus.model.HelpPost
import br.com.conectacampus.model.PostCategory
import br.com.conectacampus.model.PostDraft
import br.com.conectacampus.model.PostValidator
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppUiState(
    val isLoading: Boolean = false,
    val currentUser: CampusUser? = null,
    val posts: List<HelpPost> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val error: String? = null,
    val message: String? = null,
    val firebaseConfigured: Boolean = BuildConfig.FIREBASE_CONFIGURED,
)

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppUiState())
    val state: StateFlow<AppUiState> = _state.asStateFlow()

    private var auth: FirebaseAuth? = null
    private var root: DatabaseReference? = null
    private var postsListener: ValueEventListener? = null
    private var favoritesListener: ValueEventListener? = null

    init {
        if (BuildConfig.FIREBASE_CONFIGURED) {
            runCatching {
                auth = FirebaseAuth.getInstance()
                root = FirebaseDatabase.getInstance().reference
                auth?.currentUser?.let { firebaseUser ->
                    val user = CampusUser(
                        uid = firebaseUser.uid,
                        name = firebaseUser.displayName ?: firebaseUser.email?.substringBefore('@').orEmpty(),
                        email = firebaseUser.email.orEmpty(),
                    )
                    _state.value = _state.value.copy(currentUser = user)
                    observeData(user.uid)
                }
            }.onFailure {
                _state.value = _state.value.copy(error = "Não foi possível iniciar o Firebase: ${it.message}")
            }
        }
    }

    fun login(email: String, password: String) {
        if (!BuildConfig.FIREBASE_CONFIGURED) {
            showError("Configure o Firebase ou use o modo demonstração.")
            return
        }
        if (email.isBlank() || password.isBlank()) {
            showError("Preencha e-mail e senha.")
            return
        }
        setLoading()
        auth?.signInWithEmailAndPassword(email.trim(), password)
            ?.addOnSuccessListener { result ->
                val firebaseUser = result.user ?: return@addOnSuccessListener showError("Usuário não encontrado.")
                val user = CampusUser(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: email.substringBefore('@'),
                    email = firebaseUser.email.orEmpty(),
                )
                _state.value = _state.value.copy(isLoading = false, currentUser = user, error = null)
                observeData(user.uid)
            }
            ?.addOnFailureListener { showError(authMessage(it)) }
    }

    fun register(name: String, email: String, password: String) {
        if (!BuildConfig.FIREBASE_CONFIGURED) {
            showError("Configure o Firebase ou use o modo demonstração.")
            return
        }
        when {
            name.trim().length < 3 -> return showError("Digite seu nome completo.")
            !email.contains('@') -> return showError("Digite um e-mail válido.")
            password.length < 6 -> return showError("A senha precisa ter pelo menos 6 caracteres.")
        }
        setLoading()
        auth?.createUserWithEmailAndPassword(email.trim(), password)
            ?.addOnSuccessListener { result ->
                val firebaseUser = result.user ?: return@addOnSuccessListener showError("Falha ao criar usuário.")
                val cleanName = name.trim()
                val profile = UserProfileChangeRequest.Builder().setDisplayName(cleanName).build()
                firebaseUser.updateProfile(profile)
                root?.child("users")?.child(firebaseUser.uid)?.child("profile")
                    ?.setValue(mapOf("name" to cleanName, "email" to email.trim()))
                val user = CampusUser(firebaseUser.uid, cleanName, email.trim())
                _state.value = _state.value.copy(
                    isLoading = false,
                    currentUser = user,
                    error = null,
                    message = "Conta criada com sucesso!",
                )
                observeData(user.uid)
            }
            ?.addOnFailureListener { showError(authMessage(it)) }
    }

    fun enterDemo() {
        detachListeners()
        val user = CampusUser("demo-user", "Aluno Demonstração", "demo@campus.edu", isDemo = true)
        _state.value = AppUiState(
            currentUser = user,
            posts = demoPosts(),
            firebaseConfigured = BuildConfig.FIREBASE_CONFIGURED,
            message = "Modo demonstração: os dados ficam apenas neste aparelho.",
        )
    }

    fun logout() {
        detachListeners()
        auth?.signOut()
        _state.value = AppUiState(firebaseConfigured = BuildConfig.FIREBASE_CONFIGURED)
    }

    fun savePost(draft: PostDraft, editingId: String? = null): Boolean {
        val validation = PostValidator.validate(draft)
        if (validation != null) {
            showError(validation)
            return false
        }
        val user = _state.value.currentUser ?: return false
        val existing = editingId?.let { id -> _state.value.posts.find { it.id == id } }
        if (existing != null && existing.authorId != user.uid) {
            showError("Você só pode editar suas próprias publicações.")
            return false
        }
        val id = existing?.id ?: root?.child("posts")?.push()?.key ?: "demo-${System.currentTimeMillis()}"
        val post = HelpPost(
            id = id,
            authorId = user.uid,
            authorName = user.name,
            title = draft.title.trim(),
            description = draft.description.trim(),
            category = draft.category.name,
            location = draft.location.trim(),
            contact = draft.contact.trim(),
            createdAt = existing?.createdAt ?: System.currentTimeMillis(),
            resolved = existing?.resolved ?: false,
        )
        if (user.isDemo) {
            val updated = _state.value.posts.filterNot { it.id == id } + post
            _state.value = _state.value.copy(
                posts = updated.sortedByDescending { it.createdAt },
                message = if (existing == null) "Publicação criada!" else "Publicação atualizada!",
                error = null,
            )
        } else {
            root?.child("posts")?.child(id)?.setValue(post)
                ?.addOnSuccessListener { showMessage(if (existing == null) "Publicação criada!" else "Publicação atualizada!") }
                ?.addOnFailureListener { showError("Falha ao salvar: ${it.message}") }
        }
        return true
    }

    fun toggleResolved(post: HelpPost) {
        val user = _state.value.currentUser ?: return
        if (post.authorId != user.uid) return showError("Somente o autor pode alterar o status.")
        if (user.isDemo) {
            val updated = _state.value.posts.map { if (it.id == post.id) it.copy(resolved = !it.resolved) else it }
            _state.value = _state.value.copy(posts = updated, message = "Status atualizado.")
        } else {
            root?.child("posts")?.child(post.id)?.child("resolved")?.setValue(!post.resolved)
        }
    }

    fun deletePost(post: HelpPost) {
        val user = _state.value.currentUser ?: return
        if (post.authorId != user.uid) return showError("Você só pode excluir suas próprias publicações.")
        if (user.isDemo) {
            _state.value = _state.value.copy(
                posts = _state.value.posts.filterNot { it.id == post.id },
                favoriteIds = _state.value.favoriteIds - post.id,
                message = "Publicação excluída.",
            )
        } else {
            root?.child("posts")?.child(post.id)?.removeValue()
                ?.addOnSuccessListener { showMessage("Publicação excluída.") }
                ?.addOnFailureListener { showError("Falha ao excluir: ${it.message}") }
        }
    }

    fun toggleFavorite(postId: String) {
        val user = _state.value.currentUser ?: return
        val isFavorite = postId in _state.value.favoriteIds
        if (user.isDemo) {
            _state.value = _state.value.copy(
                favoriteIds = if (isFavorite) _state.value.favoriteIds - postId else _state.value.favoriteIds + postId,
            )
        } else {
            val ref = root?.child("users")?.child(user.uid)?.child("favorites")?.child(postId)
            if (isFavorite) ref?.removeValue() else ref?.setValue(true)
        }
    }

    fun clearNotice() {
        _state.value = _state.value.copy(error = null, message = null)
    }

    private fun observeData(uid: String) {
        detachListeners()
        val postsRef = root?.child("posts") ?: return
        postsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val posts = snapshot.children.mapNotNull { it.getValue(HelpPost::class.java) }
                    .sortedByDescending { it.createdAt }
                _state.value = _state.value.copy(posts = posts, isLoading = false)
            }

            override fun onCancelled(error: DatabaseError) {
                showError("Não foi possível carregar o mural: ${error.message}")
            }
        }.also(postsRef::addValueEventListener)

        val favoritesRef = root?.child("users")?.child(uid)?.child("favorites") ?: return
        favoritesListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ids = snapshot.children.filter { it.getValue(Boolean::class.java) == true }.map { it.key.orEmpty() }.toSet()
                _state.value = _state.value.copy(favoriteIds = ids)
            }

            override fun onCancelled(error: DatabaseError) = Unit
        }.also(favoritesRef::addValueEventListener)
    }

    private fun detachListeners() {
        postsListener?.let { root?.child("posts")?.removeEventListener(it) }
        _state.value.currentUser?.uid?.let { uid ->
            favoritesListener?.let { root?.child("users")?.child(uid)?.child("favorites")?.removeEventListener(it) }
        }
        postsListener = null
        favoritesListener = null
    }

    private fun setLoading() {
        _state.value = _state.value.copy(isLoading = true, error = null, message = null)
    }

    private fun showError(text: String) {
        _state.value = _state.value.copy(isLoading = false, error = text, message = null)
    }

    private fun showMessage(text: String) {
        _state.value = _state.value.copy(isLoading = false, message = text, error = null)
    }

    private fun authMessage(error: Exception): String = when (error) {
        is FirebaseAuthUserCollisionException -> "Este e-mail já está cadastrado."
        is FirebaseAuthInvalidCredentialsException -> "E-mail ou senha inválidos."
        is FirebaseNetworkException -> "Sem conexão com a internet."
        else -> error.localizedMessage ?: "Falha na autenticação."
    }

    override fun onCleared() {
        detachListeners()
    }

    private fun demoPosts(): List<HelpPost> {
        val now = System.currentTimeMillis()
        return listOf(
            HelpPost("demo-1", "maria", "Maria Souza", "Grupo de estudos de Matemática", "Procuro colegas para revisar funções antes da prova de sexta.", PostCategory.MATERIAL.name, "Biblioteca — Campus Centro", "maria@campus.edu", now - 3_600_000),
            HelpPost("demo-2", "joao", "João Lima", "Carona para o campus à noite", "Tenho duas vagas saindo do Terminal Norte às 18h20, de segunda a quinta.", PostCategory.CARONA.name, "Terminal Norte", "(11) 99999-0000", now - 7_200_000),
            HelpPost("demo-3", "ana", "Ana Clara", "Encontrei um estojo azul", "Estojo encontrado no laboratório 3. Entrego na coordenação com identificação.", PostCategory.ACHADOS.name, "Bloco B — laboratório 3", "Coordenação do curso", now - 86_400_000),
            HelpPost("demo-4", "demo-user", "Aluno Demonstração", "Doação de livros do 1º semestre", "Tenho três livros introdutórios em bom estado para quem estiver começando o curso.", PostCategory.DOACAO.name, "Cantina do campus", "demo@campus.edu", now - 172_800_000),
        )
    }
}

