package ru.job4j.bmb.model;

public class User {

    private Long id;
    private long clientId;
    private long chatId;

    public User(long chatId, long clientId, Long id) {
        this.chatId = chatId;
        this.clientId = clientId;
        this.id = id;
    }

    public  User() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getClientId() {
        return clientId;
    }

    public void setClientId(long clientId) {
        this.clientId = clientId;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

}
