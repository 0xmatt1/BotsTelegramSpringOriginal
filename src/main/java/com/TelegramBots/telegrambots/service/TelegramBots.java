package com.TelegramBots.telegrambots.service;


import com.TelegramBots.telegrambots.Search.WhoIs;
import com.TelegramBots.telegrambots.config.BotConfig;
import com.TelegramBots.telegrambots.model.AccountRepository;
import com.TelegramBots.telegrambots.telegram.SendMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.UnsupportedEncodingException;

import static com.TelegramBots.telegrambots.constant.VarConstant.*;


@Component
public class TelegramBots extends TelegramLongPollingBot {


    SendMessageService sendMessageService = new SendMessageService();

    private final SearchService service;
    protected static Logger logger;
    protected static Long userId = null;
    protected static String firstnameUser;
    protected static String usernameUser;
    protected static Long chatId = null;
    protected static String userLangCode;
    final BotConfig config;
    @Autowired
    private AccountRepository accountRepository;

    public TelegramBots(SearchService service, BotConfig config) {
        this.service = service;
        this.config = config;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @NotNull
    @Override
    public void onUpdateReceived(Update update) {


        logger = LogManager.getRootLogger();
        Message message = update.getMessage();
        usernameUser = update.getMessage().getFrom().getUserName();
        userId = update.getMessage().getFrom().getId();
        firstnameUser = update.getMessage().getFrom().getFirstName();
        chatId = update.getMessage().getChatId();
        userLangCode = update.getMessage().getFrom().getLanguageCode();
        //Определяем доступ к боту(сверка пользователя на наличие в таблице account в БД Telegram)
        if (accountRepository.findById(chatId).isPresent()) {


            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                String firstName = update.getMessage().getChat().getFirstName();

                if (update.hasMessage() && message.hasText()) {
                    switch (messageText) {
                        case START -> {
                            sendImage(chatId, HI);
                            executeMessage(sendMessageService.createStartMessage(update));


                        }
                        case CREATORS -> creatorsCommand(chatId, firstName);
                        case SEARCH_FOR_ID -> executeMessage(sendMessageService.searchForID(update));
                        case SEARCH_FOR_PHONE -> executeMessage(sendMessageService.searchForPhone(update));
                        case SEARCH_FOR_IP -> executeMessage(sendMessageService.searchForIp(update));

                    }
                } else {
                    try {
                        execute(SendMessage
                                .builder().text("‼️Проверьте правильность введенных данных‼️")
                                .chatId(update.getMessage().getChatId().toString()).build());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }

                }
                if (update.hasMessage() && message.hasText()) {

                    String[] lineText = update.getMessage().getText().split("\n");
                    for (String s : lineText) {
                        if (s.equals(SEARCH_FOR_ID) || s.equals(SEARCH_FOR_PHONE) || s.equals(SEARCH_FOR_IP))
                            break;

                        boolean Starts = s.equals(START);
                        boolean Creators = s.equals(CREATORS);
                        boolean regexUserId = s.matches(REGEX_USER_ID);
                        boolean regexPhone = s.matches(REGEX_PHONE);
                        boolean regexUrl = s.matches(REGEX_URL);
                        boolean regexIp = s.matches(REGEX_IP);


                        if (regexIp || regexUrl) {
                            logger.info("Пользователь с ID - " + userId + " FirstName -  " + firstnameUser + " ищет: " + s);
                            try {
                                execute(SendMessage.builder().text(String.valueOf(WhoIs.getInfo(s))).chatId(String.valueOf(chatId)).build());
                            } catch (TelegramApiException | UnsupportedEncodingException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (regexUserId) {
                            try {
                                String result = service.findFromId(Long.valueOf(s)).toString();
                                sendMessages(chatId, result);
                            } catch (NullPointerException e) {
                                logger.info("Пользователь Telegram c ID " + s + " в базе данных не найден!");
                                try {
                                    execute(SendMessage
                                            .builder().text("❌Пользователь не найден❌")
                                            .chatId(String.valueOf(chatId)).build());
                                } catch (TelegramApiException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        }
                        if (regexPhone) {
                            try {
                                String result = service.findFromPhone(s).toString();
                                sendMessages(chatId, result);

                            } catch (NullPointerException e) {
                                logger.info("Абонентский номер " + s + " в базе данных не найден!");
                                try {
                                    execute(SendMessage
                                            .builder().text("❌Пользователь не найден❌")
                                            .chatId(String.valueOf(chatId)).build());
                                } catch (TelegramApiException ex) {
                                    ex.printStackTrace();
                                }

                            }


                        }


                        if (!Starts && !Creators && !regexIp && !regexUrl && !regexUserId && !regexPhone) {

                            try {
                                execute(SendMessage
                                        .builder().text("‼️Проверьте правильность введенных данных‼️")
                                        .chatId(String.valueOf(chatId)).build());
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                    }
                }
            }
        } else {
            try {
                execute(SendMessage
                        .builder().text("‼️У Вас нет прав доступа‼️")
                        .chatId(String.valueOf(chatId)).build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private <T extends BotApiMethod> void executeMessage(T sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Не удалось отправить сообщение!" + e.getCause());
        }
    }

    public void sendImage(Long chatId, String path) {
        try {
            SendPhoto photo = new SendPhoto();
            photo.setPhoto(new InputFile(new File(path)));
            photo.setChatId(String.valueOf(chatId));
            execute(photo);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Выловлено исключение - > " + e);
        }
    }

/*    public void sendSticker(Long chatId, String path) {
        try {
            SendSticker sticker = new SendSticker();
            sticker.setSticker(new InputFile(new File(path)));

            sticker.setChatId(chatId.toString());
            execute(sticker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void creatorsCommand(long chatId, String firstName) {
        String answer = firstName + ", вот данные:\n" + CREATOR_MESSAGE;
        sendMessages(chatId, answer);
    }

    private void sendMessages(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


}


