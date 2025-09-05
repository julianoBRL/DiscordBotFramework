# Root image
FROM amazoncorretto:21

# Arguments
ENV JAVA_ARGS=""

# Dir
WORKDIR /app

VOLUME /app/plugins
VOLUME /app/configs

# Copys
COPY target/discord-bot-core-1.0.0-jar-with-dependencies.jar /app/app.jar
COPY src/main/resources/*.yml /app/configs/application.yml
COPY entrypoint.sh /app/entrypoint.sh

# Permissões
RUN chmod +x /app/entrypoint.sh

# Definindo o ENTRYPOINT para permitir argumentos adicionais
ENTRYPOINT ["/app/entrypoint.sh"]