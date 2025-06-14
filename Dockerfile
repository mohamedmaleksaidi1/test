# Utilise l'image officielle Java 21 JDK
FROM eclipse-temurin:21-jdk

# Crée un dossier pour ton app dans le conteneur
WORKDIR /app

# Copie ton fichier .jar (généré par Maven ou Gradle)
COPY target/*.jar app.jar

# Ouvre le port utilisé par Spring Boot (par défaut 8080)
EXPOSE 8080

# Commande pour démarrer ton application
ENTRYPOINT ["java", "-jar", "app.jar"]
