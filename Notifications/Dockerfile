# Usamos una imagen de JDK 21 solo con el entorno de ejecución (JRE)
# para reducir el tamaño de la imagen y ejecutar el JAR de la aplicación Spring Boot.
FROM eclipse-temurin:21-jre

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor.
COPY target/notifications-0.0.1-SNAPSHOT.jar app.jar

# Expone el puerto en el que tu aplicación Spring Boot escucha
EXPOSE 8080

# Comando para ejecutar la aplicación Spring Boot
ENTRYPOINT ["java","-jar","app.jar"]
