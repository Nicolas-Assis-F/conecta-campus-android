# Conecta Campus

Aplicativo Android nativo em Kotlin para aproximar estudantes que precisam ou podem oferecer ajuda. O mural reúne material de estudo, caronas, achados e perdidos, doações e outros pedidos úteis para a comunidade acadêmica.

O projeto foi inspirado na estrutura didática do repositório `profbaiao/aulaMobile`: Jetpack Compose, Navigation, Firebase Authentication e Firebase Realtime Database. Em vez de um CRUD genérico, aqui essas tecnologias sustentam um produto que pode ser usado de verdade no campus.

## O que já funciona

- cadastro e login com e-mail e senha;
- mural atualizado em tempo real;
- criação, edição, conclusão e exclusão de publicações;
- busca e filtro por categoria;
- favoritos separados por usuário;
- perfil com resumo da atividade;
- modo demonstração, para apresentar sem depender da internet ou do Firebase;
- validação dos formulários e mensagens de erro;
- regras de segurança do Realtime Database.

## Abrir e executar hoje

1. Abra esta pasta no Android Studio.
2. Espere o Gradle sincronizar.
3. Crie ou escolha um emulador Android (API 24 ou superior).
4. Clique em **Run ▶**.
5. Na tela inicial, clique em **Entrar no modo demonstração**.

O modo demonstração permite testar todas as telas e o CRUD durante a apresentação. Os dados duram enquanto o app estiver aberto.

## Conectar ao Firebase

Siga [FIREBASE.md](FIREBASE.md). O resumo é: criar o projeto, cadastrar o app Android com o pacote `br.com.conectacampus`, colocar `google-services.json` dentro de `app/`, ativar Authentication por e-mail/senha, criar o Realtime Database e publicar as regras de [firebase-database-rules.json](firebase-database-rules.json).

O Gradle detecta o arquivo `app/google-services.json` automaticamente. Sem ele, compila em modo demonstração; com ele, ativa o Firebase real.

> O arquivo `google-services.json` está no `.gitignore` porque pertence ao seu projeto Firebase. Não use o arquivo de outro projeto.

## Estrutura importante

```text
app/src/main/java/br/com/conectacampus/
├── MainActivity.kt
├── model/
│   ├── HelpPost.kt              # modelos de dados
│   └── PostValidator.kt         # regras do formulário
└── ui/
    ├── AppViewModel.kt          # autenticação, Firebase e CRUD
    ├── ConectaCampusApp.kt      # navegação e estado principal
    ├── components/              # cards e formulário
    ├── screens/                 # login, mural, meus posts e perfil
    └── theme/                   # cores e tema Material 3
```

No Firebase, os dados ficam organizados assim:

```text
posts/{postId}
users/{uid}/profile
users/{uid}/favorites/{postId}
```

## Testes e comandos

No terminal do Android Studio:

```bash
./gradlew test
./gradlew assembleDebug
```

O APK de teste é gerado em `app/build/outputs/apk/debug/app-debug.apk`.

## Como explicar o código

1. **Compose** desenha cada tela usando funções `@Composable`.
2. **Navigation** troca entre Mural, Meus posts e Perfil.
3. **AppViewModel** guarda o estado e evita colocar regras de negócio nas telas.
4. **Firebase Auth** identifica o usuário.
5. **Realtime Database** envia alterações do mural imediatamente para todos os aparelhos conectados.
6. **Regras do banco** garantem que apenas o autor edite ou exclua sua publicação.

Veja o roteiro pronto em [APRESENTACAO.md](APRESENTACAO.md).

