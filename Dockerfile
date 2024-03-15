FROM azul/zulu-openjdk-alpine:17

WORKDIR /app

RUN addgroup --system javauser && adduser -S -s /usr/sbin/nologin -G javauser javauser

COPY target/gestaoreservas-0.0.1-SNAPSHOT.jar app.jar

RUN chown -R javauser:javauser .

USER javauser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]