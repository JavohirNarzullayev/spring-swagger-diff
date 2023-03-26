package uz.narzullayev.javohir.telegram.model;


public enum BotMessageEnum {
    NON_COMMAND_MESSAGE("Пожалуйста пишите!! \uD83D\uDC47"),

    //результаты загрузки
    EXCEPTION_TELEGRAM_API_MESSAGE("Ошибка при попытку получить файл из API Telegram"),
    EXCEPTION_TOO_LARGE_MESSAGE("Максимальный размер 20 Мб"),
    EXCEPTION_BAD_FILE_MESSAGE("Файл не может быть обработан. Вы шлёте мне что-то не то, балуетесь, наверное"),

    EXCEPTION_API("Не удаётся подключит  соединение!!");

    private final String message;

    BotMessageEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
