# 🧊 Database Management (DevOps) 

<details>


## 📑 Sumário

1. 📋 [Como funciona o Database Management (Banco de Dados) em DevOps?](#como-funciona-o-Database-Management-(Banco-de-Dados)-em-DevOps)
 
2. ⚙️ [Como é Implementado no Nosso Projeto?](#como-é-implementado-no-nosso-projeto)
3. 🛠️ [Ferramentas Utilizadas](#ferramentas-utilizadas)
4. 🧱 [Estrutura de Versionamento]()
5. 🚧 [XXXDescrever](#XXXDescrever)



## 📋 Como funciona o Database Management (Banco de Dados) em DevOps?
Se trata da aplicação de um conjunto de práticas, ferramentas e processos que juntos integram o gerenciamento de banco de dados ao fluxo do DevOps. Se resumo em:
* Controle de versão de esquemas e scripts SQL;
* Automatização de criação, alteração e versionamento do banco;
* Testes automatizados com base em dados e scripts;
- Migrações seguras e consistentes em todos os ambientes (dev, QA, prod)

## ⚙️ Como é implementado no nosso projeto?

### Objetivo 


### Principais alterações

* Sprint 1: iniciamos com MySQL mas devido a incompatibilidade com o SonarQube, houve a necessidade de alterar e considerar o PostgreeSQL para o desenvolvimento do projeto.


* Sprint 2: Sem alterações significativas, apenas inclusões no Script (definido anteriormente na Sprint 1);

* Sprint 3: Alteração de senha para manter a segurança do nosso Banco de Dados.


### Desafios e Soluções



## 🛠️Ferramentas Utilizadas

- Linguagens: Java, SQL
- Bibliotecas: datetime, json
- Banco de Dados: PostegreSQL
- API:API REST TAIGA
- Dashboard: React
- DevOps: Jira + GitHub, Docker, CI/CD


## 🧱 Estrutura de Versionamento

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





- Os scripts de migração estão localizados em:
    src/main/resources/db/migration

📁 src
└── 📁 main
    └── 📁 resources
        └── 📁 db
            └── 📁 migration
                ├── 📄 V1__create_users_table.sql
                ├── 📄 V2__add_email_column.sql
                ├── 📄 V3__create_address_table.sql
                ├── 📄 V4__insert_initial_roles.sql
                └── 📄 V5__update_user_constraints.sql



<pre> ```text src └── main └── resources └── db └── migration ├── V1__create_users_table.sql ├── V2__add_column_email.sql └── V3__rename_table.sql ``` </pre>


## 🚧 XXXDescrever












Futuro (como possibilidade de uso):
🔄 Pipeline de Migração	🔄	Para uma futura seção sobre o ciclo ou automação
🧪 Testes de Migração	🧪	Caso você vá incluir validação/testes
🚨 Boas Práticas e Cuidados	🚨 ou ✅	Dependendo se for mais sobre alertas ou boas práticas
📚 Referências	📚	Ideal para seção de links e leitura adicional
👨‍💻 Autor	👨‍💻 / 👩‍💻	Personalize conforme o autor