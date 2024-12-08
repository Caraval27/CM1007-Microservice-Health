package journal.backend_hapi.Core;

import journal.backend_hapi.Core.Model.*;
import org.hl7.fhir.r4.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081",
        "https://journal-app-frontend.app.cloud.cbh.kth.se:3000",
        "https://journal-app-message.app.cloud.cbh.kth.se:8081"})
@RestController
public class Controller {
    @Autowired
    private HapiService hapiService;

    @Autowired
    private ImageServiceClient imageServiceClient;

    public Controller() {}

    @GetMapping("/patient")
    public ResponseEntity<PatientData> getPatient(@RequestParam String id) {
        try {
            Patient patient = hapiService.getPatientByIdentifier(id);
            System.out.println(patient.getName().get(0).getNameAsSingleString());
            PatientData patientData = hapiService.getPatientData(patient);
            System.out.println(patientData.getFullName());
            return ResponseEntity.ok(patientData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/practitioner")
    public ResponseEntity<PractitionerData> getPractitioner(@RequestParam String id) {
        try {
            Practitioner practitioner = hapiService.getPractitionerByIdentifier(id);
            PractitionerData practitionerData = hapiService.getPractitionerData(practitioner);
            return ResponseEntity.ok(practitionerData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/find_patient")
    public ResponseEntity<User> getPatientUser(@RequestParam String id) {
        try {
            User patientUser = hapiService.getPatientUserByIdentifier(id);
            if (patientUser == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(patientUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/find_practitioner")
    public ResponseEntity<User> getPractitionerUser(@RequestParam String id) {
        try {
            User practitionerUser = hapiService.getPractitionerUserByIdentifier(id);
            if (practitionerUser == null) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(practitionerUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/observations")
    public ResponseEntity<List<ObservationData>> getObservations(@RequestParam String id) {
        try {
            List<Observation> observations = hapiService.getObservationsByPatientIdentifier(id);
            List<ObservationData> observationsData = new ArrayList<>();
            for (Observation observation : observations) {
                observationsData.add(hapiService.getObservationData(observation));
            }
            return ResponseEntity.ok(observationsData);
        } catch (Exception e) {
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
            hapiService.addObservationToPatient(newObservation, imageId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/conditions")
    public ResponseEntity<List<ConditionData>> getConditions(@RequestParam String id) {
        try {
            List<Condition> conditions = hapiService.getConditionsByPatientIdentifier(id);
            List<ConditionData> conditionsData = new ArrayList<>();
            for (Condition condition : conditions) {
                conditionsData.add(hapiService.getConditionData(condition));
            }
            return ResponseEntity.ok(conditionsData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/create_condition")
    public ResponseEntity<Void> createNewCondition(@RequestBody CreateCondition newCondition) {
        try {
            hapiService.addConditionToPatient(newCondition);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/encounters")
    public ResponseEntity<List<EncounterData>> getEncounters(@RequestParam String id) {
        try {
            List<Encounter> encounters = hapiService.getEncountersByPractitionerIdentifier(id);
            List<EncounterData> encountersData = new ArrayList<>();
            for (Encounter encounter : encounters) {
                EncounterData encounterData = hapiService.getEncounterData(encounter);
                encountersData.add(encounterData);
            }
            return ResponseEntity.ok(encountersData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get_general_practitioner_by_identifier")
    public ResponseEntity<String> getGeneralPractitionerByIdentifier(@RequestParam String id) {
        try {
            String receiverIdentifier = hapiService.getGeneralPractitionerByIdentifier(id);
            return ResponseEntity.ok(receiverIdentifier);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}