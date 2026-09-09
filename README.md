# iCompras --- Spring Boot + Kafka

Projeto desenvolvido para estudar, na prática, **arquitetura de
microsserviços com Spring Boot e Apache Kafka**, simulando o fluxo de
uma plataforma de compras.

A aplicação é dividida em serviços independentes que se comunicam por
**REST** e **eventos Kafka**, passando pelo ciclo de criação do pedido,
pagamento, faturamento, geração da nota fiscal, armazenamento do
documento e envio para logística.

## 🚀 Visão geral

O projeto possui os seguintes microsserviços:

-   **Clientes** --- cadastro e consulta de clientes.
-   **Produtos** --- cadastro e consulta de produtos.
-   **Pedidos** --- criação de pedidos, consulta e processamento de
    pagamentos.
-   **Faturamento** --- recebe pedidos pagos, gera a nota fiscal em PDF
    e armazena o arquivo no MinIO.
-   **Logística** --- recebe o pedido faturado, prepara o envio e gera
    um código de rastreio.

### Fluxo principal

``` text
                  ┌─────────────┐
                  │   Clientes  │
                  │    :8082    │
                  └──────┬──────┘
                         │ REST
                         │
                  ┌──────▼──────┐
                  │   Pedidos   │
                  │    :8083    │
                  └──────┬──────┘
                         │
                    Pedido pago
                         │
                         ▼
                 ┌───────────────┐
                 │     Kafka     │
                 │ pedidos-pagos │
                 └───────┬───────┘
                         │
                         ▼
                 ┌───────────────┐
                 │  Faturamento  │
                 │     :8084     │
                 └───────┬───────┘
                         │
                  JasperReports
                         │
                         ▼
                 ┌───────────────┐
                 │     MinIO     │
                 │ armazenamento │
                 │    da NF      │
                 └───────┬───────┘
                         │
                    URL da NF
                         │
                         ▼
                 ┌───────────────┐
                 │     Kafka     │
                 │pedidos-faturados
                 └───────┬───────┘
                         │
                         ▼
                 ┌───────────────┐
                 │   Logística   │
                 │ preparação do │
                 │     envio     │
                 └───────┬───────┘
                         │
                         ▼
                 pedidos-enviados
```

## 🧩 Como os serviços se comunicam

O projeto utiliza dois tipos principais de comunicação:

### Comunicação síncrona --- REST

O serviço de **Pedidos** utiliza **Spring Cloud OpenFeign** para
consultar informações de outros serviços.

Por exemplo:

``` text
Pedidos ──REST──> Clientes
Pedidos ──REST──> Produtos
```

Assim, ao consultar um pedido, o serviço consegue buscar os dados do
cliente e dos produtos relacionados.

### Comunicação assíncrona --- Kafka

Para eventos do fluxo do pedido, o projeto utiliza **Apache Kafka**.

Exemplo:

``` text
Pedidos
   │
   │ publica evento
   ▼
Kafka: icompras.pedidos-pagos
   │
   ▼
Faturamento
```

O Faturamento consome o evento utilizando `@KafkaListener`, processa a
mensagem e gera a nota fiscal.

Depois:

``` text
Faturamento
   │
   │ publica evento
   ▼
Kafka: icompras.pedidos-faturados
   │
   ▼
Logística
```

A Logística processa o pedido e publica o evento de envio:

``` text
Kafka: icompras.pedidos-enviados
```

## 📦 Tecnologias utilizadas

-   **Java 21**
-   **Spring Boot**
-   **Spring Web**
-   **Spring Data JPA**
-   **Spring Cloud OpenFeign**
-   **Spring Kafka**
-   **Apache Kafka**
-   **PostgreSQL**
-   **Docker / Docker Compose**
-   **Maven**
-   **Lombok**
-   **MapStruct**
-   **JasperReports / JasperSoft**
-   **MinIO**
-   **Kafka UI**

## 📁 Estrutura do projeto

``` text
SpringBoot-Kafka/
│
├── clientes/
│   └── Microsserviço de clientes
│
├── produtos/
│   └── Microsserviço de produtos
│
├── pedidos/
│   └── Microsserviço responsável pelos pedidos
│
├── faturamento/
│   └── Faturamento + geração da nota fiscal
│
├── logistica/
│   └── Processamento do envio
│
├── icompras-servicos/
│   ├── database/
│   │   └── PostgreSQL
│   │
│   ├── broker/
│   │   └── Kafka + ZooKeeper + Kafka UI
│   │
│   └── bucket/
│       └── MinIO
│
└── README.md
```

## 🗄️ Banco de dados

O projeto utiliza **PostgreSQL** com bancos separados para os principais
serviços:

``` text
icomprasclientes
icomprasprodutos
icompraspedidos
```

O arquivo responsável pela estrutura inicial está em:

``` text
icompras-servicos/database/schema.sql
```

Ele cria os bancos e as tabelas necessárias para clientes, produtos e
pedidos.

O PostgreSQL é disponibilizado pelo Docker na porta:

``` text
localhost:5555
```

## 📨 Tópicos Kafka

Os principais tópicos utilizados são:

  ------------------------------------------------------------------------------------
  Tópico                         Produtor          Consumidor        Finalidade
  ------------------------------ ----------------- ----------------- -----------------
  `icompras.pedidos-pagos`       Pedidos           Faturamento       Informa que o
                                                                     pedido foi pago

  `icompras.pedidos-faturados`   Faturamento       Logística         Informa que a
                                                                     nota fiscal foi
                                                                     gerada e o pedido
                                                                     foi faturado

  `icompras.pedidos-enviados`    Logística         Pedidos           Informa que o
                                                                     pedido foi
                                                                     enviado
  ------------------------------------------------------------------------------------

O projeto também possui **Kafka UI** para visualizar tópicos, mensagens
e consumidores.

A interface fica disponível em:

``` text
http://localhost:8090
```

## 🧾 Geração da nota fiscal

Quando o serviço de **Faturamento** recebe um pedido pago:

1.  O evento é consumido pelo Kafka.
2.  Os dados do pedido são convertidos para o modelo utilizado pelo
    faturamento.
3.  O **JasperReports** utiliza o arquivo:

``` text
faturamento/src/main/resources/reports/nota-fiscal.jrxml
```

4.  O relatório é preenchido com os dados do cliente, pedido e itens.
5.  O relatório é exportado para **PDF**.
6.  O PDF é enviado para o **MinIO**.
7.  É gerada uma URL temporária para o arquivo.
8.  A URL da nota fiscal é enviada para o próximo serviço através do
    Kafka.

O projeto também possui o arquivo de imagem utilizado no relatório:

``` text
faturamento/src/main/resources/reports/logo.png
```

## 🪣 Armazenamento com MinIO

O MinIO funciona como armazenamento de objetos para os arquivos gerados
pelo sistema.

Neste projeto, ele é utilizado principalmente para armazenar as notas
fiscais em PDF.

Portas utilizadas:

``` text
9000 → API do MinIO
9001 → Console web
```

Console:

``` text
http://localhost:9001
```

O serviço de faturamento também disponibiliza endpoints para upload e
obtenção de URL de arquivos pelo bucket.

## 🔌 Principais endpoints

### Clientes --- `8082`

Criar cliente:

``` http
POST http://localhost:8082/clientes
```

Consultar cliente:

``` http
GET http://localhost:8082/clientes/{codigo}
```

Excluir cliente:

``` http
DELETE http://localhost:8082/clientes/{codigo}
```

### Produtos --- `8081`

Criar produto:

``` http
POST http://localhost:8081/produtos
```

Consultar produto:

``` http
GET http://localhost:8081/produtos/{codigo}
```

Excluir produto:

``` http
DELETE http://localhost:8081/produtos/{codigo}
```

### Pedidos --- `8083`

Criar pedido:

``` http
POST http://localhost:8083/pedidos
```

Adicionar novo pagamento:

``` http
POST http://localhost:8083/pedidos/pagamentos
```

Consultar pedido:

``` http
GET http://localhost:8083/pedidos/{codigo}
```

Callback de pagamento:

``` http
POST http://localhost:8083/pedidos/callback-pagamentos
```

### Faturamento --- `8084`

Upload de arquivo:

``` http
POST http://localhost:8084/bucket
```

Obter URL de um arquivo:

``` http
GET http://localhost:8084/bucket?filename={nomeArquivo}
```

## 🐳 Executando o projeto

### Pré-requisitos

Instale:

-   Java 21
-   Maven
-   Docker
-   Docker Compose
-   Git

### 1. Clone o projeto

``` bash
git clone https://github.com/brunowwdev/Springboot-Kafka.git
cd Springboot-Kafka
```

### 2. Suba o PostgreSQL

Entre na pasta:

``` bash
cd icompras-servicos/database
```

Execute:

``` bash
docker compose up -d
```

### 3. Crie a estrutura do banco

Execute o arquivo:

``` text
icompras-servicos/database/schema.sql
```

Você pode utilizar o **DBeaver**, `psql` ou outra ferramenta de
gerenciamento PostgreSQL.

### 4. Suba o Kafka

Entre em:

``` bash
cd ../broker
```

Execute:

``` bash
docker compose up -d
```

Confira os containers:

``` bash
docker ps
```

Kafka UI:

``` text
http://localhost:8090
```

### 5. Suba o MinIO

Entre em:

``` bash
cd ../bucket
```

Execute:

``` bash
docker compose up -d
```

Console:

``` text
http://localhost:9001
```

### 6. Execute os microsserviços

Cada serviço é uma aplicação Spring Boot independente.

Por exemplo:

``` bash
cd clientes
./mvnw spring-boot:run
```

No Windows:

``` bash
mvnw.cmd spring-boot:run
```

Repita o processo para:

``` text
clientes
produtos
pedidos
faturamento
logistica
```

## ⚙️ Portas

  Serviço             Porta
  --------------- ---------
  Produtos           `8081`
  Clientes           `8082`
  Pedidos            `8083`
  Faturamento        `8084`
  PostgreSQL         `5555`
  Kafka             `29092`
  Kafka UI           `8090`
  MinIO API          `9000`
  MinIO Console      `9001`
  ZooKeeper         `22181`

## 🔄 Exemplo de fluxo completo

Um exemplo simplificado do processamento é:

``` text
1. Cliente cria um pedido
        ↓
2. Pedidos valida e salva o pedido
        ↓
3. Sistema solicita o pagamento
        ↓
4. Pagamento é confirmado
        ↓
5. Pedidos publica "pedidos-pagos"
        ↓
6. Faturamento recebe o evento
        ↓
7. JasperReports gera a nota fiscal PDF
        ↓
8. MinIO armazena o PDF
        ↓
9. Faturamento publica "pedidos-faturados"
        ↓
10. Logística recebe o evento
        ↓
11. Código de rastreio é gerado
        ↓
12. Logística publica "pedidos-enviados"
```

Esse fluxo demonstra na prática como uma arquitetura de microsserviços
pode combinar **REST para consultas diretas** e **Kafka para
processamento orientado a eventos**.

## 🎯 O que foi praticado

Este projeto foi desenvolvido com foco em prática e integração dos
principais conceitos de backend:

-   Construção de microsserviços com Spring Boot.
-   Criação de APIs REST.
-   Comunicação entre serviços com OpenFeign.
-   Comunicação assíncrona com Apache Kafka.
-   Implementação de Producers e Consumers.
-   Uso de tópicos e grupos de consumidores.
-   Persistência com Spring Data JPA e PostgreSQL.
-   Configuração de infraestrutura com Docker Compose.
-   Geração de documentos PDF com JasperReports.
-   Armazenamento de arquivos com MinIO.
-   Geração de URLs temporárias para arquivos armazenados.
-   Separação de responsabilidades entre os microsserviços.

## 📚 Objetivo

O objetivo principal deste projeto foi aprofundar conhecimentos em
**Java, Spring Boot, microsserviços e Apache Kafka**, colocando em
prática um fluxo completo de negócio e explorando como diferentes
serviços podem trabalhar de forma independente e orientada a eventos.

------------------------------------------------------------------------

**Desenvolvido por Bruno Manhães Alves**

[LinkedIn](https://www.linkedin.com/in/brunomanhaesalves/)
