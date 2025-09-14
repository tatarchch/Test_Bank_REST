# Система управления банковскими картами

Restful API для управления картами

# Аутентификация и авторизация:
Происходит с помощью Spring Security и JWT(jjwt).

Вызвав метод "authenticateUser" контроллера "AuthentificationController"
возвращается токен, который используется для дальнейшего доступа к методам.
Это происходит в методе "authentication" сервиса "AuthenticationService" путём
создания с помощью builder объекта org.springframework.security.core.userdetails.User,
который после создания используется для формирования объекта org.springframework.security.authentication.UsernamePasswordAuthenticationToken.

Разграничение доступа по ролям описано в классе "WebSecurityConfig" с помощью org.springframework.security.web.SecurityFilterChain.
Также используются несколько фильтров, а именно:

com.example.bankcards.security.jwt.AuthEntryPointJwt, который является наследником класса org.springframework.security.web.AuthenticationEntryPoint.
Он формирует ответ в случае обращения неавторизованных пользователей к ендпоинтам, отличным от:
"/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/actuator/health", "/api/v1/user/createNew", "/api/v1/authentification/signIn");

com.example.bankcards.security.jwt.AccessDeniedHandlerJwt, который является наследником класса org.springframework.security.web.access.AccessDeniedHandler.
Он формирует ответ в случае обращения пользователей с правами 'USER' к ендпоинтам, для которых нужен уровень прав 'ADMIN';

com.example.bankcards.security.jwt.AuthTokenFilter, который является наследником класса org.springframework.web.filter.OncePerRequestFilter.
Он используется для валидации токена и присвоении прав.

Таким образом, 
к ендпоинтам "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/actuator/health", "/api/v1/user/createNew", "/api/v1/authentification/signIn") имеют доступ все.

к ендпоинтам 
"/api/v1/card/getByIdWithMask/{cardId}",
"/api/v1/card/getByIdAndOwnerIdWithMask/{cardId}/{ownerId}",
"/api/v1/card/deleteById/{cardId}",
"/api/v1/card/getAllByOwnerId/{ownerId}", "/api/v1/card/transfer",
"/api/v1/card/getBalance/{cardId}" имеют доступ пользователи с правами 'USER' и 'ADMIN'.

ко все остальным ендпоинтам имеют доступ только пользователи с правами 'ADMIN'

Шаги по запуску

1. Клонирование репозитория (если необходимо)

Если проект находится в Git-репозитории, клонируйте его:
~bash~
-- git clone https://github.com/tatarchch/Test_Bank_REST.git
-- cd <папка проекта>

2. Подготовка файла переменных окружения
Создайте файл .env в корневой директории проекта и заполните его по примеру:
~bash~
TZ=Europe/Moscow
DB_NAME=your_database_name
DB_USER=your_database_user
DB_PASSWORD=your_strong_password
Замените значения на свои.

3. Сборка и запуск контейнеров
   Запустите сборку и запуск контейнеров с помощью Docker Compose.
   Для этого зайдите в корневую папку(там должен быть файл docker-compose)
~bash~
docker-compose up -d

4. Проверка работоспособности
   После запуска проверьте, что оба сервиса работают:

Приложение: откройте в браузере http://localhost:8080/actuator/health

Для тестирования Swagger откройте в браузере http://localhost:8080/swagger-ui/index.html


5. Остановка и удаление контейнеров
   Чтобы остановить и удалить контейнеры, выполните:
~bash~
docker-compose down
Если вы хотите также удалить volumes (включая данные базы данных), используйте:
~bash~
docker-compose down -v

6. Мониторинг логов
   Для просмотра логов приложения:

~bash~
docker-compose logs app

Для просмотра логов базы данных:
~bash~
docker-compose logs db

Для просмотра логов в реальном времени используйте флаг -f:
~bash~
docker-compose logs -f app