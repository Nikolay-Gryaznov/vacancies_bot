package ru.gryaznov.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import ru.gryaznov.bot.vacancy.Person;

import java.util.Arrays;
import java.util.HashMap;

import static ru.gryaznov.bot.constants.VarConstants.*;

public class SendMessageService {

    ButtonsService buttonsService = new ButtonsService();

    public SendMessage greetingMessage(Update update){
        SendMessage sendMessage = createMessage(update, GREETING_MESSAGE);
        ReplyKeyboardMarkup replyKeyboardMarkup =
                buttonsService.setButtons(buttonsService.createButtons(
                        Arrays.asList(
                                SEARCH_VACANCIES,
                                SEARCH_SETTINGS)));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage mainMenu(Update update){
        SendMessage sendMessage = createMessage(
                update, "Выможете продолжить поиск или изменить настройки поиска");
        ReplyKeyboardMarkup replyKeyboardMarkup =
                buttonsService.setButtons(buttonsService.createButtons(
                        Arrays.asList(
                                SEARCH_VACANCIES,
                                SEARCH_SETTINGS)));
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        return sendMessage;
    }

    public SendMessage createMessage(Update update, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getMessage().getChatId()));
        sendMessage.setText(message);
        return sendMessage;
    }

    public SendMessage createCallbackMessage(Update update, String message){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        sendMessage.setText(message);
        return sendMessage;
    }

    public SendMessage settingMessage(Update update){
        SendMessage sendMessage = createMessage(update, SETTING_MESSAGE);
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                SHOW_PERON,
                                SHOW_JOB_NAME,
                                SHOW_AREA,
                                SHOW_EXPERIENCE,
                                SHOW_SCHEDULE,
                                SHOW_EMPLOYMENT,
                                SHOW_SALARY,
                                SHOW_SORTING)));
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }

    public EditMessageText settingEditMessage(
            int callbackMessageId, String callbackChatId){
        EditMessageText editMessage = createEditMessage(SETTING_MESSAGE, callbackMessageId, callbackChatId);
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                SHOW_PERON,
                                SHOW_JOB_NAME,
                                SHOW_AREA,
                                SHOW_EXPERIENCE,
                                SHOW_SCHEDULE,
                                SHOW_EMPLOYMENT,
                                SHOW_SALARY,
                                SHOW_SORTING)));
        editMessage.setReplyMarkup(inlineKeyboardMarkup);
        return editMessage;
    }

    public EditMessageText createEditMessage(Update update, String message){
        EditMessageText editMessageText = new EditMessageText();
        int mesId = update.getCallbackQuery().getMessage().getMessageId();
        editMessageText.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
        editMessageText.setMessageId(mesId);
        editMessageText.setText(message);
        return editMessageText;
    }

    public EditMessageText createEditMessage(
            String message, int callbackMessageId, String callbackChatId){
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(callbackChatId);
        editMessageText.setMessageId(callbackMessageId);
        editMessageText.setText(message);
        return editMessageText;
    }

    public EditMessageText personMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update,
                "Профессия: " + personMap.get(chatId).getText() + "\n"
                        + "Регион: " + personMap.get(chatId).getArea() + "\n"
                        + "Опыт работы: " + personMap.get(chatId).getFormExperience() + "\n"
                        + "График работы: " + personMap.get(chatId).getFormSchedule() + "\n"
                        + "Тип занятости: " + personMap.get(chatId).getFormEmployment() + "\n"
                        + "Доход: " + personMap.get(chatId).getSalary()+ " RUB" + "\n"
                        + "Тип сортировки: " + personMap.get(chatId).getFormVacancySearchOrder());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CLEAR_PERSON,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText jobNameMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "Профессия: "
                + personMap.get(chatId).getText());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_JOB_NAME,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText areaMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "Регион: "
                + personMap.get(chatId).getArea());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_AREA,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText experienceMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "Опыт работы: "
                + personMap.get(chatId).getFormExperience());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_EXPERIENCE,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText changeExperience(Update update){
        EditMessageText editMessageText = createEditMessage(update, "Выберете опыт работы");
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                NO_EXPERIENCE,
                                EXPERIENCE_BETWEEN1AND3,
                                EXPERIENCE_BETWEEN3AND6,
                                EXPERIENCE_MORE_THAN6,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText scheduleMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "График работы: "
                + personMap.get(chatId).getFormSchedule());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_SCHEDULE,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText changeSchedule(Update update){
        EditMessageText editMessageText = createEditMessage(update, "Выберете график работы");
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                FULL_DAY,
                                SHIFT,
                                FLEXIBLE,
                                REMOTE,
                                FLY_IN_FLY_OUT,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText employmentMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "Тип занятости: "
                + personMap.get(chatId).getFormEmployment());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_EMPLOYMENT,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText changeEmployment(Update update){
        EditMessageText editMessageText = createEditMessage(update, "Выберете тип занятости");
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                FULL,
                                PART,
                                PROJECT,
                                VOLUNTEER,
                                PROBATION,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText sortingMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "Тип сортировки: "
                + personMap.get(chatId).getFormVacancySearchOrder());
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_SORTING,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText changeSorting(Update update){
        EditMessageText editMessageText = createEditMessage(update, "Выберете тип сортировки");
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                PUBLICATION_DATA,
                                SALARY_DESC,
                                SALARY_ASC,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

    public EditMessageText salaryMessage(Update update, HashMap<String, Person> personMap, String chatId){
        EditMessageText editMessageText = createEditMessage(update, "Доход: "
                + personMap.get(chatId).getSalary() + "RUB");
        InlineKeyboardMarkup inlineKeyboardMarkup =
                buttonsService.setInlineButtons(buttonsService.createInlineButtons(
                        Arrays.asList(
                                CHANGE_SALARY,
                                BACK)));
        editMessageText.setReplyMarkup(inlineKeyboardMarkup);
        return editMessageText;
    }

}
