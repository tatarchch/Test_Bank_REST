# Система управления банковскими картами

Restful API для управления картами

Шаги по запуску

1. Клонирование репозитория (если необходимо)

Если проект находится в Git-репозитории, клонируйте его:
~bash~
-- git clone https://github.com/tatarchch/Test_Bank_REST.git
-- cd <папка проекта>

2. Подготовка файла переменных окружения
Создайте файл .env в корневой директории проекта и заполните его по примеру:
~bash~
# Временная зона
TZ=Europe/Moscow
# Настройки базы данных
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