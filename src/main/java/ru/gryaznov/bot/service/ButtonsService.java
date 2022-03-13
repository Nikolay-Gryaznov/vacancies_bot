package ru.gryaznov.bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

//class for creating buttons
public class ButtonsService {
    public ReplyKeyboardMarkup setButtons (List<KeyboardRow> keyboardRowList){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);
        return replyKeyboardMarkup;
    }

    public List<KeyboardRow> createButtons(List<String> buttonsName){
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();
        for (String buttonName: buttonsName) {
            keyboardRow.add(new KeyboardButton(buttonName));
        }
        keyboardRows.add(keyboardRow);
        return keyboardRows;
    }

    public InlineKeyboardMarkup setInlineButtons (List<List<InlineKeyboardButton>> keyboardButtonsList){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(keyboardButtonsList);
        return  inlineKeyboardMarkup;
    }

    public List<List<InlineKeyboardButton>> createInlineButtons (List<String> buttonNames){
        List<List<InlineKeyboardButton>> keyboardButtonsList = new ArrayList<>();
        for (String buttonName: buttonNames) {
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setCallbackData(buttonName);
            inlineKeyboardButton.setText(buttonName);
            keyboardButtonsRow.add(inlineKeyboardButton);
            keyboardButtonsList.add(keyboardButtonsRow);
        }
        return  keyboardButtonsList;
    }


}
