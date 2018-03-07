package com.example.realtimewebsocket;

import java.util.Date;

/**
 * @author babadopulos
 */
public class TransportMessage {
    private Date date;
    private DataType type;
    private String data;
    private Long nextUpdate;

    public TransportMessage(DataType type){
        this.type = type;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public DataType getType() {
        return type;
    }

    public Long getNextUpdate() {
        return nextUpdate;
    }

    public void setNextUpdate(Long nextUpdate) {
        this.nextUpdate = nextUpdate;
    }
}
