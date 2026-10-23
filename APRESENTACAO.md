# Roteiro para apresentar em 4 minutos

## 1. Problema e proposta — 30 segundos

> “Estudantes frequentemente precisam de um material, uma carona, recuperar um objeto perdido ou doar algo, mas essas mensagens se perdem em vários grupos. O Conecta Campus centraliza essas necessidades em um mural organizado e pesquisável.”

## 2. Demonstração — 2 minutos

1. Entre pelo **modo demonstração** ou faça login no Firebase.
2. No **Mural**, mostre as categorias, a busca e o botão de favorito.
3. Toque em **+** e crie uma publicação realista, por exemplo:
   - categoria: Material de estudo;
   - título: `Monitoria de programação`;
   - descrição: `Posso ajudar iniciantes com lógica na quarta-feira à tarde.`;
   - local: `Biblioteca — mesa 4`;
   - contato: seu e-mail.
4. Abra **Meus posts**, edite a publicação e marque como resolvida.
5. Abra **Perfil** e mostre os números e o estado da conexão.

## 3. Explicação técnica — 1 minuto

> “O front-end é nativo Android, feito em Kotlin com Jetpack Compose. A Navigation organiza as páginas. Um ViewModel concentra o estado e as operações. O Firebase Authentication cuida das contas e o Realtime Database mantém o mural sincronizado. Implementei CRUD completo: criar, ler, atualizar e excluir. As regras do banco impedem que um usuário altere o post de outro.”

Mostre rapidamente estes arquivos:

- `ConectaCampusApp.kt`: navegação e estrutura das páginas;
- `HomeScreen.kt`: interface do mural;
- `AppViewModel.kt`: autenticação e operações no banco;
- `firebase-database-rules.json`: autorização no servidor.

## 4. Fechamento — 30 segundos

> “O projeto pode começar em uma turma e crescer para todo o campus. Próximos passos seriam notificações, denúncia de conteúdo e confirmação de e-mail institucional. A versão atual já resolve o fluxo principal e pode ser testada sem internet no modo demonstração.”

## Perguntas que podem fazer

**Por que Realtime Database?**  
Porque o mural recebe mudanças em tempo real e o modelo JSON é simples para um CRUD didático.

**Onde os dados ficam?**  
Na nuvem do Firebase, separados entre publicações públicas para usuários autenticados e dados privados de cada usuário.

**Qual é a diferença entre front-end e banco?**  
Compose é a interface que o usuário toca. O Firebase autentica e persiste os dados. O ViewModel conecta os dois.

**O que acontece sem internet?**  
Para a aula, o modo demonstração mantém o fluxo funcional. Em produção, pode-se habilitar cache offline do Firebase.

