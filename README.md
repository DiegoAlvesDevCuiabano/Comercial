# Sistema de Gestão Comercial - FATEC

Sistema de gerenciamento de eventos comerciais desenvolvido com Spring Boot 3.2.0 e Java 21.

## Tecnologias

- **Backend:** Spring Boot 3.2.0, Java 21
- **Segurança:** Spring Security com BCrypt
- **Banco de Dados:** MySQL 8.0
- **Frontend:** Thymeleaf, Bootstrap 5.2.3, jQuery
- **Build:** Maven
- **PDF:** iText 5.x

## Requisitos

- Java 21+
- MySQL 8.0+
- Maven 3.8+

## Instalação

### 1. Clonar repositório
```bash
git clone https://github.com/DiegoAlvesDevCuiabano/Comercial.git
cd Comercial
```

### 2. Configurar banco de dados

Criar database no MySQL:
```sql
CREATE DATABASE comercial_dev;
```

### 3. Configurar credenciais (opcional)

Por padrão usa `root/root`. Para customizar:

**Windows (PowerShell):**
```powershell
$env:DB_USER="seu_usuario"
$env:DB_PASSWORD="sua_senha"
```

**Linux/Mac:**
```bash
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

### 4. Rodar aplicação
```bash
./mvnw spring-boot:run
```

Acesse: `http://localhost:8080/comercial`

## Estrutura do Projeto

```
src/main/java/com/controle_comercial/
├── config/              # Configurações (Security, MVC)
├── controller/          # Controllers MVC
├── service/            # Lógica de negócio
├── repository/         # Acesso a dados (JPA)
├── model/
│   └── entity/        # Entidades JPA
├── exception/         # Custom exceptions e handlers
└── util/              # Utilitários (geração de PDF)
```

## Funcionalidades

- **Gestão de Clientes:** Cadastro, edição, listagem
- **Gestão de Serviços:** Controle de serviços oferecidos com preços
- **Gestão de Locais:** Cadastro de locais para eventos (tipo, capacidade)
- **Gestão de Eventos:**
  - Eventos com múltiplos dias
  - Múltiplos serviços por evento
  - Cálculo automático de totais
  - Sistema de descontos (valor e percentual)
- **Relatórios PDF:**
  - Relatório de eventos por período
  - Relatório completo de entidades

## Arquitetura

### Modelo de Dados

**Evento** (central)
- Datas (início/fim) e horários
- Cliente e Local (relacionamento N:1)
- Serviços (relacionamento N:N via EventoServico)
- Valores e descontos

**EventoServico** (join table)
- Composite key (eventoId + servicoId)
- Campo quantidade
- Usado para calcular totais

### Segurança

- Autenticação form-based
- Senhas com BCrypt
- CSRF protection habilitado
- Todos endpoints requerem autenticação (exceto login e assets)

### Exception Handling

Sistema robusto de tratamento de erros:
- `EntityNotFoundException` - Entidade não encontrada
- `ClienteNotFoundException, ServicoNotFoundException, LocalNotFoundException`
- `ValidationException` - Erros de validação
- `GlobalExceptionHandler` - Tratamento centralizado

## Desenvolvimento

### Build
```bash
./mvnw clean package
```

### Testes
```bash
./mvnw test
```

### Build sem testes
```bash
./mvnw clean package -DskipTests
```

## Boas Práticas Implementadas

- ✅ Custom exceptions tipadas
- ✅ Global exception handler
- ✅ Validação de dados
- ✅ Transações apropriadas (`@Transactional`)
- ✅ Queries otimizadas (filtro no banco, não em memória)
- ✅ Credenciais externalizadas
- ✅ Logging apropriado
- ✅ Commits atômicos (Conventional Commits)

## Fluxo de Trabalho Git

### Branches principais
- `main` - Produção
- `develop` - Desenvolvimento

### Criar feature
```bash
git checkout develop
git pull origin develop
git checkout -b feat/minha-feature

# Trabalhar e commitar
git add .
git commit -m "feat: descrição da mudança"

# Merge
git checkout develop
git merge feat/minha-feature --no-ff
git push origin develop
git branch -d feat/minha-feature
```

## Geração de Senha para Usuário

```bash
# Via IDE (IntelliJ):
# Run PasswordHashGenerator com argumentos: "sua_senha"

# Atualizar no banco:
UPDATE Usuario SET senha = '<hash_gerado>' WHERE usuario = 'admin';
```

## Troubleshooting

### Erro de conexão com MySQL
- Verificar se MySQL está rodando
- Conferir credenciais em `application.properties`
- Verificar se database existe

### Erro de compilação
```bash
./mvnw clean compile
```

### Limpar dependências
```bash
./mvnw dependency:purge-local-repository
```

## Licença

Este projeto foi desenvolvido como parte do estágio na FATEC.

## Contato

Diego Alves - [GitHub](https://github.com/DiegoAlvesDevCuiabano)
