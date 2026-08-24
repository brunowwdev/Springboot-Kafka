# Spring Boot + Kafka

Projeto desenvolvido para estudar na prática **Spring Boot, microsserviços e Apache Kafka**, simulando parte de um sistema de compras.

A ideia principal do projeto é trabalhar com diferentes serviços, cada um responsável por uma parte do sistema, e utilizar o Kafka para fazer a comunicação assíncrona entre eles.

## Sobre o projeto

O projeto simula um sistema de compras dividido em alguns microsserviços:

* **Clientes** — gerenciamento dos clientes
* **Produtos** — gerenciamento dos produtos
* **Pedidos** — criação e processamento de pedidos
* **Faturamento** — processamento das informações de faturamento

Além das APIs REST, o projeto utiliza **Apache Kafka** para troca de eventos entre os serviços.

Um exemplo do fluxo é:

```text
Pedido
   │
   │ evento
   ▼
 Kafka
   │
   ▼
Faturamento
```

Dessa forma, o serviço de pedidos pode publicar um evento e o serviço de faturamento recebe e processa essa mensagem de forma independente.

## Tecnologias

* Java
* Spring Boot
* Spring Data JPA
* Spring Kafka
* Apache Kafka
* PostgreSQL
* Docker
* Docker Compose
* Maven
* Lombok

## Kafka

O Kafka é utilizado principalmente para a comunicação entre os microsserviços.

No serviço de pedidos, por exemplo, um evento pode ser publicado utilizando o `KafkaTemplate`:

```java
kafkaTemplate.send(topico, "dados", json);
```

Já o serviço consumidor utiliza `@KafkaListener` para receber a mensagem:

```java
@KafkaListener(topics = "${kafka.topico}")
public void consumir(String mensagem) {
    // processamento
}
```

Com isso, os serviços não precisam ficar diretamente acoplados uns aos outros para realizar esse tipo de comunicação.

## Estrutura

```text
Springboot-Kafka/
│
├── clientes/
│
├── produtos/
│
├── pedidos/
│
├── faturamento/
│
└── icompras-servicos/
```

Cada pasta representa uma parte do sistema e possui sua própria aplicação Spring Boot.

## Como executar

### Pré-requisitos

Você precisa ter instalado:

* Java
* Maven
* Docker
* Docker Compose

### 1. Clone o projeto

```bash
git clone https://github.com/brunowwdev/Springboot-Kafka.git
```

```bash
cd Springboot-Kafka
```

### 2. Suba os serviços do Docker

Entre na pasta de infraestrutura:

```bash
cd icompras-servicos
```

Depois execute:

```bash
docker compose up -d
```

Para verificar os containers:

```bash
docker ps
```

### 3. Execute os microsserviços

Os serviços podem ser executados individualmente pela IDE ou através do Maven:

```bash
mvn spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

## O que pratiquei neste projeto

Esse projeto foi criado principalmente para colocar em prática conceitos que são bastante utilizados no desenvolvimento backend:

* Criação de APIs REST com Spring Boot
* Arquitetura de microsserviços
* Comunicação síncrona utilizando REST
* Comunicação assíncrona utilizando Kafka
* Producer e Consumer
* Integração entre serviços
* Persistência com PostgreSQL
* Configuração de infraestrutura utilizando Docker
* Docker Compose
* Separação de responsabilidades entre serviços

**Tecnologias:** Java • Spring Boot • REST • Microsserviços • Kafka • Docker • PostgreSQL

[GitHub](https://github.com/brunowwdev)

[LinkedIn](https://www.linkedin.com/in/brunomanhaesalves/)
