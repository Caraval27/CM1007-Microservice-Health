package journal.lab3_health.Core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import journal.lab3_health.Core.Model.PatientData;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
public class HealthServiceTest {
    @InjectMocks
    private HealthService healthService;

    @MockBean
    private FhirContext fhirContext;

    @Mock
    private IGenericClient mockClient;

    private static final String HAPI_SERVER_URL = "https://hapi-fhir.app.cloud.cbh.kth.se/fhir";
    private static final String PATIENT_SYSTEM = "http://electronichealth.se/identifier/personnummer";
    private static final String PRACTITIONER_SYSTEM = "http://terminology.hl7.org/CodeSystem/v2-0203";
    private static final String PRACTITIONER_ROLE_SYSTEM = "http://terminology.hl7.org/CodeSystem/practitioner-role";
    private static final String CONDITION_SYSTEM = "http://snomed.info/sct";
    private static final String OBSERVATION_SYSTEM = "http://loinc.org";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(fhirContext.newRestfulGenericClient(anyString())).thenReturn(mockClient);
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

        IUntypedQuery mockQuery = mock(IUntypedQuery.class);
        IQuery mockQueryForResource = mock(IQuery.class);
        IQuery mockWhere = mock(IQuery.class);
        Bundle mockBundle = new Bundle();
        Patient mockPatient = new Patient();
        mockPatient.addIdentifier().setSystem(PATIENT_SYSTEM).setValue(identifierValue);
        mockPatient.addName().setFamily("Doe").addGiven("John");

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(mockPatient);
        mockBundle.addEntry(entry);

        when(mockClient.search()).thenReturn(mockQuery);
        when(mockQuery.forResource(Patient.class)).thenReturn(mockQueryForResource);
        when(mockQueryForResource.where((ICriterion<?>) any())).thenReturn(mockWhere);
        when(mockWhere.returnBundle(Bundle.class)).thenReturn(mockWhere);
        when(mockWhere.execute()).thenReturn(mockBundle);

        Patient result = healthService.getPatientByIdentifier(identifierValue);

        assertNotNull(result, "Patient should not be null");
        assertEquals(identifierValue, result.getIdentifierFirstRep().getValue(), "Patient identifier should match input value");
        assertEquals("Doe", result.getNameFirstRep().getFamily(), "Patient family name should match");
        assertEquals("John", result.getNameFirstRep().getGivenAsSingleString(), "Patient given name should match");
    }

    @Test
    void testGetPatientById() {
        String patientId = "12345";

        Patient mockPatient = new Patient();
        mockPatient.setId(patientId);
        mockPatient.addName().setFamily("Doe").addGiven("John");

        IRead mockRead = mock(IRead.class);
        IReadTyped<Patient> mockReadTyped = mock(IReadTyped.class);
        IReadExecutable<Patient> mockExecutable = mock(IReadExecutable.class);

        when(mockClient.read()).thenReturn(mockRead);
        when(mockRead.resource(Patient.class)).thenReturn(mockReadTyped);
        when(mockReadTyped.withId(patientId)).thenReturn(mockExecutable);
        when(mockExecutable.execute()).thenReturn(mockPatient);

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
        mockPatient.setId(patientIdentifier);
        mockPatient.addGeneralPractitioner().setReference("Practitioner/" + practitionerIdentifier);

        Practitioner mockPractitioner = new Practitioner();
        mockPractitioner.setId(practitionerIdentifier);
        mockPractitioner.addIdentifier()
                .setSystem(PRACTITIONER_SYSTEM)
                .setValue(practitionerIdentifier);

        IUntypedQuery mockQuery = mock(IUntypedQuery.class);
        IQuery mockQueryForResource = mock(IQuery.class);
        IQuery mockWhere = mock(IQuery.class);
        Bundle patientBundle = new Bundle();
        Bundle.BundleEntryComponent patientEntry = new Bundle.BundleEntryComponent();
        patientEntry.setResource(mockPatient);
        patientBundle.addEntry(patientEntry);

        when(mockClient.search()).thenReturn(mockQuery);
        when(mockQuery.forResource(Patient.class)).thenReturn(mockQueryForResource);
        when(mockQueryForResource.where((ICriterion<?>) any())).thenReturn(mockWhere);
        when(mockWhere.returnBundle(Bundle.class)).thenReturn(mockWhere);
        when(mockWhere.execute()).thenReturn(patientBundle);

        IRead mockRead = mock(IRead.class);
        IReadTyped<Practitioner> mockReadTyped = mock(IReadTyped.class);
        IReadExecutable<Practitioner> mockExecutable = mock(IReadExecutable.class);

        when(mockClient.read()).thenReturn(mockRead);
        when(mockRead.resource(Practitioner.class)).thenReturn(mockReadTyped);
        when(mockReadTyped.withId(practitionerIdentifier)).thenReturn(mockExecutable);
        when(mockExecutable.execute()).thenReturn(mockPractitioner);

        String result = healthService.getGeneralPractitionerByIdentifier(patientIdentifier);

        assertNotNull(result, "General Practitioner ID should not be null");
        assertEquals(practitionerIdentifier, result, "General Practitioner ID should match expected value");
    }

    //getPractitionerRoleByPractitionerId
}
