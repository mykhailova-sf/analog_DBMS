package somihmih;

import somihmih.er.entity.Entity;
import somihmih.er.entity.PatientEntity;
import somihmih.er.entity.AdmissionEntity;
import somihmih.er.indexservice.Index;

public class ClinicDbService {

    private final DBMS dbms = new DBMS();

    public void insertPatient(PatientEntity patient) {
        System.out.println("Я буду зберігати пацієнта " + patient.getName());
        dbms.insertPatient(patient);
        System.out.println("ОК - Пацієнт збережений");
    }

    public void readAllPatients() {
        for(Entity entity : dbms.getAllPatients()) {
            System.out.println(entity.toString());
        }
    }

    public void reindexAdmission() {
        System.out.println("Переіндексація прийому...");
        dbms.reindexAdmissions();
    }

    public void reindexPatients() {
        System.out.println("Переіндексація прийому...");
        dbms.reindexPatients();
    }

    public void showPatientIndexes() {
        System.out.println("Індекси пацієнтів");
        Index[] indexes = dbms.getPatientIndices();
        showIndices(indexes);
    }

    public void showAddmissionIndexes() {
        System.out.println("Індекси записів");
        showIndices(dbms.getAddmissionIndices());
    }

    private static void showIndices(Index[] indices) {
        for (Index index : indices) {
            System.out.println(index);
        }
    }

    public void saveIndices() {
        System.out.println("Зберігаю індекси пацієнтів...");
        dbms.savePatientIndices();
        System.out.println("Зберігаю індекси пацієнтів...");
        dbms.saveAdmissionsIndices();
    }

    public void insertAdmission(int patientId, AdmissionEntity newAdmission) {
        PatientEntity patient = getPatient(patientId);
        if (patient == null) {
            return;
        }
        dbms.insertAdmission(newAdmission);

        AdmissionEntity[] admissions = dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId());

        if (admissions == null) {
            patient.setAdmissionId(newAdmission.getId());
            dbms.savePatient(patient);
            return;
        }

        AdmissionEntity lastAdmission = admissions[admissions.length - 1];
        lastAdmission.setNextAdId(newAdmission.getId());
        dbms.saveAdmission(lastAdmission);
    }

    public void readAllAdmissions() {
        for(Entity entity : dbms.readAllAdmissions()) {
            System.out.println(entity.toString());
        }
    }

    public void reindexAll() {
        reindexPatients();
        reindexAdmission();
    }

    public void updatePatient(PatientEntity patient) {
        if (getPatient(patient.getId()) == null) {
            return;
        }

        dbms.savePatient(patient);
    }

    public void saveAdmission(AdmissionEntity admission) {
        if (dbms.getAdmission(admission.getId()) == null) {
            System.out.println("З таким id нічого не знайдено");
            return;
        }

        dbms.saveAdmission(admission);
    }

    public void deletePatient(int id) {
        dbms.deletePatient(id);
    }

    public void deleteAdmission(int id) {
        dbms.deleteAdmission(id);
    }

    public void clearDb() {
        dbms.clearDb();
    }

    public void showPatientWithAdmission(int id) {
        PatientEntity patient = getPatient(id);
        if (patient == null) {
            return;
        }

        System.out.println("Patient " + patient.getName() + ", tel:" + patient.getPhoneNumber());

        AdmissionEntity[] admissions = dbms.getAllAdmissionsFromFirst(patient.getFirstAdmissionId());

        if (admissions == null) {
            System.out.println("Admissions empty");
            return;
        }

        for (AdmissionEntity admission : admissions) {
            System.out.println(admission.toString());
        }
    }

    public void deletePatientWithAdmissions(int id) {
        PatientEntity patient = getPatient(id);
        if (patient == null) {
            return;
        }

        dbms.deletePatientWithAdmissions(patient);


    }

    private PatientEntity getPatient(int id) {
        PatientEntity patient = dbms.getPatient(id);
        if (patient == null) {
            System.out.println("Patient not found");
        }

        return patient;
    }
}
