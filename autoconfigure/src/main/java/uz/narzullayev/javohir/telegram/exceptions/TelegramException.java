package uz.narzullayev.javohir.telegram.exceptions;

import lombok.Getter;
import uz.narzullayev.javohir.telegram.model.BotMessageEnum;

@Getter
public class TelegramException {

    @Getter
    public static class TelegramFileNotFoundException extends RuntimeException{
        public final String chatId;
        public TelegramFileNotFoundException(String chatId) {
            super(BotMessageEnum.EXCEPTION_TELEGRAM_API_MESSAGE.getMessage());
            this.chatId=chatId;
        }
    }

    @Getter
    public static class CommandEmptyException extends RuntimeException{
        public final String chatId;
        public CommandEmptyException(String chatId) {
            super(BotMessageEnum.NON_COMMAND_MESSAGE.getMessage());
            this.chatId=chatId;
        }
    }

    @Getter
    public static class TooBigMessageException extends RuntimeException{
        public final String chatId;
        public TooBigMessageException(String chatId) {
            super(BotMessageEnum.EXCEPTION_TOO_LARGE_MESSAGE.getMessage());
            this.chatId=chatId;

        }
    }

    @Getter
    public static class InternalErrorException extends RuntimeException{
        public final String chatId;
        public InternalErrorException(String chatId) {
            super(BotMessageEnum.EXCEPTION_BAD_FILE_MESSAGE.getMessage());
            this.chatId=chatId;
        }
    }

    @Getter
    public static class DefaultException extends RuntimeException {
        public final String chatId;
        public DefaultException(String chatId, String message) {
            super(message);
            this.chatId = chatId;
        }
    }


}
