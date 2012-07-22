package org.karpukhin.smsviewer.model;

import java.util.Date;

/**
 * @author Pavel Karpukhin
 */
public class Message {

    private Date date;
    private String number;
    private String text;
    private boolean inbox;

    public Message() {
    }

    public Message(Date date, String number, String text, boolean inbox) {
        this.date = date;
        this.number = number;
        this.text = text;
        this.inbox = inbox;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getInbox() {
        return inbox;
    }

    public void setInbox(boolean inbox) {
        this.inbox = inbox;
    }
}
