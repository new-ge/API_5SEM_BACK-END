## Database Management 

<details>

### Sumário


1. [Como funciona o Database Management em DevOps?](#como-funciona-o-database-management-em-devops)
2. [Ferramenta utilizada: Flyway](#ferramenta-utilizada-flyway)
   - [2.1 O que é o Flyway?](#21-o-que-é-o-flyway)
   - [2.2 Quando usar o Flyway?](#22-quando-usar-o-flyway)
   - [2.3 Principais características](#23-principais-características)
3. [Como o Flyway foi implementado no nosso projeto?](#como-o-flyway-foi-implementado-no-nosso-projeto)
    - [3.1 Adicionado no pom.xml](#31-adicionado-no-pom.xml)
    - [3.2 Adicionado no application.yml](#32-adicionado-no-application.yml)
    - [3.3 Adicionado no compose.yaml](#33-adicionado-no-compose.yaml)
    - [3.4 Visualização no DBeaver](#34-visualização-no-DBeaver)
4. [Versões X Alterações](#versões-x-alterações)
    - [4.1 Versão 1 e suas alterações](#41-versão-1-e-suas-alterações)
    - [4.2 Versão 2 e suas alterações](#42-versão-2-e-suas-alterações)
    - [4.3 Versão 3 e suas alterações](#43-versão-3-e-suas-alterações)
5. [Ferramentas Utilizadas em Nosso Projeto](#ferramentas-utilizadas-em-nosso-projeto)


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

### 3.1 Adicionado no pom.xml

``` xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
    <version>11.5.0</version>
    <scope>runtime</scope>
</dependency>
```

### 3.2 Adicionado no application.yml

```yml
spring:
    application:
        name: vision
    datasource:
        url: ${DB_URL}
        username: ${DB_USERNAME}
        password: ${DB_PASSWORD}
        driver-class-name: org.postgresql.Driver
    jpa:
        hibernate:
            ddl-auto: update
        show-sql: true
    flyway:
        validate-on-migrate: true
        enabled: true
        baseline-on-migrate: true
    springdoc:
    api-docs:
      enabled: true
    swagger-ui:
      enabled: true
      url: /v3/api-docs
      operationsSorter: alpha
      tagsSorter: alpha

server:
  error:
    include-message: always
```

### 3.3 Adicionado no compose.yaml

```yaml
services:
  postgresql:
    image: postgres:17.4-alpine3.21
    container_name: vision-back-postgres
    environment:
      POSTGRES_USER: ${DB_USER}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: ${DB_DATABASE}
      PGDATA: /data/postgres
    ports:
      - '5433:5432'
    volumes:
      - postgresql:/var/lib/postgresql
      - ./vision_back/src/main/java/com/vision_back/vision_back/resources:/docker-entrypoint-initdb.d
    networks:
      - qtsw-network
      
  sonarqube:
    image: sonarqube:community
    container_name: vision-back-sonarqube
    depends_on:
      - postgresql
    environment:
      SONAR_JDBC_URL: ${SONAR_URL}
      SONAR_JDBC_USERNAME: ${SONAR_USER}
      SONAR_JDBC_PASSWORD: ${SONAR_PASSWORD}
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_extensions:/opt/sonarqube/extensions
      - sonarqube_logs:/opt/sonarqube/logs
    ports:
      - "9001:9000"
    networks:
      - qtsw-network

volumes:
  postgresql:
  sonarqube_data:
  sonarqube_extensions:
  sonarqube_logs:

networks: 
  qtsw-network:
    driver: bridge
```

### 3.4 Visualização no DBeaver

<p align="center">
  <img src="https://github.com/user-attachments/assets/70c4300d-8441-4ecf-8684-cf368eaf2b8b" alt="image" />
</p>

<p align="center">
  <img src="https://github.com/user-attachments/assets/00188fd5-d8eb-48ca-b803-402a18a76d86" alt="image" />
</p>

---
### 4. Versões X Alterações


### 4.1 Versão 1 e suas alterações: 

**- flyway_schema_history:**

<p align="center">
  <img src="https://github.com/user-attachments/assets/2bc2519a-a363-4685-861a-fcb8ff9e0deb" alt="image" />
</p>

**Alterações:** 
- Iniciamos com o objetivo de utilizar o MySQL mas devido a incompatibilidade com o SonarQube, houve a necessidade de alterar e considerar o PostgreeSQL para o desenvolvimento do projeto.
- Houve apenas a criação inicial das tabelas

- [Acesso aos Scripts](https://github.com/new-ge/API_5SEM_BACK-END/tree/Sprint-1/vision_back/src/main/resources/db/migration)

### 4.2 Versão 2 e suas alterações:

**- flyway_schema_history:**
<p align="center">
  <img src="https://github.com/user-attachments/assets/952d5bf5-7322-46a3-a7f5-4ec4209372a6" alt="image" />
</p>


**Alterações:** 
- Seguem as alterações que ocorreram da Versão 1 para a Versão 2:

| Versão | Tabela                | Alterações                                                                                       |
|--------|-----------------------|--------------------------------------------------------------------------------------------------|
| V2     | `project`             | Adição de `UNIQUE` ao campo `project_code`                                                       |
| V3     | `milestone`           | Nova tabela vinculada ao projeto (V3__create_table_period.sql)                                   | 
| V4     | `stats`               | Mudança para `stats_name`, adição de `UNIQUE` em `stats_code`                                    |
| V5     | `task`                | Primeira criação da tabela de tarefas com `task_code`                                            |
| V6     | `usr`                 | Recriação com `usr_role` como `VARCHAR[]` + `is_logged_in`                                       |
| V7     | `task_status_history` | Novo relacionamento entre `usr_code` e `task_code`, com `UNIQUE` constraint                      |
| V8     | `usr_tag`             | Criação com `tag_name`, FKs para `project`, `task`, `usr`                                        |
| V9     | `usr_tag`             | Sem alterações                                                                                   |
| V10    | `usr_task`            | Mudança para *_code, inclusão de `role_code`, remoção de `period_id`, nova `UNIQUE` constraint   |                                                                         


- [Acesso aos Scripts](https://github.com/new-ge/API_5SEM_BACK-END/tree/Sprint-2/vision_back/src/main/resources/db/migration)

### 4.3 Versão 3 e suas alterações:
**- flyway_schema_history:**
<p align="center">
  <img src="https://github.com/user-attachments/assets/0861e829-b0dd-4914-b5ca-2fb4559c925d" alt="image" />
</p>

**Alterações:** 
- Troca de senha para manter a segurança do nosso Banco de Dados.

- Seguem as alterações que ocorreram da Versão 2 para a Versão 3:

| Versão | Tabela                | Alterações                                                                                       |
|--------|-----------------------|--------------------------------------------------------------------------------------------------|
| V2     | `project`             | Sem alterações                                                                                   |                                                                  
| V3     | `milestone`           | Sem alterações                                                                                   |
| V4     | `stats`               | Estrutura mantida                                                                                |
| V5     | `usr`                 | Versão final com `usr_code UNIQUE`, `usr_role` como `VARCHAR[]`, `is_logged_in`                  |
| V6     | `task`                | Inclusão de FK para `milestone`                                                                  |
| V7     | `task_status_history` | Inclusão de `project_code`, `milestone_code`, novo `UNIQUE` para rastreabilidade                 |
| V8     | `usr_tag`             | Inclusão de `milestone_code` como nova FK                                                        |
| V9     | `role`                | Criação da nova tabela com `role_code`, `role_name`, `project_code`                              |
| V10    | `usr_task`            | Sem alterações                                                                                   |                                                              |

- [Acesso aos Scripts](https://github.com/new-ge/API_5SEM_BACK-END/tree/Sprint-3/vision_back/src/main/resources/db/migration)

---
### 5. Ferramentas Utilizadas em Nosso Projeto

- Linguagens: Java, SQL
- Bibliotecas: datetime, json
- Banco de Dados: PostegreSQL
- API:API REST TAIGA
- Dashboard: React
- DevOps: Jira + GitHub, Docker, CI/CD

---