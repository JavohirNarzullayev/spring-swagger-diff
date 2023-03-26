package uz.narzullayev.javohir.telegram.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
@Getter
@Setter
public class SendMessage {
    @JsonProperty("chat_id")
    private @NonNull String chatId;
    @JsonProperty("text")
    private @NonNull String text;
    @JsonProperty("parse_mode")
    private String parseMode;
    @JsonProperty("disable_web_page_preview")
    private Boolean disableWebPagePreview;
    @JsonProperty("disable_notification")
    private Boolean disableNotification;
    @JsonProperty("reply_to_message_id")
    private Integer replyToMessageId;

    @JsonProperty("allow_sending_without_reply")
    private Boolean allowSendingWithoutReply;

    public void disableWebPagePreview() {
        this.disableWebPagePreview = true;
    }

    public void enableWebPagePreview() {
        this.disableWebPagePreview = null;
    }

    public void enableNotification() {
        this.disableNotification = null;
    }

    public void disableNotification() {
        this.disableNotification = true;
    }

    public void enableMarkdown(boolean enable) {
        if (enable) {
            this.parseMode = "Markdown";
        } else {
            this.parseMode = null;
        }

    }

    public void enableHtml(boolean enable) {
        if (enable) {
            this.parseMode = "html";
        } else {
            this.parseMode = null;
        }

    }

    public void enableMarkdownV2(boolean enable) {
        if (enable) {
            this.parseMode = "MarkdownV2";
        } else {
            this.parseMode = null;
        }

    }

    public String toString() {
        return "SendMessage.SendMessageBuilder(chatId=" + this.chatId + ", text=" + this.text + ", parseMode=" + this.parseMode + ", disableWebPagePreview=" + this.disableWebPagePreview + ", disableNotification=" + this.disableNotification + ", replyToMessageId=" + this.replyToMessageId + ", replyMarkup=" + ", allowSendingWithoutReply=" + this.allowSendingWithoutReply + ")";
    }
}
