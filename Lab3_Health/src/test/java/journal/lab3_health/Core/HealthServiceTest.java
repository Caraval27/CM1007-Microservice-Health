package journal.lab3_health.Core;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import journal.lab3_health.Core.Model.PatientData;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class HealthServiceTest {
    private HealthService healthService;
    private IGenericClient mockClient;

    private static final String HAPI_SERVER_URL = "https://hapi-fhir.app.cloud.cbh.kth.se/fhir";
    private static final String PATIENT_SYSTEM = "http://electronichealth.se/identifier/personnummer";
    private static final String PRACTITIONER_SYSTEM = "http://terminology.hl7.org/CodeSystem/v2-0203";
    private static final String PRACTITIONER_ROLE_SYSTEM = "http://terminology.hl7.org/CodeSystem/practitioner-role";
    private static final String CONDITION_SYSTEM = "http://snomed.info/sct";
    private static final String OBSERVATION_SYSTEM = "http://loinc.org";

    @BeforeEach
    void setUp() {
        mockClient = mock(IGenericClient.class);
        healthService = new HealthService() {
            @Override
            protected IGenericClient getClient() {
                return mockClient;
            }
        };
    }

    @Test
    void testGetPatientData() {
        Patient mockPatient = new Patient();
        mockPatient.addIdentifier().setSystem(PATIENT_SYSTEM).setValue("12345");
        mockPatient.addName().setFamily("Doe").addGiven("John");
        mockPatient.setGender(Enumerations.AdministrativeGender.MALE);

        PatientData patientData = healthService.getPatientData(mockPatient);

        assertNotNull(patientData, "Patient data should not be null");
        assertEquals("12345", patientData.getId(), "Patient identifier should match input value");
        assertEquals("John Doe", patientData.getFullName(), "Patient full name should match");
        assertEquals("Male", patientData.getGender().name(), "Patient gender should match");
    }

    @Test
    void testGetPatientByIdentifier() {
        String identifierValue = "12345";

        Patient mockPatient = new Patient();
        mockPatient.addIdentifier().setSystem(PATIENT_SYSTEM).setValue(identifierValue);
        mockPatient.addName().setFamily("Doe").addGiven("John");

        Bundle mockBundle = new Bundle();
        mockBundle.addEntry().setResource(mockPatient);

        when(mockClient
                .search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(PATIENT_SYSTEM, identifierValue))
                .returnBundle(Bundle.class)
                .execute())
                .thenReturn(mockBundle);

        Patient patient = healthService.getPatientByIdentifier(identifierValue);

        assertNotNull(patient, "Patient should not be null");
        assertEquals(identifierValue, patient.getIdentifierFirstRep().getValue(), "Patient identifier should match input value");
        assertEquals("Doe", patient.getNameFirstRep().getFamily(), "Patient family name should match");
        assertEquals("John", patient.getNameFirstRep().getGivenAsSingleString(), "Patient given name should match");
    }

    @Test
    void testGetPatientById() {
        String patientId = "12345";

        Patient mockPatient = new Patient();
        mockPatient.setId(patientId);
        mockPatient.addName().setFamily("Doe").addGiven("John");

        when(mockClient.read()
                .resource(Patient.class)
                .withId(patientId)
                .execute())
                .thenReturn(mockPatient);

        Patient result = healthService.getPatientById(patientId);

        assertNotNull(result, "The returned Patient should not be null");
        assertEquals(patientId, result.getIdElement().getIdPart(), "The Patient ID should match the input ID");
        assertEquals("Doe", result.getNameFirstRep().getFamily(), "The Patient family name should match");
        assertEquals("John", result.getNameFirstRep().getGivenAsSingleString(), "The Patient given name should match");
    }

    //getPractitionerData

    @Test
    void testGetGeneralPractitionerByIdentifier() {
        String patientIdentifier = "12345";
        String practitionerIdentifier = "67890";

        Patient mockPatient = new Patient();
        mockPatient.setId("1");
        mockPatient.addGeneralPractitioner().setReference("Practitioner/" + practitionerIdentifier);

        Practitioner mockPractitioner = new Practitioner();
        mockPractitioner.setId("67890");
        mockPractitioner.addIdentifier().setSystem(PRACTITIONER_SYSTEM).setValue(practitionerIdentifier);

        when(mockClient
                .read()
                .resource(Patient.class)
                .withId("1")
                .execute())
                .thenReturn(mockPatient);

        when(mockClient
                .read()
                .resource(Practitioner.class)
                .withId(practitionerIdentifier)
                .execute())
                .thenReturn(mockPractitioner);

        String result = healthService.getGeneralPractitionerByIdentifier(patientIdentifier);

        assertEquals(practitionerIdentifier, result);
    }

    //getPractitionerRoleByPractitionerId
}
