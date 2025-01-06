package somihmih;

import somihmih.er.entity.PatientEntity;
import somihmih.er.entity.AdmissionEntity;

import java.util.Scanner;


public class Starter {

    public static void main(String[] args) {
        System.out.println("MyDB starting....");

        ClinicDbService clinicDbService = new ClinicDbService();
        Scanner scanner = new Scanner(System.in);

        printCommandMenu();
        String command;
        do {
            command = scanner.nextLine();
            switch (command) {
                case "0" -> printCommandMenu();
                case "1" -> newPatient(scanner, clinicDbService);
                case "10" -> updatePatient(scanner, clinicDbService);
                case "11" -> newAdmission(scanner, clinicDbService);
                case "110" -> updateAddmission(scanner, clinicDbService);
                case "2" -> clinicDbService.readAllPatients();
                case "22" -> clinicDbService.readAllAdmissions();
                case "222" -> showPatientWithAdmisions(scanner, clinicDbService);
                case "3" -> clinicDbService.reindexPatients();
                case "33" -> clinicDbService.reindexAdmission();
                case "333" -> clinicDbService.reindexAll();
                case "4" -> clinicDbService.showPatientIndexes();
                case "44" -> clinicDbService.showAddmissionIndexes();
                case "5" -> deletePatient(scanner, clinicDbService);
                case "55" -> deleteAdmission(scanner, clinicDbService);
                case "555" -> deletePatientWithAdmissions(scanner, clinicDbService);
                case "666" -> clinicDbService.clearDb();
                case "#" -> clinicDbService.saveIndices();
                default -> System.out.println("Ви ввели щось невідоме");
            }
        } while (!command.equals("#") && !command.equals("666"));

        System.out.println("End");
    }

    private static void deletePatientWithAdmissions(Scanner scanner, ClinicDbService clinicDbService) {
        System.out.println("Введи ID пацієнта:");
        int id = Integer.parseInt(scanner.nextLine());
        clinicDbService.deletePatientWithAdmissions(id);
    }

    private static void showPatientWithAdmisions(Scanner scanner, ClinicDbService clinicDbService) {
        System.out.println("Введи ID пацієнта:");
        int id = Integer.parseInt(scanner.nextLine());

        clinicDbService.showPatientWithAdmission(id);
    }

    private static void deleteAdmission(Scanner scanner, ClinicDbService dbService) {
    }

    private static void deletePatient(Scanner scanner, ClinicDbService dbService) {
        System.out.print("Введи ID пацієнта: ");
        int id = Integer.parseInt(scanner.nextLine());

        dbService.deletePatient(id);
    }

    private static void newAdmission(Scanner scanner, ClinicDbService dbService) {
        System.out.print("Введи Id пацієнта і дату через кому (8 цифр у форматі ddmmyyyy): ");  // TODO зробити перевірку введених данних
        String[] adParams = scanner.nextLine().split(",");
        int patientId = Integer.parseInt(adParams[0]);
        String date = adParams[1];

        dbService.insertAdmission(patientId, new AdmissionEntity(date));
    }

    private static void newPatient(Scanner scanner, ClinicDbService dbService) {
        System.out.print("Введи імʼя і телефон пацієнта через кому: ");
        String[] patientField = scanner.nextLine().split(",");

        String name = patientField[0];
        String phoneNumber = patientField[1];
        PatientEntity patient = new PatientEntity(name, phoneNumber);

        dbService.insertPatient(patient);
    }

    private static void updateAddmission(Scanner scanner, ClinicDbService clinicDbService) {
        System.out.print("Введи Id запису, нову дату через кому (8 цифр у форматі ddmmyyyy) и ціну (-1 якщо ціна ще невідома): ");
        String[] adParams = scanner.nextLine().split(",");
        int id = Integer.parseInt(adParams[0]);
        String date = adParams[1];
        int price = Integer.parseInt(adParams[1]);

        AdmissionEntity admission = new AdmissionEntity(date);
        admission.setId(id);
        admission.setPrice(price);

        clinicDbService.saveAdmission(admission);
    }

    private static void updatePatient(Scanner scanner, ClinicDbService clinicDbService) {
        System.out.print("Введи ID, змінене імʼя або телефон пацієнта: ");
        // 1,Max,+5454
        String[] patientField = scanner.nextLine().split(",");
        String id = patientField[0]; // 1
        String name = patientField[1]; // Max
        String phoneNumber = patientField[2]; // +5454

        PatientEntity patient = new PatientEntity(Integer.parseInt(id), name, phoneNumber);

        clinicDbService.updatePatient(patient);
    }

    private static void printCommandMenu() {
        System.out.println("Insert       - new Patient: 1, new Admission: 11");
        System.out.println("Update       - Patient: 10, Admission: 110");
        System.out.println("Show All     - Patients: 2, Admissions: 22");
        System.out.println("Show         -  Patient by ID with his admissions: 222");
        System.out.println("Reindex      - Patients: 3, Admission: 33, All: 333");

        System.out.println("Show Indexes - Patient: 4, Admissions 44");
        System.out.println("Delete by Id - Patient: 5, Admissions: 55");
        System.out.println("Delete All DB files and exit: 666");
        System.out.println("Show help: 0");
        System.out.println("# - Exit");
    }
}