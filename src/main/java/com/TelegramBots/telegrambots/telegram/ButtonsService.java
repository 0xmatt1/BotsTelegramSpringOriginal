package com.TelegramBots.telegrambots.telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import static com.TelegramBots.telegrambots.constant.VarConstant.SEARCH_FOR_IP;

public class ButtonsService {
    public ReplyKeyboardMarkup setButtons(List<KeyboardRow> keyboardRowList) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    public List<KeyboardRow> createButtons(List<String> buttonsName) {
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        KeyboardRow keyboardRow2 = new KeyboardRow();
        KeyboardButton keyboardboton = new KeyboardButton();
        for (String buttonName : buttonsName) {
            keyboardRow.add(new KeyboardButton(buttonName));
        }
        keyboardRow2.add(new KeyboardButton(SEARCH_FOR_IP));
        keyboardRow2.add(keyboardboton);
        keyboardboton.setText("Отправьте мне свои контакты!");
        keyboardboton.setRequestContact(true);
        keyboardRows.add(keyboardRow2);
        keyboardRows.add(keyboardRow);
        return keyboardRows;
    }

    /*    public List<List<InlineKeyboardButton>> createInlineButton(String buttonName) {
            List<List<InlineKeyboardButton>> keyBoardList = new ArrayList<>();
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            InlineKeyboardButton buttonOSINT = new InlineKeyboardButton();
            buttonOSINT.setText(buttonName);
            buttonOSINT.setCallbackData(buttonName);
            keyboardRow.add(buttonOSINT);
            keyBoardList.add(keyboardRow);
            return keyBoardList;
        }*/
/*    public InlineKeyboardMarkup setInlineKeyboard(List<List<InlineKeyboardButton>> keyBoardList ){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyBoardList);
        return inlineKeyboardMarkup;
    }*/

}
