package com.khcpietro.smc.grandtheftalarm.network;

public class DefaultResponse {
    private int responseCode;
    private int responseMessage;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public int getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(int responseMessage) {
        this.responseMessage = responseMessage;
    }
}
