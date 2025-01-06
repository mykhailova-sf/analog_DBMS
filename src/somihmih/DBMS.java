package somihmih;

import somihmih.er.entity.AdmissionEntity;
import somihmih.er.entity.PatientEntity;
import somihmih.er.entity.Entity;
import somihmih.er.indexservice.Index;
import somihmih.er.indexservice.IndexService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DBMS {

    public static final String ADMISSIONS = "./dbfiles/addmissions";
    public static final String PATIENTS = "./dbfiles/patients";
    public static final String ADMISSION_INDEXES = "./dbfiles/addmissionIndexes";
    public static final String PATIENTS_INDEXES = "./dbfiles/patientsIndexes";

    private IndexService patientIndexService;
    private IndexService admissionIndexService;
    public DBMS() {
        patientIndexService = new IndexService(PATIENTS_INDEXES);
        admissionIndexService = new IndexService(ADMISSION_INDEXES);
        System.out.println("DBMS started");
    }

    public void insertPatient(PatientEntity patient) {
        Index index = patientIndexService.getNewIndex(); // Index:{patient-Id, position-In-Table, deleted-mark}
        patient.setId(index.getEntityId());

        saveEntityToPositionIn(patient, index.getPos(), PATIENTS);

        patientIndexService.addIndex(index);
    }

    public void insertAdmission(AdmissionEntity addmission) {
        System.out.println("Я буду зберігати запис на дату " + addmission.getDate());

        Index index = admissionIndexService.getNewIndex();
        addmission.setId(index.getEntityId());
        saveEntityToPositionIn(addmission, index.getPos(), ADMISSIONS);

        admissionIndexService.addIndex(index);
    }

    public Entity[] getAllPatients() {
        return readFromFile(PATIENTS, new PatientEntity());
    }/*



    private static void writeEntityToFile(Entity entity, String fileName) {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(fileName, true))) {
            entity.saveYourselfTo(outputStream);
            System.out.println("Size: " + outputStream.size());
        } catch (IOException exception) {
            System.out.println("Помилка при запису обʼекта у файл: " + exception.getMessage());
        }
    }*/

    public void savePatient(PatientEntity patient) {
        int position = patientIndexService.getPosition(patient.getId());
        saveEntityToPositionIn(patient, position, PATIENTS);
    }

    private void saveEntityToPositionIn(Entity patient, int position, String fileName) {
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            file.seek(position * patient.getSizeInBytes());
            patient.saveYourselfTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAdmission(AdmissionEntity admission) {
        int position = admissionIndexService.getPosition(admission.getId());
        saveEntityToPositionIn(admission, position, ADMISSIONS);
        /*try (RandomAccessFile file = new RandomAccessFile(ADMISSIONS, "rw")) {
            file.seek((long) position * (long) admission.getSizeInBytes());
            admission.saveYourselfTo(file);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public Entity[] readAllAdmissions() {
        return  readFromFile(ADMISSIONS, new AdmissionEntity());
    }

    private Entity[] readFromFile(String fileName, Entity entity) {
        int count = 0;
        Entity[] entities = new Entity[100];
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName))) {
//            inputStream.skipBytes(24 * 3);
            while (inputStream.available() >= entity.getSizeInBytes()) {
                entity.loadYourselfFrom(inputStream);
                entities[count++] = entity.getClone();
            }
        } catch (IOException exception) {
            System.out.println("Помилка при читанні з файлу: " + fileName + " : " + exception.getMessage());
        }

        return Arrays.copyOfRange(entities, 0, count);
    }
    private Entity readFromFileOneEntity(String fileName, Entity entity, int position) {
        if (position < 0) {
            return null;
        }
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(fileName))) {
            inputStream.skipBytes(position * entity.getSizeInBytes());
            entity.loadYourselfFrom(inputStream);
            return entity.getClone();
        } catch (IOException e) {
            System.out.println("Помилка при читанні з файлу " + e.getMessage());
        }

        return null;
    }

    public void reindexPatients() {
        patientIndexService.recreateIndexFile(getIndices(getAllPatients()));
    }

    public void reindexAdmissions() {
        admissionIndexService.recreateIndexFile(getIndices(readAllAdmissions()));
    }

    private static Index[] getIndices(Entity[] entities) {
        Index[] indices = new Index[entities.length];

        for (int pos = 0; pos < entities.length; pos++) {
            indices[pos] = new Index(entities[pos].getId(), pos);
        }
        return indices;
    }

    public Index[] getPatientIndices() {
        return patientIndexService.getIndices();
    }

    public void savePatientIndices() {
        patientIndexService.saveToFile();
    }

    public void saveAdmissionsIndices() {
        admissionIndexService.saveToFile();
    }

    public Index[] getAddmissionIndices() {
        return admissionIndexService.getIndices();
    }

    public PatientEntity getPatient(int patientId) {
        int patientPos = patientIndexService.getPosition(patientId);

        return (PatientEntity) readFromFileOneEntity(PATIENTS, new PatientEntity(), patientPos);
    }
    public AdmissionEntity getAdmission(int id) {
        int position = admissionIndexService.getPosition(id);

        return (AdmissionEntity) readFromFileOneEntity(ADMISSIONS, new AdmissionEntity(), position);
    }

    public void deletePatient(int id) {
        PatientEntity patient = getPatient(id);
        if (patient == null) {
            return;
        }

        patient.markAsDeleted();
        savePatient(patient);
        patientIndexService.getIndexFor(id).setDeleted();
    }
    public void deleteAdmission(int id) {
        AdmissionEntity admission = getAdmission(id);
        if (admission == null) {
            return;
        }

        admission.markAsDeleted();
        saveAdmission(admission);
        admissionIndexService.getIndexFor(id).setDeleted();
    }

    public void clearDb() {
        String[] files = new String[]{ADMISSIONS, PATIENTS, ADMISSION_INDEXES, PATIENTS_INDEXES};

        for (String file : files) {
            System.out.println("Removing: " + file);
            try {
                Files.delete(Paths.get(file));
            } catch (IOException e) {
                System.out.println("clearDb error: " + e.getMessage());
            }
        }


    }

    public AdmissionEntity[] getAllAdmissionsFromFirst(int firstAdmissionId) {
        if (firstAdmissionId == -1) {
            return null;
        }
        AdmissionEntity[] admissions = new AdmissionEntity[IndexService.MAX_COUNT];
        int count = 0;
        int currentAdmissionId = firstAdmissionId;
        do {
            admissions[count] = getAdmission(currentAdmissionId);
            currentAdmissionId = admissions[count].getNextAdId();
            count++;
        } while (currentAdmissionId != -1);

        return Arrays.copyOfRange(admissions, 0, count);
    }

    public void deletePatientWithAdmissions(PatientEntity patient) {
        AdmissionEntity[] admissions = getAllAdmissionsFromFirst(patient.getFirstAdmissionId());

        deletePatient(patient.getId());
        for (AdmissionEntity admission : admissions) {
            deleteAdmission(admission.getId());
        }
    }
}
