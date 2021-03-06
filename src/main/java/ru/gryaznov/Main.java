package ru.gryaznov;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.gryaznov.bot.core.VacancyBot;

public class Main {

    public static void main(String[] args) {

        //registration and launch of the bot
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new VacancyBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
