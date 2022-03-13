package ru.gryaznov.bot.core;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.gryaznov.bot.areaparse.Parser;
import ru.gryaznov.bot.service.SendMessageService;
import ru.gryaznov.bot.vacancy.Person;
import ru.gryaznov.bot.vacancy.Vacancies;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

import static ru.gryaznov.bot.constants.VarConstants.*;

public class VacancyBot extends TelegramLongPollingBot {

    SendMessageService sendMessageService = new SendMessageService();

    Dotenv dotenv = Dotenv.load();
    String BOT_NAME = dotenv.get("BOT_NAME");
    String BOT_TOKEN = dotenv.get("BOT_TOKEN");

    public VacancyBot() {
        super();
    }

    boolean waitUpdateJobName = false;
    boolean waitUpdateSalary = false;
    boolean waitUpdateArea = false;

    int callbackMessageId;
    String callbackChatId;

    String text, experience, employment, schedule, area, salary, page, vacancySearchOrder;
    static int pageNumber = 0;
    Parser parser = new Parser();
    HashMap<String, Person> personMap = new HashMap<>();
    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()){
            String chatId = String.valueOf(update.getMessage().getChatId());
            if (!personMap.containsKey(chatId)) {
                personMap.put(chatId, new Person());
                System.out.println("new");
            }
            try {
                switch (update.getMessage().getText()){

                    case START:
                        execute(sendMessageService.greetingMessage(update));
                        break;

                    case SEARCH_VACANCIES:
                        waitUpdateSalary = false;
                        waitUpdateJobName = false;
                        waitUpdateArea = false;

                        page = String.valueOf(pageNumber);
                        if (pageNumber>99){
                            execute(sendMessageService.createMessage(update,
                                    "Вы просмотрели все открытые вакансии"));
                            pageNumber = 0;
                            break;
                        }

                        text = personMap.get(chatId).getText();
                        experience = personMap.get(chatId).getExperience();
                        employment = personMap.get(chatId).getEmployment();
                        schedule = personMap.get(chatId).getSchedule();
                        vacancySearchOrder = personMap.get(chatId).getVacancySearchOrder();
                        area = parser.parse().getAreaData().get(personMap.get(chatId).getArea().toLowerCase());
                        if (area==null) area = "";
                        salary = personMap.get(chatId).getSalary();

                        StringBuilder stringBuilder = new StringBuilder("https://api.hh.ru/vacancies?page=").append(page).append("&");
                        if (!text.equals("")) stringBuilder.append("text=").append(text.toLowerCase().replace(" ", "")).append("&");
                        if (!experience.equals("")) stringBuilder.append("experience=").append(experience).append("&");
                        if (!employment.equals("")) stringBuilder.append("employment=").append(employment).append("&");
                        if (!schedule.equals("")) stringBuilder.append("schedule=").append(schedule).append("&");
                        if (!area.equals("")) stringBuilder.append("area=").append(area).append("&");
                        if (!salary.equals("")) stringBuilder.append("salary=").append(salary).append("&");
                        if (!vacancySearchOrder.equals("")) stringBuilder.append("vacancy_search_order=").append(vacancySearchOrder).append("&");
                        System.out.println(stringBuilder);
                        HttpClient client = HttpClient.newHttpClient();
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(stringBuilder.toString()))
                                .build();
                        try {
                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            if (String.valueOf(response.body()).contains("errors")){
                                execute(sendMessageService.createMessage(update,
                                        "По вашим параметрам не было найдено вакансий"));
                                break;
                            }
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                            Vacancies vacancies = mapper.readValue(response.body(), Vacancies.class);
                            if (vacancies.getItems().isEmpty()){
                                execute(sendMessageService.createMessage(update,
                                        "По вашим параметрам не было найдено вакансий"));
                                break;
                            }else {
                                vacancies.getItems().forEach(job ->{
                                    try {
                                        execute(new SendMessage(chatId,
                                                job.getName() + "\n"
                                                        + job.getArea().getName() + "\n"
                                                        + "https://hh.ru/vacancy/"+job.getId() + "\n"));
                                    } catch (TelegramApiException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        pageNumber++;
                        break;


                    case MAIN_MENU:
                        execute(sendMessageService.mainMenu(update));
                        break;

                    case SEARCH_SETTINGS:
                        waitUpdateSalary = false;
                        waitUpdateJobName = false;
                        waitUpdateArea = false;

                        execute(sendMessageService.settingMessage(update));
                        pageNumber = 0;
                        break;

                    default:
                        if (waitUpdateJobName){
                            personMap.get(chatId).setText(update.getMessage().getText());
                            execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                            waitUpdateJobName = false;
                            break;
                        } else if (waitUpdateSalary) {
                            String res;
                            try{
                                res = update.getMessage().getText();
                            } catch (Exception e){
                                res = "";
                            }
                            personMap.get(chatId).setSalary(res);
                            execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                            waitUpdateSalary = false;
                            break;
                        }else if (waitUpdateArea){
                            if (parser.parse().getAreaData().containsKey(update.getMessage().getText().toLowerCase())) {
                                personMap.get(chatId).setArea(update.getMessage().getText());
                            }
                            execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                            waitUpdateArea = false;
                            break;
                        } else{
                            execute(sendMessageService.createMessage(
                                    update, "Воспользуйтесь кнопками панели или сообщения с настройками"));
                            break;
                        }

                }
            } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
        }
        String res = "";
        if(update.hasCallbackQuery()){
            callbackMessageId = update.getCallbackQuery().getMessage().getMessageId();
            callbackChatId = String.valueOf(update.getCallbackQuery().getMessage().getChatId());
            try {
                if (update.getCallbackQuery().getData().equals(NO_EXPERIENCE) ||
                        update.getCallbackQuery().getData().equals(EXPERIENCE_BETWEEN1AND3) ||
                        update.getCallbackQuery().getData().equals(EXPERIENCE_BETWEEN3AND6) ||
                        update.getCallbackQuery().getData().equals(EXPERIENCE_MORE_THAN6)){
                    if (update.getCallbackQuery().getData().equals(NO_EXPERIENCE)) res = "noExperience";
                    if (update.getCallbackQuery().getData().equals(EXPERIENCE_BETWEEN1AND3)) res = "between1And3";
                    if (update.getCallbackQuery().getData().equals(EXPERIENCE_BETWEEN3AND6)) res = "between3And6";
                    if (update.getCallbackQuery().getData().equals(EXPERIENCE_MORE_THAN6)) res = "moreThan6";
                    personMap.get(callbackChatId).setExperience(res);
                    personMap.get(callbackChatId).setFormExperience(update.getCallbackQuery().getData());
                    execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                }
                if (update.getCallbackQuery().getData().equals(FULL_DAY) ||
                        update.getCallbackQuery().getData().equals(SHIFT) ||
                        update.getCallbackQuery().getData().equals(FLEXIBLE) ||
                        update.getCallbackQuery().getData().equals(REMOTE) ||
                        update.getCallbackQuery().getData().equals(FLY_IN_FLY_OUT)){
                    if (update.getCallbackQuery().getData().equals(FULL_DAY)) res = "fullDay";
                    if (update.getCallbackQuery().getData().equals(SHIFT)) res = "shift";
                    if (update.getCallbackQuery().getData().equals(FLEXIBLE)) res = "flexible";
                    if (update.getCallbackQuery().getData().equals(REMOTE)) res = "remote";
                    if (update.getCallbackQuery().getData().equals(FLY_IN_FLY_OUT)) res = "flyInFlyOut";
                    personMap.get(callbackChatId).setSchedule(res);
                    personMap.get(callbackChatId).setFormSchedule(update.getCallbackQuery().getData());
                    execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                }
                if (update.getCallbackQuery().getData().equals(FULL) ||
                        update.getCallbackQuery().getData().equals(PART) ||
                        update.getCallbackQuery().getData().equals(PROJECT) ||
                        update.getCallbackQuery().getData().equals(VOLUNTEER) ||
                        update.getCallbackQuery().getData().equals(PROBATION)){
                    if (update.getCallbackQuery().getData().equals(FULL)) res = "full";
                    if (update.getCallbackQuery().getData().equals(PART)) res = "part";
                    if (update.getCallbackQuery().getData().equals(PROJECT)) res = "project";
                    if (update.getCallbackQuery().getData().equals(VOLUNTEER)) res = "volunteer";
                    if (update.getCallbackQuery().getData().equals(PROBATION)) res = "probation";
                    personMap.get(callbackChatId).setEmployment(res);
                    personMap.get(callbackChatId).setFormEmployment(update.getCallbackQuery().getData());
                    execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                }
                if (update.getCallbackQuery().getData().equals(PUBLICATION_DATA) ||
                        update.getCallbackQuery().getData().equals(SALARY_DESC) ||
                        update.getCallbackQuery().getData().equals(SALARY_ASC)){
                    if (update.getCallbackQuery().getData().equals(PUBLICATION_DATA)) res = "publication_time";
                    if (update.getCallbackQuery().getData().equals(SALARY_DESC)) res = "salary_desc";
                    if (update.getCallbackQuery().getData().equals(SALARY_ASC)) res = "salary_asc";
                    personMap.get(callbackChatId).setVacancySearchOrder(res);
                    personMap.get(callbackChatId).setFormVacancySearchOrder(update.getCallbackQuery().getData());
                    execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                }

            switch (update.getCallbackQuery().getData()){

                case SHOW_JOB_NAME:
                    execute(sendMessageService.jobNameMessage(update, personMap, callbackChatId));
                    break;

                case CHANGE_JOB_NAME:
                    execute(sendMessageService.createCallbackMessage(update, "Введите профессию"));
                    waitUpdateJobName = true;
                    break;

                case SHOW_AREA:
                    execute(sendMessageService.areaMessage(update, personMap, callbackChatId));
                    break;

                case CHANGE_AREA:
                    execute(sendMessageService.createCallbackMessage(update, "Введите регион"));
                    waitUpdateArea = true;
                    break;

                case SHOW_EXPERIENCE:
                    execute(sendMessageService.experienceMessage(update, personMap, callbackChatId));
                    break;

                case CHANGE_EXPERIENCE:
                    execute(sendMessageService.changeExperience(update));
                    break;

                case SHOW_SCHEDULE:
                    execute(sendMessageService.scheduleMessage(update, personMap, callbackChatId));
                    break;
                case CHANGE_SCHEDULE:
                    execute(sendMessageService.changeSchedule(update));
                    break;

                case SHOW_EMPLOYMENT:
                    execute(sendMessageService.employmentMessage(update, personMap, callbackChatId));
                    break;

                case CHANGE_EMPLOYMENT:
                    execute(sendMessageService.changeEmployment(update));
                    break;

                case SHOW_SALARY:
                    execute(sendMessageService.salaryMessage(update, personMap, callbackChatId));
                    break;

                case CHANGE_SALARY:
                    execute(sendMessageService.createCallbackMessage(update, "Введите желаемый доход"));
                    waitUpdateSalary = true;
                    break;

                case SHOW_SORTING:
                    execute(sendMessageService.sortingMessage(update, personMap, callbackChatId));
                    break;

                case CHANGE_SORTING:
                    execute(sendMessageService.changeSorting(update));
                    break;

                case SHOW_PERON:
                    execute(sendMessageService.personMessage(update, personMap, callbackChatId));
                    break;

                case CLEAR_PERSON:
                    personMap.put(callbackChatId, new Person());

                    execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                    break;

                case BACK:
                    waitUpdateJobName = false;
                    waitUpdateSalary = false;
                    waitUpdateArea = false;

                    execute(sendMessageService.settingEditMessage(callbackMessageId, callbackChatId));
                    break;

            }} catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername() {
        return BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

}
