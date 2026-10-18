# language: pt
@login @regression
Funcionalidade: Login
  Como cliente da Swag Labs
  Quero entrar no sistema com o meu usuário e senha
  Para acessar o catálogo de produtos e fazer minhas compras

  Contexto:
    Dado que estou na página de login

  @login-valido
  Esquema do Cenário: <id> - <descricao>
    Quando faço login com o usuário "<usuario>" e a senha "<senha>"
    Então devo ver a página de produtos

    Exemplos:
      | id   | descricao                         | usuario                 | senha        |
      | CT01 | Login com usuário padrão          | standard_user           | secret_sauce |
      | CT02 | Login com problem_user            | problem_user            | secret_sauce |
      | CT03 | Login com performance_glitch_user | performance_glitch_user | secret_sauce |
      | CT04 | Login com error_user              | error_user              | secret_sauce |
      | CT05 | Login com visual_user             | visual_user             | secret_sauce |

  @login-invalido
  Esquema do Cenário: <id> - <descricao>
    Quando faço login com o usuário "<usuario>" e a senha "<senha>"
    Então devo ver a mensagem de erro "<mensagem>"
    E devo continuar na página de login

    Exemplos:
      | id   | descricao                           | usuario         | senha        | mensagem                                                                  |
      | CT06 | Login com usuário bloqueado         | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.                       |
      | CT07 | Login com usuário e senha em branco |                 |              | Epic sadface: Username is required                                        |
      | CT08 | Login informando apenas a senha     |                 | secret_sauce | Epic sadface: Username is required                                        |
      | CT09 | Login informando apenas o usuário   | standard_user   |              | Epic sadface: Password is required                                        |
      | CT10 | Login com senha incorreta           | standard_user   | senha_errada | Epic sadface: Username and password do not match any user in this service |
      | CT11 | Login com usuário inexistente       | user1           | pass1        | Epic sadface: Username and password do not match any user in this service |
      | CT12 | Login com usuário em maiúsculas     | STANDARD_USER   | secret_sauce | Epic sadface: Username and password do not match any user in this service |