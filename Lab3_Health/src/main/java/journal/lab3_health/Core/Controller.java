package journal.lab3_health.Core;

import journal.lab3_health.Core.Model.*;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081",
        "https://journal-app-frontend.app.cloud.cbh.kth.se",
        "https://journal-app-message.app.cloud.cbh.kth.se"})
@RestController
public class Controller {
    @Autowired
    private HealthService healthService;

    @Autowired
    private ImageServiceClient imageServiceClient;

    public Controller() {}

    @GetMapping("/patient")
    public ResponseEntity<PatientData> getPatient(@RequestParam String id) {
        try {
            Patient patient = healthService.getPatientByIdentifier(id);
            System.out.println(patient.getName().get(0).getNameAsSingleString());
            PatientData patientData = healthService.getPatientData(patient);
            System.out.println(patientData.getFullName());
            return ResponseEntity.ok(patientData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/practitioner")
    public ResponseEntity<PractitionerData> getPractitioner(@RequestParam String id) {
        try {
            Practitioner practitioner = healthService.getPractitionerByIdentifier(id);
            PractitionerData practitionerData = healthService.getPractitionerData(practitioner);
            return ResponseEntity.ok(practitionerData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/find_patient")
    public ResponseEntity<User> getPatientUser(@RequestParam String id) {
        try {
            User patientUser = healthService.getPatientUserByIdentifier(id);
            if (patientUser == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(patientUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/find_practitioner")
    public ResponseEntity<User> getPractitionerUser(@RequestParam String id) {
        try {
            User practitionerUser = healthService.getPractitionerUserByIdentifier(id);
            if (practitionerUser == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(practitionerUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/observations")
    public ResponseEntity<List<ObservationData>> getObservations(@RequestParam String id) {
        try {
            List<Observation> observations = healthService.getObservationsByPatientIdentifier(id);
            List<ObservationData> observationsData = new ArrayList<>();
            for (Observation observation : observations) {
                observationsData.add(healthService.getObservationData(observation));
            }
            return ResponseEntity.ok(observationsData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create_observation")
    public ResponseEntity<Void> createNewObservation(@RequestPart("observation") CreateObservation newObservation,
                                                     @RequestPart(value = "image", required = false) MultipartFile image) {
        try {
            String imageId = null;
            if (image != null) {
                imageId = imageServiceClient.createBinary(image);
            }
            System.out.println("Binary created with id: " + imageId);
            healthService.addObservationToPatient(newObservation, imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conditions")
    public ResponseEntity<List<ConditionData>> getConditions(@RequestParam String id) {
        try {
            List<Condition> conditions = healthService.getConditionsByPatientIdentifier(id);
            List<ConditionData> conditionsData = new ArrayList<>();
            for (Condition condition : conditions) {
                conditionsData.add(healthService.getConditionData(condition));
            }
            return ResponseEntity.ok(conditionsData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create_condition")
    public ResponseEntity<Void> createNewCondition(@RequestBody CreateCondition newCondition) {
        try {
            healthService.addConditionToPatient(newCondition);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/encounters")
    public ResponseEntity<List<EncounterData>> getEncounters(@RequestParam String id) {
        try {
            List<Encounter> encounters = healthService.getEncountersByPractitionerIdentifier(id);
            List<EncounterData> encountersData = new ArrayList<>();
            for (Encounter encounter : encounters) {
                EncounterData encounterData = healthService.getEncounterData(encounter);
                encountersData.add(encounterData);
            }
            return ResponseEntity.ok(encountersData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get_general_practitioner_by_identifier")
    public ResponseEntity<String> getGeneralPractitionerByIdentifier(@RequestParam String id) {
        try {
            String receiverIdentifier = healthService.getGeneralPractitionerByIdentifier(id);
            return ResponseEntity.ok(receiverIdentifier);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}