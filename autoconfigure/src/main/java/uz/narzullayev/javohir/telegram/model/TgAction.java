package uz.narzullayev.javohir.telegram.model;

public interface TgAction {
    String SEND_MESSAGE = "/sendMessage";
    String SEND_PHOTO = "/sendPhoto";
    String SET_WEBHOOK = "/setWebhook";
    String SEND_DOCUMENT = "/sendDocument";
    String GET_FILE = "/getFile";
}
