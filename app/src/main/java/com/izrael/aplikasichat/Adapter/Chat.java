package com.izrael.aplikasichat.Adapter;

public class Chat {
    private String sender;

    public String getReciver() {
        return reciver;
    }

    private String  reciver;
    private String  message;
    private Boolean isseen;

    public Boolean getIsseen() {
        return isseen;
    }

    public void setIsseen(Boolean isseen) {
        this.isseen = isseen;
    }

    Chat(String sender, String reciver, String message, Boolean isseen) {
        this.sender = sender;
        this.reciver = reciver;
        this.message = message;
        this.isseen = isseen;
    }
    public Chat(){

    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }



    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
