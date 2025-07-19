# Многопоточность глазами разработчика

[Ссылка на репозиторий с кодом](https://github.com/proselytear/javaconcurrency) </br>
[Ссылка на видео-урок от Евгения Сулейманова](https://youtu.be/m-7EljqdxpA)





### Немного информации о дереве зависимостей
Чтобы посмотреть дерево зависимостей в проекте на Gradle для определенных модулей проекта выполните команду в терминале:

```bash
./gradlew :tkf-contract:dependencies --configuration runtimeClasspath
```

```bash
./gradlew :bonds-reference:dependencies --configuration runtimeClasspath
```
Это покажет дерево зависимостей и поможет понять, какие библиотеки тянут разные версии.

### Дополнительные советы
#### Gradle 8.7 и выше поддерживает `BOM` и `resolutionStrategy.force` — используйте их.
#### Если используете Spring Boot BOM, убедитесь, что версии gRPC и protobuf не конфликтуют с ним.


### Пересобрать проект с очисткой и обновлением зависимостей для обновления кэша:
```bash
./gradlew clean build --refresh-dependencies
```

### Если в вашем проекте используется Armeria:
https://armeria.dev/docs/advanced-spring-boot-integration/ </br>
https://armeria.dev/docs/ </br>
то встроенный сервис документации (доступный по `/docs`) — это именно функционал Armeria, который позволяет 
автоматически показывать доступные gRPC и HTTP эндпоинты с возможностью тестирования прямо из браузера.

Пример из логов запуска сервиса:

    17:01:45.759 [armeria-boss-http-*:8080] INFO  com.linecorp.armeria.server.Server - Serving HTTP at /[0:0:0:0:0:0:0:0]:8080 - http://127.0.0.1:8080/
    17:01:45.761 [main] INFO  ru.datafeed.rest.WebServer - Server has been started. Serving DocService at http://127.0.0.1:8080/docs

#### Environment variables (для этого проекта):
```shell
GRPC_THREADS=1;PORT=8080;URL=sandbox-invest-public-api.tinkoff.ru:443;WEB_THREADS=1;TKF_SENDBOX=qw2123qw
```

### Документирование `REST/gRPC` сервисов в Spring

В Spring Boot *аналогичной встроенной возможности "из коробки" нет*, но есть популярные решения для автоматической генерации документации:

| Инструмент              | Описание                                                                                    |
|------------------------|----------------------------------------------------------------------------------------------|
| **Springdoc OpenAPI**  | Автоматически генерирует документацию OpenAPI (Swagger UI) для REST-контроллеров Spring MVC и WebFlux. Позволяет видеть эндпоинты и модели данных в удобном веб-интерфейсе. |
| **Swagger UI**          | Веб-интерфейс для визуализации и тестирования API, часто используется вместе с Springdoc OpenAPI. |
| **gRPC Spring Boot Starter + grpc-spring-boot-starter** | Для gRPC в Spring есть отдельные стартеры, но полноценной UI-документации, как в Armeria, обычно нет. Можно интегрировать с внешними инструментами, например, grpcui. |

### Как получить документацию в Spring Boot

- Для `REST API` используйте `springdoc-openapi`:

```kotlin
dependencies {
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
}
```

После запуска приложение будет доступно по URL:

```curl
http://localhost:8080/swagger-ui.html
```

где можно увидеть все REST эндпоинты и модели.

- Для `gRPC` в Spring Boot документация обычно не генерируется автоматически в UI, но можно использовать сторонние инструменты, например:

   - [grpcui](https://github.com/fullstorydev/grpcui) — веб-интерфейс для тестирования gRPC.
   - Интеграция с Armeria, если хотите получить UI-документацию, как в этом проекте.

### Можно ли использовать Armeria Docs вместе со Spring?

Да, `Armeria` отлично интегрируется со `Spring Boot` и может выступать как сервер для `gRPC` и `REST`, при этом предоставляя свой сервис документации 
по адресу `/internal/docs` (или `/docs`, если настроено) [[**1**](https://armeria.dev/docs/advanced-spring-boot-integration/)]. 
Это позволяет иметь удобный UI для всех сервисов, запущенных через `Armeria`, даже если часть логики реализована на Spring.

### Итог

- **Armeria предоставляет мощный встроенный сервис документации для gRPC и REST**, который вы уже используете.
- В **чистом Spring Boot** для REST лучше использовать `springdoc-openapi` + Swagger UI.
- Для gRPC в Spring Boot UI-документация стандартно не генерируется, но можно использовать сторонние инструменты или интегрировать Armeria.
- Если хотите единый UI для всех сервисов, рассмотрите использование Armeria вместе со Spring Boot, как описано в документации Armeria [[**1**](https://armeria.dev/docs/advanced-spring-boot-integration/)].


[1] https://armeria.dev/docs/advanced-spring-boot-integration/ </br>
[2] https://armeria.dev/docs/ </br>
[3] https://habr.com/ru/companies/tbank/articles/864552/ </br>
[4] https://www.reddit.com/r/java/comments/1gqiinz/java_spring_and_grpc/?tl=ru </br>
[5] https://www.reddit.com/r/java/comments/164b7up/introduction_to_grpc_with_spring_boot_piotrs/?tl=ru </br>
[6] https://habr.com/ru/companies/jugru/articles/725848/ </br>
[7] https://devmark.ru/article/spring-config-formats-comparison </br>
[8] https://t.me/s/javalib?before=5965 </br>
[9] https://javaswag.github.io </br>
[10] https://www.youtube.com/watch?v=yITGWkwtPBY </br>


