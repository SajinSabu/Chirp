package ca.chirp.messenger;

public class ChatModel {

    private String senderId;
    private String recipientId;
    private String messageText;
    private String sinchId;
    private String timeSent;

    public ChatModel() {}

    public ChatModel(String senderId, String recipientId, String messageText, String sinchId, String timeSent) {
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.messageText = messageText;
        this.sinchId = sinchId;
        this.timeSent = timeSent;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setName(String senderId) {
        this.senderId = senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSinchId() {
        return sinchId;
    }

    public void setSinchId(String sinchId) {
        this.sinchId = sinchId;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }
}
