# Etapa 1: Compilar a aplicação
FROM maven:3.9.8-eclipse-temurin-21 AS build

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o arquivo pom.xml primeiro para aproveitar o cache do Docker
COPY pom.xml ./
COPY src ./src

# Compila a aplicação, ignorando os testes para acelerar o build
RUN mvn clean install -U -DskipTests

# Etapa 2: Criar a imagem final para execução
FROM eclipse-temurin:21-jre-alpine

# Define o diretório de trabalho dentro do contêiner
WORKDIR /app

# Copia o jar compilado da etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8084

# Define o comando padrão para rodar a aplicação
CMD ["java", "-jar", "app.jar"]
