package com.example.mymessenger;

import com.google.gson.Gson;

public class Protocol {
    public final static int USER_STATUS = 1;   //Сообщение о статусе (онлайн или офлайн)
    public final static int MESSAGE = 2;       //Входящее и исходящее
    public final static int USER_NAME = 3;     //Сообщаем своё имя


    //Структура пользователя

    /*
    User = { id: 22, name:"Мишаня" }
    Message = 2{ sender: 22, receiver:1, encodedText:"Всем чмоки в этом чате"}
    */
    static class User{
        private long id;  //Номер
        private String name; //Имя

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public User() {}
    }
    //Структура сообщения о статусе пользователя
    static class UserStatus{
        private User user; //Пользователь
        private boolean connected; // Подключен ли

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }

        public boolean isConnected() {
            return connected;
        }

        public void setConnected(boolean connected) {
            this.connected = connected;
        }

        public UserStatus() {}
    }

    static class Message{
        public static final int GROUP_CHAT = 1; // ID группового чата
        private long sender; //Отправитель
        public long receiver; //Получатель
        private String encodedText; // текст сообщения

        public long getSender() {
            return sender;
        }

        public void setSender(long sender) {
            this.sender = sender;
        }

        public long getReceiver() {
            return receiver;
        }

        public void setReceiver(long receiver) {
            this.receiver = receiver;
        }

        public String getEncodedText() {
            return encodedText;
        }

        public void setEncodedText(String encodedText) {
            this.encodedText = encodedText;
        }

        public Message(String encodedText) {
            this.encodedText = encodedText;
        }
    }

    static class UserName {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public UserName(String name) {
            this.name = name;
        }
    }

    public static int getType(String json){
        if (json == null || json.length() == 0){
            return -1;}
        return Integer.valueOf(json.substring(0,1)); //Первый символ

    }
    public static UserStatus unpackStatus(String json){
        Gson g = new Gson();
        return g.fromJson(json.substring(1),UserStatus.class);
    }

    public static Message unpackMessage(String json){
        Gson g = new Gson();
        return g.fromJson(json.substring(1),Message.class);
    }

    public static String packMessage(Message message){
        Gson g = new Gson();
        return MESSAGE + g.toJson(message);
    }

    public static String packName(UserName name){
        Gson g = new Gson();
        return USER_NAME + g.toJson(name);
    }
}

