package com.TelegramBots.telegrambots.service;


import com.TelegramBots.telegrambots.Search.WhoIs;
import com.TelegramBots.telegrambots.config.BotConfig;
import com.TelegramBots.telegrambots.entity.User;
import com.TelegramBots.telegrambots.entity.UserInfo;
import com.TelegramBots.telegrambots.model.AccountRepository;
import com.TelegramBots.telegrambots.model.UserRepository;
import com.TelegramBots.telegrambots.telegram.SendMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;

import static com.TelegramBots.telegrambots.constant.VarConstant.*;


@Component
public class TelegramBots extends TelegramLongPollingBot {


    SendMessageService sendMessageService = new SendMessageService();

    UserInfo userInfo = new UserInfo();
    User user = new User();
    private final SearchService service;
    private static Logger logger;
    protected static Long userId = null;
    protected static String firstnameUser;
    protected static String usernameUser;
    protected static Long chatId = null;
    protected static String userInformation;
    protected static String userLangCode;
    final BotConfig config;
    @Autowired
    private UserRepository userRepository;
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


        String text = update.getMessage().getText();
        logger = LogManager.getRootLogger();
        Message message = update.getMessage();
        usernameUser = update.getMessage().getFrom().getUserName();
        userId = update.getMessage().getFrom().getId();
        firstnameUser = update.getMessage().getFrom().getFirstName();
        chatId = update.getMessage().getChatId();
        userLangCode = update.getMessage().getFrom().getLanguageCode();
        //Определяем доступ к боту(сверка пользователя на наличие в таблице account в БД Telegram)
        if (accountRepository.findById(chatId).isPresent()) {

            if (update.hasMessage() && update.getMessage().getContact() != null) {
                // Get the contact
                Contact contact = update.getMessage().getContact();


                // Print the contact information

                sendMessage(chatId, "Контакт добавлен в базу данных");
            }

            if (update.hasMessage() && update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();

                String firstName = update.getMessage().getChat().getFirstName();

                if (update.hasMessage() && message.hasText()) {
                    switch (messageText) {
                        case START -> {
                            startCommandReceived(chatId, firstName);
                            executeMessage(sendMessageService.createStartMessage(update));
                            logger.info("Следующий пользователь входит в систему: " + userId);


                        }
                        case CREATORS -> {
                            creatorsCommand(chatId, firstName);

                        }
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

                            sendMessage(chatId, service.findFromId(Long.valueOf(s)).toString());
                        }
                        if (regexPhone) {
                            try {
                                String result = service.findFromPhone(s).toString();
                                sendMessage(chatId, result);

                            }catch (NullPointerException e){
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

/*                switch (messageText) {
                    case START:
                        startCommandReceived(chatId, firstName);
                        break;
                    case CREATORS:
                        creatorsCommand(chatId, firstName);
                        break;
                    case SEARCH_FOR_ID:
                        *//*sendMessage(chatId,ENTER_ID_USER);*//*

                        if (text.matches(REGEX_USER_ID)){

                            sendMessage(chatId,"user");
                            break;
                        }


                    default:
                        sendMessage(chatId, "Извини, пока данная команда не поддерживается!");
                }*/


            /*if (update.hasMessage() && update.getMessage().hasContact()) {


                String contacts = update.getMessage().getContact().getPhoneNumber();
                System.out.println(contacts);
                try {
                    execute(SendMessage.builder().text(contacts).chatId(update.getMessage().getChatId().toString()).build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }

            logger = LogManager.getRootLogger();
            Message message = update.getMessage();
            usernameUser = update.getMessage().getFrom().getUserName();
            userId = update.getMessage().getFrom().getId();
            firstnameUser = update.getMessage().getFrom().getFirstName();
            chatId = update.getMessage().getChatId();
            userLangCode = update.getMessage().getFrom().getLanguageCode();


            userInformation = "ID пользователя - " + userId + ", установлен язык устройства - " +
                    userLangCode + ", FirstName - " + firstnameUser
                    + " Username - " + usernameUser;


            if (update.hasMessage() && message.hasText()) {
                switch (update.getMessage().getText()) {
                    case START -> {
                        sendImage(chatId, String.valueOf(Path.of(HI)));


                        executeMessage(sendMessageService.createStartMessage(update));
                    }
                    case CREATORS -> {
                        sendSticker(chatId, String.valueOf(Path.of(CREATORS_FILE)));
                        executeMessage(sendMessageService.createCreatorsMessage(update));
                    }
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
                            execute(SendMessage.builder().text(String.valueOf(WhoIs.getInfo(s))).chatId(chatId.toString()).build());
                        } catch (TelegramApiException | UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (regexUserId) {
                        if (userRepository.findAllByUserId(Long.parseLong(s)).isEmpty()) {
                            logger.info("Пользователь с ID - " + userId + " FirstName -  " + firstnameUser + " ищет: " + s);
                            try {
                                execute(SendMessage.builder().text("❌ID пользователя в базе Telegram не найдено").chatId(chatId.toString()).build());
                            } catch (TelegramApiException e) {
                                throw new RuntimeException(e);
                            }
                            break;
                        }
                        try {
                            execute(SendMessage.builder().text(
                                    StringUtils.capitalize(userRepository.findAllByUserId(Long.parseLong(s)).toString())).chatId(chatId.toString()).build());
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }


                    if (!Starts && !Creators && !regexIp && !regexUrl && !regexUserId && !regexPhone) {

                        try {
                            execute(SendMessage
                                    .builder().text("‼️Проверьте правильность введенных данных‼️")
                                    .chatId(chatId.toString()).build());
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                        break;
                    }*/





/*    @NotNull
    private void searchPhone(Session session, String s) throws TelegramApiException {
        if (searchByPhone.getInfoFromTelegramDb(session, s).isEmpty()) {
            execute(SendMessage.builder().text("❌Пользователь в базе Telegram не найден").chatId(chatId.toString()).build());
        }else {
            execute(SendMessage.builder().text(
                    StringUtils.capitalize(searchByPhone.getInfoFromTelegramDb(session, s).toString())).chatId(chatId.toString()).build());
            logger.info("Пользователь : " + userInformation + " получил результат поиска: " + searchByPhone.getInfoFromTelegramDb(session, s));
        }
    }*/

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

    public void sendSticker(Long chatId, String path) {
        try {
            SendSticker sticker = new SendSticker();
            sticker.setSticker(new InputFile(new File(path)));

            sticker.setChatId(chatId.toString());
            execute(sticker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void creatorsCommand(long chatId, String firstName) {
        String answer = firstName + ", вот данные:\n" + CREATOR_MESSAGE;
        sendMessage(chatId, answer);
    }

    private void startCommandReceived(long chatId, String firstName) {
        sendImage(chatId, HI);
        sendMessage(chatId, firstName);

    }

    private void sendMessage(Long chatId, String textToSend) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }


}

/*    private void loxCommandReceived(long chatId, String firstName) {
        String answer = "Пока, " + firstName + " !";
        sendMessage(chatId,answer);
    }

    private void startCommandReceived(long chatId, String firstName) {
        String answer = "Привет, " + firstName + " доброго времени суток!";
        sendMessage(chatId,answer);

    }
    private void sendMessage(Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);

        try{
            execute(sendMessage);
        } catch (TelegramApiException e) {

        }
    }*/

