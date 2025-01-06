package somihmih.er.entity;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

public class PatientEntity implements Entity {

    public static final int MAX_NAME_LEN = 30;
    public static final int MAX_NUMBER_LEN = 13;
    public static final int ID_MAX_SIZE = Integer.BYTES;

    public static final int STATUS_SIZE = 1;
    private int id = -1;
    private String name;
    private String phoneNumber;
    private int admissionId = -1;
    private boolean deleted = false;

    public PatientEntity() {
    }

    public PatientEntity(int id, String name, String phoneNumber, int admissionId, boolean deleted) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.admissionId = admissionId;
        this.deleted = deleted;
    }

    public PatientEntity(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public PatientEntity(int id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getFirstAdmissionId() {
        return admissionId;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void saveYourselfTo(DataOutput outputStream) {
        try {
            String nameToSave = normalizeToMaxLen(name, MAX_NAME_LEN);
            String numberToSave = normalizeToMaxLen(phoneNumber, MAX_NUMBER_LEN);

            outputStream.writeInt(id);
            outputStream.write(nameToSave.getBytes("UTF-16BE"));
            outputStream.write(numberToSave.getBytes("UTF-16BE"));
            outputStream.writeInt(admissionId);
            outputStream.writeBoolean(deleted);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String normalizeToMaxLen(String value, int maxLen) {
        return (value.length() < maxLen)
                ? value + " ".repeat(maxLen - value.length())
                : value.substring(0, maxLen);
    }

    public void loadYourselfFrom(DataInputStream inputStream) {
        try {
            id = inputStream.readInt();
            byte[] nameAsBytes = new byte[PatientEntity.MAX_NAME_LEN * 2];
            inputStream.readFully(nameAsBytes);
            byte[] numberAsBytes = new byte[PatientEntity.MAX_NUMBER_LEN * 2];
            inputStream.readFully(numberAsBytes);
            name = new String(nameAsBytes, "UTF-16").trim(); // трім обрізає пропуски
            phoneNumber = new String(numberAsBytes, "UTF-16").trim();
            admissionId = inputStream.readInt();
            deleted = inputStream.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Entity getClone() {
        return new PatientEntity(id, name, phoneNumber, admissionId, deleted);
    }

    @Override
    public String toString() {
        return "Patient (" +
                "id=" + id +
                ", name=" + name +
                ", phoneNumber=" + phoneNumber +
                ", addmissionId=" + admissionId +
                ((isDeleted()) ? ", DELETED" : "") +
                ')';
    }

    @Override
    public int getSizeInBytes() {
        return ID_MAX_SIZE
             + MAX_NAME_LEN * 2
             + MAX_NUMBER_LEN * 2
             + AdmissionEntity.ID_SIZE
             + 1;
    }

    @Override
    public void markAsDeleted() {
        deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setAdmissionId(int admissionId) {
        this.admissionId = admissionId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
