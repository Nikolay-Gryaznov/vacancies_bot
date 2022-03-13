package ru.gryaznov.bot.vacancy;

//class for database
public class Person {

    public Person() {
        this.text = "";
        this.experience = "";
        this.employment = "";
        this.schedule = "";
        this.area = "";
        this.salary = "";
        this.vacancySearchOrder = "";
        this.formVacancySearchOrder = "";
        this.formEmployment = "";
        this.formExperience = "";
        this.formSchedule = "";
    }

    private String text;
    private String experience;
    private String formExperience;
    private String employment;
    private String formEmployment;
    private String schedule;
    private String formSchedule;
    private String area;
    private String salary;
    private String vacancySearchOrder;
    private String formVacancySearchOrder;

    public String getText() {
        return text;
    }

    public String getFormExperience() {
        return formExperience;
    }

    public void setFormExperience(String formExperience) {
        this.formExperience = formExperience;
    }

    public String getFormEmployment() {
        return formEmployment;
    }

    public void setFormEmployment(String formEmployment) {
        this.formEmployment = formEmployment;
    }

    public String getFormSchedule() {
        return formSchedule;
    }

    public void setFormSchedule(String formSchedule) {
        this.formSchedule = formSchedule;
    }

    public String getFormVacancySearchOrder() {
        return formVacancySearchOrder;
    }

    public void setFormVacancySearchOrder(String formVacancySearchOrder) {
        this.formVacancySearchOrder = formVacancySearchOrder;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getEmployment() {
        return employment;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }


    public String getVacancySearchOrder() {
        return vacancySearchOrder;
    }

    public void setVacancySearchOrder(String vacancySearchOrder) {
        this.vacancySearchOrder = vacancySearchOrder;
    }
}
