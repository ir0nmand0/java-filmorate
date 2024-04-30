package ru.yandex.practicum.filmorate.exception;

/*
Это исключение создано так, как есть ошибка в тестах Postman, а именно в:
"Not friend remove"
При попытке удалить у пользователя друга,
которые не являются друзьями ожидается код 200, вместо 400.
А может это особенность 🤷...
*/

public class PostmanNotFriendRemoveException extends RuntimeException {
    public PostmanNotFriendRemoveException(String message) {
        super(message);
    }
}
