package com.TelegramBots.telegrambots.telegram;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import static com.TelegramBots.telegrambots.constant.VarConstant.*;
import static java.util.Arrays.asList;

public class SendMessageService {

    private final ButtonsService buttonsService = new ButtonsService();

    public SendMessage createStartMessage(Update update) {
        SendMessage message = createSimpleMessage(update, GREATI_MESSAGE);
        ReplyKeyboardMarkup keyboardMarkup = buttonsService
                .setButtons(buttonsService.createButtons(asList(SEARCH_FOR_ID, SEARCH_FOR_PHONE, CREATORS)));
        message.setReplyMarkup(keyboardMarkup);
        return message;
    }

    public SendMessage createCreatorsMessage(Update update) {
        return createSimpleMessage(update, CREATOR_MESSAGE);
    }

    public SendMessage searchForID(Update update) {
        return createSimpleMessage(update, ENTER_ID_USER);
    }

    public SendMessage searchForPhone(Update update) {
        return createSimpleMessage(update, ENTER_PHONE);
    }
    public SendMessage exportContactUser(Update update) {

        return createSimpleMessage(update, ENTER_PHONE_FROM_EXPORT);
    }

    public SendMessage searchForIp(Update update) {

        SendMessage sendMessage = createSimpleMessage(update, ENTER_IP);
        return createSimpleMessage(update, ENTER_IP);
    }


    private SendMessage createSimpleMessage(Update update, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId().toString());
        sendMessage.setText(message);
        sendMessage.enableMarkdown(true);

        return sendMessage;

    }

}
