# Configuração do Firebase

Reserve aproximadamente 10 minutos.

## 1. Criar o projeto e cadastrar o app

1. Entre em [console.firebase.google.com](https://console.firebase.google.com/).
2. Clique em **Adicionar projeto** e escolha um nome, por exemplo `conecta-campus`.
3. Na página inicial do projeto, clique no ícone do Android.
4. Em **Nome do pacote Android**, informe exatamente:

   ```text
   br.com.conectacampus
   ```

5. Baixe o arquivo `google-services.json`.
6. Coloque o arquivo em:

   ```text
   ConectaCampus/app/google-services.json
   ```

Não altere o nome nem coloque o arquivo na raiz do projeto.

## 2. Ativar login por e-mail

1. No menu do Firebase, abra **Authentication**.
2. Clique em **Começar**.
3. Em **Sign-in method**, abra **E-mail/senha**.
4. Ative a primeira opção e salve.

## 3. Criar o Realtime Database

1. Abra **Realtime Database** no menu.
2. Clique em **Criar banco de dados**.
3. Escolha uma região próxima e inicie em modo bloqueado.
4. Abra a aba **Regras**.
5. Copie todo o conteúdo de `firebase-database-rules.json`, cole e clique em **Publicar**.

As regras exigem login e permitem que somente o autor altere a própria publicação. Cada usuário só enxerga e modifica os próprios favoritos.

## 4. Executar

1. No Android Studio, use **File > Sync Project with Gradle Files**.
2. Execute o app novamente.
3. A tela de entrada mostrará **Firebase conectado**.
4. Crie duas contas diferentes para demonstrar que o mural é compartilhado, mas cada autor controla apenas suas publicações.

## Problemas comuns

- **Ainda aparece “Firebase não configurado”**: confirme que o arquivo está em `app/google-services.json` e sincronize o Gradle.
- **Cadastro falha**: confirme que o provedor E-mail/senha foi ativado no Authentication.
- **Permission denied**: publique as regras fornecidas e verifique se o usuário está autenticado.
- **Package name diferente**: cadastre no Firebase exatamente `br.com.conectacampus`.

