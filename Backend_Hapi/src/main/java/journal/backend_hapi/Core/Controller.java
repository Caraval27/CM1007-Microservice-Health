package journal.backend_hapi.Core;

import journal.backend_hapi.Core.Model.*;
import org.hl7.fhir.r4.model.Condition;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081", "http://journal-app-frontend.app.cloud.cbh.kth.se/"})
@RestController
public class Controller {
    @Autowired
    private HapiService hapiService;

    public Controller() {}

    @GetMapping("/hello")
    public String sayHello(@RequestParam String id) {
        Patient patient = hapiService.getPatientByIdentifier(id);
        return "Hello, World!";
    }

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

    @GetMapping("/patients")
    public ResponseEntity<List<PatientData>> getPatients() {
        try {
            List<Patient> patients = hapiService.getPatientsByIdentifierSystem();
            List<PatientData> patientsData = new ArrayList<>();
            for (Patient patient : patients) {
                patientsData.add(hapiService.getPatientData(patient));
            }
            return ResponseEntity.ok(patientsData);
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
    public ResponseEntity<Void> createNewObservation(@RequestBody CreateObservation newObservation) {
        try {
            hapiService.addObservationToPatient(newObservation);
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