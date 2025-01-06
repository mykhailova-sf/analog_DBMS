package somihmih.er.entity;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class AdmissionEntity implements Entity {

    public static final int ID_SIZE = Integer.BYTES;
    public static final int MAX_DATE_LEN = 8;
    public static final int PRICE_LEN = Integer.BYTES;

    public static final int STATUS_SIZE = 1;

    public void markAsDeleted() {
        this.deleted = true;
    }

    private boolean deleted = false;


    public AdmissionEntity() {
    }

    public AdmissionEntity(int id, String date, int price, int nextAdId, boolean deleted) {
        this.id = id;
        this.date = date;
        this.price = price;
        this.nextAdId = nextAdId;
        this.deleted = deleted;
    }

    public AdmissionEntity(String date) {

        this.date = date;
    }

    private int id = -1;
    private String date;
    private int price = -1;
    private int nextAdId = -1;

    @Override
    public AdmissionEntity getClone() {
        return new AdmissionEntity(id, date, price, nextAdId, deleted);
    }


    @Override
    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getNextAdId() {
        return nextAdId;
    }

    public void setNextAdId(int nextAdId) {
        this.nextAdId = nextAdId;
    }

    @Override
    public void saveYourselfTo(DataOutput outputStream) {

        try {
            String dateToSave = (date.length() < MAX_DATE_LEN) ?
                date + " ".repeat(MAX_DATE_LEN - date.length())
              : date.substring(0, MAX_DATE_LEN);

            outputStream.writeInt(id);
            outputStream.write(dateToSave.getBytes("UTF-16BE"));
            outputStream.writeInt(price);
            outputStream.writeInt(nextAdId);
            outputStream.writeBoolean(deleted);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadYourselfFrom(DataInputStream inputStream) {
        try {
            id = inputStream.readInt();
            byte[] dateAsBytes = new byte[AdmissionEntity.MAX_DATE_LEN * 2];
            inputStream.readFully(dateAsBytes);
            date = new String(dateAsBytes, "UTF-16").trim();
            price = inputStream.readInt();
            nextAdId = inputStream.readInt();
            deleted = inputStream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Admission (" +
                "id:" + id +
                ", date:'" + date +
                ", price:" + price +
                ", nextAdId:" + nextAdId +
                ((isDeleted()) ? ", DELETED" : "") +
                ')';
    }

    private boolean isDeleted() {
        return deleted;
    }

    @Override
    public int getSizeInBytes() {
        return ID_SIZE
                + MAX_DATE_LEN * 2
                + PRICE_LEN
                + ID_SIZE
                + 1; // Boolean size
    }
}
