package net.suparking.chargeserver.mq;

public class BasicMQData {
    public String collectionName;
    public String type;

    public BasicMQData() {}
    public BasicMQData(String collectionName, String type) {
        this.collectionName = collectionName;
        this.type = type;
    }

    @Override
    public String toString() {
        return "BasicMQData{" + "collectionName='" + collectionName + '\'' + ", type='" + type + '\'' + '}';
    }
}
