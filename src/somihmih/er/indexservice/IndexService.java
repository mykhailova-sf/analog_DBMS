package somihmih.er.indexservice;

import java.io.*;
import java.util.Arrays;

public class IndexService {

    public static final int POSITION_NUM_SIZE = Integer.BYTES;
    public static final int ENTITY_ID_SIZE = Integer.BYTES;

    public static final int MAX_COUNT = 100;

    private Index[] indexes = new Index[MAX_COUNT];

    private int count = 0;

    private int currentMaxIndex = 0;
    private String fileName;

    public IndexService(String fileName) {
        this.fileName = fileName;
        loadIndexes();
    }

    public void saveToFile() {
        recreateIndexFile(Arrays.copyOfRange(indexes, 0, count));
    }

    public void loadIndexes() {
        indexes = new Index[MAX_COUNT];
        count = 0;
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName))) {
            while (inputStream.available() >= (ENTITY_ID_SIZE + POSITION_NUM_SIZE + 1)) {
                indexes[count] = new Index(inputStream.readInt(), inputStream.readInt(), inputStream.readBoolean());
                if (indexes[count].getEntityId() > currentMaxIndex) {
                    currentMaxIndex = indexes[count].getEntityId();
                }
                System.out.println("Прочитаний індекс : (id: " + indexes[count].getEntityId() + ", position: " + indexes[count].getPos() + ")");
                count++;
            }
        } catch (IOException e) {
            System.out.println("Помилка при читанні з файлу " + e.getMessage());
        }
    }

    public void recreateIndexFile(Index[] indices) {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(fileName))) {
            for (int i = 0; i < indices.length; i++) {
                outputStream.writeInt(indices[i].getEntityId());
                outputStream.writeInt(indices[i].getPos());
                outputStream.writeBoolean(indices[i].isDeleted());
            }
        } catch (IOException e) {
            System.out.println("Помилка при записі у файл " + e.getMessage());
        }

        loadIndexes();
    }

    public Index[] getIndices() {
        return Arrays.copyOfRange(indexes, 0, count);
    }

    public void addIndex(Index index) {
        indexes[count++] = new Index(index.getEntityId(), index.getPos());
    }


    public int getNewId() {
        return ++currentMaxIndex;
    }

    public Index getNewIndex() {
        int i = 0;
        while (i < count) {
            if (indexes[i].isDeleted()) {
                return new Index(getNewId(), indexes[i].getPos());
            }
            i++;
        }
        /*
        for (Index index : indexes) {
            if (index != null && index.isDeleted()) {
                return new Index(getNewId(), index.getPos());
            }
        }
       */
        return new Index(getNewId(), count);
    }

    public int getPosition(int id) {
        Index index = getIndexFor(id);

        return (index != null) ? index.getPos() : -1 ;
    }

    public Index getIndexFor(int id) {
        for (Index index : indexes) {

            if (index != null && index.getEntityId() == id) {
                return index;
            }
        }

        return null;
    }
}
