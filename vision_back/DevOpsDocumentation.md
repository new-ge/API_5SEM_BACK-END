## Database Management 

<details>

### Sumário


1. [Como funciona o Database Management em DevOps?](#como-funciona-o-database-management-em-devops)
2. [Ferramenta utilizada: Flyway](#ferramenta-utilizada-flyway)
   - [2.1 O que é o Flyway?](#21-o-que-é-o-flyway)
   - [2.2 Quando usar o Flyway?](#22-quando-usar-o-flyway)
   - [2.3 Principais características](#23-principais-características)
3. [Como o Flyway foi implementado no nosso projeto?](#como-o-flyway-foi-implementado-no-nosso-projeto)
4. [Versões X Alterações](#versões-x-alterações)
5. [Desafios, Soluções e Lições Aprendidas](#desafios-soluções-e-lições-aprendidas)
6. [Ferramentas Utilizadas em Nosso Projeto](#ferramentas-utilizadas-em-nosso-projeto)


---
### 1. Como funciona o Database Management (Banco de Dados) em DevOps?
Se trata da aplicação de um conjunto de práticas, ferramentas e processos que integram o gerenciamento de banco de dados ao fluxo do DevOps. 
Se resume em:
* Controle de versão de esquemas e scripts SQL;
* Automatização de criação, alteração e versionamento do banco;
* Testes automatizados com base em dados e scripts;
* Migrações seguras e consistentes em todos os ambientes (dev, QA, prod)

---
## 2. Ferramenta utilizada: Flyway?

### 2.1 O que é o Flyway? 
Se trata de uma ferramenta com foco em versionamento e no gerenciamento de migrações de Banco de Dados.

### 2.2 Quando usar o Flyway?

É recomendável o uso do Flyway para os seguintes casos (quando é necessário):
- Ter um bom controle sobre as versões do Banco de Dados, ou seja, ter um rastreabilidade sobre as alterações;
- Quando se tem equipes de desenvolvimento que compartilham o mesmo Banco;
- Para casos de automação de Deploys de Banco de Dados em ambientes controlados;

### 2.3 Principais características

- Possui suporte a múltiplos bancos de dados(MySQL, PostegreSQL, Oracle, SQL Server, etc);

- Scripts: utiliza scripts SQL (ou Java), que descrevem as mudanças a serem aplicadas no banco;

- Versionamento de migrações: os scripts recebem um número de versão e eles são aplicados nessa ordem;

- Se faz necessário uma padronização de nomenclatura dos scripts, para que o Flyway consiga diferenciar as versões e a sequência necessária para executá-los. Modelo de padronização utilizada:

<p align="center">
  <img src="https://github.com/user-attachments/assets/1cd92cd4-8ef8-4b28-b623-cc1cb19468d9" alt="image" />
</p>

    - Prefixo: neste caso o prefixo é a letra V (observe que está em letra maiúscula);

    - Versão: deve ser adicionado o número da versão referente aquele script, podendo ser separado por uso de "." ou até mesmo "_". 
        Exemplo prático (Prefixo + Versão): "V1.1" ou "V1_1";

    - Separador: após inserir as informações do Prefixo e da Versão, utilizamos o Separador, sendo aplicado o undercode 2 vezes(em sequência). 
        Exemplo prático (Prefixo + Versão + Separador):  "V1.1__" ou "V1_1__"
    
    - Descrição: deve ser inserido uma breve descrição do que está sendo alterado.
        Exemplo prático (Prefixo + Versão + Separador + Descrição + Sufixo (Extensão)):  
        "V1.0__create_user.sql" ou "V1_0__create_user.sql" 
        "V1.1__create_user.sql" ou "V1_1__create_user.sql"
     
**Fonte de consulta, imagem e demais informações:** [Medium - O que é Flyway e por que usá-lo com Java e Spring](https://medium.com/@perez_vitor/o-que-%C3%A9-flyway-e-por-que-usa-lo-com-java-e-spring-312219ebf840) 
     
**Observação:** essa padronização é personalizável de acordo com as necessidades do time, mas deve existir uma constância.

---
### 3. Como o Flyway foi implementado no nosso projeto?


---
### 4. Versões X Alterações

* Sprint 1: iniciamos com MySQL mas devido a incompatibilidade com o SonarQube, houve a necessidade de alterar e considerar o PostgreeSQL para o desenvolvimento do projeto.

* Sprint 2: Sem alterações significativas, apenas inclusões no Script (definido anteriormente na Sprint 1);

* Sprint 3: Alteração de senha para manter a segurança do nosso Banco de Dados.

---
### 5. Desafios, Soluções e Lições Aprendidas

---
### 6. Ferramentas Utilizadas em Nosso Projeto

- Linguagens: Java, SQL
- Bibliotecas: datetime, json
- Banco de Dados: PostegreSQL
- API:API REST TAIGA
- Dashboard: React
- DevOps: Jira + GitHub, Docker, CI/CD

---







Remover?????

### Estrutura de Versionamento

### Versão 1 

- Diagrama Estrela
- Script SQL
- [Back-End](https://github.com/new-ge/API_5SEM_BACK-END/releases/tag/release-1)
- [Front-End](https://github.com/new-ge/API_5SEM_FRONT-END/releases/tag/Release-1)


### Versão 2 

- Diagrama Estrela
- Script SQL
- [Back-End](https://github.com/new-ge/API_5SEM_BACK-END/releases/tag/release-2)
- [Front-End](https://github.com/new-ge/API_5SEM_FRONT-END/releases/tag/Release-2)


### Versão 3 


Os scripts de migração estão localizados em:


### XXXDescrever

</details>