package journal.lab3_health.Core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.*;
import journal.lab3_health.Core.Model.*;
import org.hl7.fhir.r4.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

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
        String identifierValue = "12345";
        String patientName = "John Doe";

        Patient mockPatient = createMockPatient(identifierValue, patientName);
        mockPatient.setGender(Enumerations.AdministrativeGender.FEMALE);

        PatientData patientData = healthService.getPatientData(mockPatient);

        assertNotNull(patientData, "Patient data should not be null");
        assertEquals(identifierValue, patientData.getId(), "Patient identifier should match input value");
        assertEquals(patientName, patientData.getFullName(), "Patient full name should match");
        assertEquals("Female", patientData.getGender().name(), "Patient gender should match");
    }

    @Test
    void testGetPatientByIdentifier() {
        String identifierValue = "12345";
        String patientName = "Jane Doe";

        Patient mockPatient = createMockPatient(identifierValue, patientName);

        mockPatientSearch(mockPatient);

        Patient patient = healthService.getPatientByIdentifier(identifierValue);

        assertNotNull(patient, "Patient should not be null");
        assertEquals(identifierValue, patient.getIdentifierFirstRep().getValue(), "Patient identifier value should match input value");
        assertEquals("Doe", patient.getNameFirstRep().getFamily(), "Patient family name should match");
        assertEquals("Jane", patient.getNameFirstRep().getGivenAsSingleString(), "Patient given name should match");
    }

    @Test
    void testGetPatientById() {
        String identifierValue = "12345";
        String patientName = "Jane Doe";
        String patientId = "123";

        Patient mockPatient = createMockPatient(identifierValue, patientName);
        mockPatient.setId(patientId);

        IRead mockRead = mock(IRead.class);
        IReadTyped<Patient> mockReadTyped = mock(IReadTyped.class);
        IReadExecutable<Patient> mockExecutable = mock(IReadExecutable.class);

        when(mockClient.read()).thenReturn(mockRead);
        when(mockRead.resource(Patient.class)).thenReturn(mockReadTyped);
        when(mockReadTyped.withId(patientId)).thenReturn(mockExecutable);
        when(mockExecutable.execute()).thenReturn(mockPatient);

        Patient patient = healthService.getPatientById(patientId);

        assertNotNull(patient, "The returned Patient should not be null");
        assertEquals(patientId, patient.getIdElement().getIdPart(), "The Patient ID should match the input ID");
        assertEquals("Doe", patient.getNameFirstRep().getFamily(), "The Patient family name should match");
        assertEquals("Jane", patient.getNameFirstRep().getGivenAsSingleString(), "The Patient given name should match");
    }

    @Test
    void testGetPractitionerData() {
        String hsaId = "12345";
        String phone = "555-1234";
        String email = "test@example.com";
        String fullName = "John Doe";
        String roleDisplay = "Doctor";

        Practitioner mockPractitioner = createMockPractitioner(hsaId, fullName);
        mockPractitioner.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.PHONE)
                .setValue(phone);
        mockPractitioner.addTelecom()
                .setSystem(ContactPoint.ContactPointSystem.EMAIL)
                .setValue(email);

        PractitionerRole mockPractitionerRole = new PractitionerRole();
        CodeableConcept mockCodeableConcept = new CodeableConcept();
        mockCodeableConcept.addCoding(new Coding()
                .setSystem(PRACTITIONER_ROLE_SYSTEM)
                .setDisplay(roleDisplay));
        mockPractitionerRole.addCode(mockCodeableConcept);

        mockPractitionerRoleSearch(mockPractitionerRole);

        PractitionerData practitionerData = healthService.getPractitionerData(mockPractitioner);

        assertNotNull(practitionerData, "PractitionerData should not be null");
        assertEquals(hsaId, practitionerData.getId(), "HSA ID should match");
        assertEquals(phone, practitionerData.getPhone(), "Phone number should match");
        assertEquals(email, practitionerData.getEmail(), "Email should match");
        assertEquals(fullName, practitionerData.getFullName(), "Full name should match");
        assertEquals(roleDisplay, practitionerData.getRole(), "Role should match");
    }

    @Test
    void testGetPractitionerRoleByPractitionerId() {
        String practitionerId = "12345";
        String roleDisplay = "Doctor";

        PractitionerRole mockPractitionerRole = new PractitionerRole();
        CodeableConcept mockCodeableConcept = new CodeableConcept();
        mockCodeableConcept.addCoding(new Coding()
                .setSystem(PRACTITIONER_ROLE_SYSTEM)
                .setDisplay(roleDisplay));
        mockPractitionerRole.addCode(mockCodeableConcept);

        mockPractitionerRoleSearch(mockPractitionerRole);

        List<PractitionerRole> practitionerRoles = healthService.getPractitionerRoleByPractitionerId(practitionerId);

        assertNotNull(practitionerRoles, "Result should not be null");
        assertEquals(1, practitionerRoles.size(), "Result should contain one PractitionerRole");
        assertEquals(roleDisplay, practitionerRoles.get(0).getCodeFirstRep().getCodingFirstRep().getDisplay(), "Role display should match");
    }

    @Test
    void testGetPractitionerByIdentifier() {
        String identifierValue = "12345";

        Practitioner mockPractitioner = createMockPractitioner(identifierValue, "Jane Doe");

        IUntypedQuery mockQuery = mock(IUntypedQuery.class);
        IQuery mockQueryForResource = mock(IQuery.class);
        IQuery mockWhere = mock(IQuery.class);
        Bundle mockBundle = new Bundle();
        if (mockPractitioner != null) {
            Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
            entry.setResource(mockPractitioner);
            mockBundle.addEntry(entry);
        }

        when(mockClient.search()).thenReturn(mockQuery);
        when(mockQuery.forResource(Practitioner.class)).thenReturn(mockQueryForResource);
        when(mockQueryForResource.where((ICriterion<?>) any())).thenReturn(mockWhere);
        when(mockWhere.returnBundle(Bundle.class)).thenReturn(mockWhere);
        when(mockWhere.execute()).thenReturn(mockBundle);

        Practitioner practitioner = healthService.getPractitionerByIdentifier(identifierValue);

        assertNotNull(practitioner, "Patient should not be null");
        assertEquals(identifierValue, practitioner.getIdentifierFirstRep().getValue(), "Practitioner identifier value should match input value");
    }

    @Test
    void testGetPractitionerById() {
        String practitionerId = "67890";

        Practitioner mockPractitioner = new Practitioner();
        mockPractitioner.setId(practitionerId);

        IRead mockRead = mock(IRead.class);
        IReadTyped<Practitioner> mockReadTyped = mock(IReadTyped.class);
        IReadExecutable<Practitioner> mockExecutable = mock(IReadExecutable.class);

        when(mockClient.read()).thenReturn(mockRead);
        when(mockRead.resource(Practitioner.class)).thenReturn(mockReadTyped);
        when(mockReadTyped.withId(practitionerId)).thenReturn(mockExecutable);
        when(mockExecutable.execute()).thenReturn(mockPractitioner);

        Practitioner practitioner = healthService.getPractitionerById(practitionerId);

        assertNotNull(practitioner, "Result should not be null");
        assertEquals(practitionerId, practitioner.getIdElement().getIdPart(), "Practitioner ID should match");
    }

    @Test
    void testGetGeneralPractitionerByIdentifier() {
        String patientIdentifier = "12345";
        String practitionerIdentifier = "67890";

        Patient mockPatient = new Patient();
        mockPatient.setId(patientIdentifier);
        mockPatient.addGeneralPractitioner().setReference("Practitioner/" + practitionerIdentifier);

        Practitioner mockPractitioner = createMockPractitioner(practitionerIdentifier, "Jane Doe");
        mockPractitioner.setId(practitionerIdentifier);

        mockPatientSearch(mockPatient);

        IRead mockRead = mock(IRead.class);
        IReadTyped<Practitioner> mockReadTyped = mock(IReadTyped.class);
        IReadExecutable<Practitioner> mockExecutable = mock(IReadExecutable.class);

        when(mockClient.read()).thenReturn(mockRead);
        when(mockRead.resource(Practitioner.class)).thenReturn(mockReadTyped);
        when(mockReadTyped.withId(practitionerIdentifier)).thenReturn(mockExecutable);
        when(mockExecutable.execute()).thenReturn(mockPractitioner);

        String generalPractitioner = healthService.getGeneralPractitionerByIdentifier(patientIdentifier);

        assertNotNull(generalPractitioner, "General Practitioner ID should not be null");
        assertEquals(practitionerIdentifier, generalPractitioner, "General Practitioner ID should match expected value");
    }

    @Test
    void testGetPatientOrPractitionerNameByIdentifier() {
        String identifierValue = "12345";
        String patientName = "John Doe";
        String practitionerName = "Samantha Smith";

        Patient mockPatient = createMockPatient(identifierValue, patientName);
        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientByIdentifier(identifierValue);

        Practitioner mockPractitioner = createMockPractitioner(identifierValue, practitionerName);
        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerByIdentifier(identifierValue);

        String name = healthService.getPatientOrPractitionerNameByIdentifier(identifierValue);
        assertNotNull(name, "Name should not be null");
        assertEquals(patientName, name, "Name should match the patient's name");

        Mockito.doReturn(null).when(healthService).getPatientByIdentifier(identifierValue);
        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerByIdentifier(identifierValue);

        name = healthService.getPatientOrPractitionerNameByIdentifier(identifierValue);
        assertNotNull(name, "Name should not be null");
        assertEquals(practitionerName, name, "Name should match the practitioner's name");

        Mockito.doReturn(null).when(healthService).getPractitionerByIdentifier(identifierValue);

        name = healthService.getPatientOrPractitionerNameByIdentifier(identifierValue);
        assertNull(name, "Name should be null if no patient or practitioner is found");
    }

    @Test
    void testGetObservationsByPatientIdentifier() {
        String patientIdentifier = "12345";
        String observationId = "67890";

        Patient mockPatient = createMockPatient(patientIdentifier, "Jane Doe");

        Observation mockObservation = new Observation();
        mockObservation.setId(observationId);

        Bundle mockBundle = new Bundle();
        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(mockObservation);
        mockBundle.addEntry(entry);

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientByIdentifier(patientIdentifier);

        setupMockQueryChain(Observation.class, mockBundle);

        ReflectionTestUtils.setField(healthService, "client", mockClient);

        List<Observation> observations = healthService.getObservationsByPatientIdentifier(patientIdentifier);

        assertNotNull(observations, "Observations should not be null");
        assertEquals(1, observations.size(), "There should be one observation");
        assertEquals(observationId, observations.get(0).getIdElement().getIdPart(), "Observation ID should match");
    }

    @Test
    void testGetObservationData() {
        String observationId = "123";
        String patientId = "456";
        String performerId = "789";

        Observation mockObservation = new Observation();
        mockObservation.setId(observationId);
        mockObservation.setStatus(Observation.ObservationStatus.FINAL);
        mockObservation.setEffective(new DateTimeType(new Date()));
        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(OBSERVATION_SYSTEM).setDisplay("Blood Pressure");
        codeableConcept.addCoding(coding);
        mockObservation.setCode(codeableConcept);
        mockObservation.setValue(new Quantity().setValue(120).setUnit("mmHg"));
        mockObservation.setSubject(new Reference("Patient/" + patientId));
        mockObservation.addPerformer(new Reference("Practitioner/" + performerId));

        Patient mockPatient = createMockPatient(patientId, "John Doe");
        PatientData mockPatientData = new PatientData(patientId, "John Doe", Gender.Male, "", "", "", "", "");

        Practitioner mockPractitioner = createMockPractitioner(performerId, "Samantha Smith");
        PractitionerData mockPractitionerData = new PractitionerData(performerId, "Samantha Smith", "", "", "");

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientById(patientId);
        Mockito.doReturn(mockPatientData).when(healthService).getPatientData(mockPatient);

        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerById(performerId);
        Mockito.doReturn(mockPractitionerData).when(healthService).getPractitionerData(mockPractitioner);

        ObservationData observationData = healthService.getObservationData(mockObservation);

        assertNotNull(observationData, "ObservationData should not be null");
        assertEquals(observationId, observationData.getId(), "Observation ID should match");
        assertEquals("Blood Pressure", observationData.getDisplay(), "Observation display should match");
        assertEquals("120", observationData.getValue(), "Observation value should match");
        assertEquals("mmHg", observationData.getUnit(), "Observation unit should match");
        assertNotNull(observationData.getPatient(), "Patient data should not be null");
        assertNotNull(observationData.getPerformer(), "Performer data should not be null");
    }

    @Test
    void testAddObservationToPatient() {
        CreateObservation mockNewObservation = new CreateObservation(
                "patient123", "1234", "Blood Pressure", "120", "mmHg",
                "This is a test observation note.",  "practitioner456"
        );

        String binaryId = "binary789";

        Patient mockPatient = new Patient();
        mockPatient.setId("patient123");

        Practitioner mockPractitioner = new Practitioner();
        mockPractitioner.setId("practitioner456");

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientByIdentifier("patient123");
        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerByIdentifier("practitioner456");

        ICreate mockCreate = mock(ICreate.class);
        ICreateTyped mockCreateTyped = mock(ICreateTyped.class);

        when(mockClient.create()).thenReturn(mockCreate);
        when(mockCreate.resource(any(Observation.class))).thenReturn(mockCreateTyped);

        MethodOutcome mockOutcome = new MethodOutcome();
        mockOutcome.setCreated(true);
        mockOutcome.setId(new IdType("Observation/456"));
        when(mockCreateTyped.execute()).thenReturn(mockOutcome);

        ReflectionTestUtils.setField(healthService, "client", mockClient);

        healthService.addObservationToPatient(mockNewObservation, binaryId);

        verify(mockClient.create(), times(1)).resource(any(Observation.class));
        verify(mockCreateTyped, times(1)).execute();
        System.out.println("Observation created with ID: " + mockOutcome.getId().getIdPart());
    }

    @Test
    void testGetConditionsByPatientIdentifier() {
        String patientIdentifier = "12345";
        String conditionId = "67890";

        Patient mockPatient = createMockPatient(patientIdentifier, "Jane Doe");

        Condition mockCondition = new Condition();
        mockCondition.setId(conditionId);

        Bundle mockBundle = new Bundle();
        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(mockCondition);
        mockBundle.addEntry(entry);

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientByIdentifier(patientIdentifier);

        setupMockQueryChain(Condition.class, mockBundle);

        ReflectionTestUtils.setField(healthService, "client", mockClient);

        List<Condition> conditions = healthService.getConditionsByPatientIdentifier(patientIdentifier);

        assertNotNull(conditions, "Conditions should not be null");
        assertEquals(1, conditions.size(), "There should be one condition");
        assertEquals(conditionId, conditions.get(0).getIdElement().getIdPart(), "Condition ID should match");
    }

    @Test
    void testGetConditionData() {
        String conditionId = "123";
        String patientId = "456";
        String recorderId = "789";

        Condition mockCondition = new Condition();
        mockCondition.setId(conditionId);
        mockCondition.setRecordedDate(new Date());

        CodeableConcept codeableConcept = new CodeableConcept();
        Coding coding = new Coding();
        coding.setSystem(CONDITION_SYSTEM).setDisplay("Hypertension");
        codeableConcept.addCoding(coding);
        mockCondition.setCode(codeableConcept);

        CodeableConcept clinicalStatus = new CodeableConcept();
        clinicalStatus.addCoding(new Coding().setDisplay("Active"));
        mockCondition.setClinicalStatus(clinicalStatus);

        CodeableConcept verificationStatus = new CodeableConcept();
        verificationStatus.addCoding(new Coding().setDisplay("Confirmed"));
        mockCondition.setVerificationStatus(verificationStatus);

        CodeableConcept severity = new CodeableConcept();
        severity.addCoding(new Coding().setDisplay("Severe"));
        mockCondition.setSeverity(severity);

        mockCondition.setSubject(new Reference("Patient/" + patientId));
        mockCondition.setRecorder(new Reference("Practitioner/" + recorderId));

        Patient mockPatient = createMockPatient(patientId, "John Doe");
        PatientData mockPatientData = new PatientData(patientId, "John Doe", Gender.Male, "", "", "", "", "");

        Practitioner mockPractitioner = createMockPractitioner(recorderId, "Samantha Smith");
        PractitionerData mockPractitionerData = new PractitionerData(recorderId, "Samantha Smith", "", "", "");

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientById(patientId);
        Mockito.doReturn(mockPatientData).when(healthService).getPatientData(mockPatient);

        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerById(recorderId);
        Mockito.doReturn(mockPractitionerData).when(healthService).getPractitionerData(mockPractitioner);

        ConditionData conditionData = healthService.getConditionData(mockCondition);

        assertNotNull(conditionData, "ConditionData should not be null");
        assertEquals(conditionId, conditionData.getId(), "Condition ID should match");
        assertEquals("Hypertension", conditionData.getDisplay(), "Condition display should match");
        assertEquals("Active", conditionData.getClinicalStatus(), "Condition clinical status should match");
        assertEquals("Confirmed", conditionData.getVerificationStatus(), "Condition verification status should match");
        assertEquals("Severe", conditionData.getSeverity(), "Condition severity should match");
        assertNotNull(conditionData.getPatient(), "Patient data should not be null");
        assertNotNull(conditionData.getRecorder(), "Recorder data should not be null");
    }

    @Test
    void testAddConditionToPatient() {
        CreateCondition mockNewCondition = new CreateCondition(
                "patient123", "Hypertension", "Active", "Confirmed",
                "Severe", "patient123", "practitioner456", "This is a test note."
        );

        Patient mockPatient = new Patient();
        mockPatient.setId("patient123");

        Practitioner mockPractitioner = new Practitioner();
        mockPractitioner.setId("practitioner456");

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientByIdentifier("patient123");
        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerByIdentifier("practitioner456");

        ICreate mockCreate = mock(ICreate.class);
        ICreateTyped mockCreateTyped = mock(ICreateTyped.class);

        when(mockClient.create()).thenReturn(mockCreate);
        when(mockCreate.resource(any(Condition.class))).thenReturn(mockCreateTyped);

        MethodOutcome mockOutcome = new MethodOutcome();
        mockOutcome.setCreated(true);
        mockOutcome.setId(new IdType("Condition/789"));
        when(mockCreateTyped.execute()).thenReturn(mockOutcome);

        ReflectionTestUtils.setField(healthService, "client", mockClient);

        healthService.addConditionToPatient(mockNewCondition);

        verify(mockClient.create(), times(1)).resource(any(Condition.class));
        verify(mockCreateTyped, times(1)).execute();
        System.out.println("Condition created with ID: " + mockOutcome.getId().getIdPart());
    }

    @Test
    void testGetEncountersByPractitionerIdentifier() {
        String practitionerIdentifier = "12345";
        String encounterId = "67890";

        Practitioner mockPractitioner = createMockPractitioner(practitionerIdentifier, "Samantha Smith");

        Encounter mockEncounter = new Encounter();
        mockEncounter.setId(encounterId);

        Bundle mockBundle = new Bundle();
        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(mockEncounter);
        mockBundle.addEntry(entry);

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPractitioner).when(healthService).getPractitionerByIdentifier(practitionerIdentifier);

        setupMockQueryChain(Encounter.class, mockBundle);

        ReflectionTestUtils.setField(healthService, "client", mockClient);

        List<Encounter> encounters = healthService.getEncountersByPractitionerIdentifier(practitionerIdentifier);

        assertNotNull(encounters, "Encounters should not be null");
        assertEquals(1, encounters.size(), "There should be one encounter");
        assertEquals(encounterId, encounters.get(0).getIdElement().getIdPart(), "Encounter ID should match");
    }

    @Test
    void testGetEncounterData() {
        Encounter encounter = new Encounter();
        encounter.setId("12345");
        encounter.setStatus(Encounter.EncounterStatus.ARRIVED);
        encounter.setType(List.of(new CodeableConcept().setText("Consultation")));
        encounter.setPriority(new CodeableConcept().setText("High"));
        encounter.setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
        encounter.setLength(new Duration());
        encounter.addLocation(new Encounter.EncounterLocationComponent().setLocation(new Reference().setDisplay("Room 101")));
        encounter.setSubject(new Reference("Patient/56789"));

        Patient mockPatient = new Patient();
        mockPatient.setId("56789");
        mockPatient.addName(new HumanName().setFamily("Doe").addGiven("John"));

        PatientData mockPatientData = new PatientData("56789", "John Doe", Gender.Male, "", "", "", "", "");

        HealthService healthService = Mockito.spy(new HealthService());
        Mockito.doReturn(mockPatient).when(healthService).getPatientById("56789");
        Mockito.doReturn(mockPatientData).when(healthService).getPatientData(mockPatient);

        EncounterData encounterData = healthService.getEncounterData(encounter);

        assertNotNull(encounterData);
        assertEquals("12345", encounterData.getId());
        assertEquals("arrived", encounterData.getStatus());
        assertEquals("Consultation", encounterData.getType());
        assertEquals("High", encounterData.getPriority());
        assertEquals(mockPatientData, encounterData.getPatient());
        assertNotNull(encounterData.getPeriodStart());
        assertNotNull(encounterData.getPeriodEnd());
        assertEquals("", encounterData.getLength());
        assertEquals("Room 101", encounterData.getLocation());
    }

    private Patient createMockPatient(String identifierValue, String fullName) {
        Patient mockPatient = new Patient();
        mockPatient.addIdentifier().setSystem(PATIENT_SYSTEM).setValue(identifierValue);
        mockPatient.addName().setFamily(fullName.split(" ")[1]).addGiven(fullName.split(" ")[0]);
        return mockPatient;
    }

    private Practitioner createMockPractitioner(String identifierValue, String fullName) {
        Practitioner mockPractitioner = new Practitioner();
        mockPractitioner.addIdentifier().setSystem(PRACTITIONER_SYSTEM).setValue(identifierValue);
        mockPractitioner.addName().setFamily(fullName.split(" ")[1]).addGiven(fullName.split(" ")[0]);
        return mockPractitioner;
    }

    private void mockPatientSearch(Patient mockPatient) {
        IUntypedQuery mockQuery = mock(IUntypedQuery.class);
        IQuery mockQueryForResource = mock(IQuery.class);
        IQuery mockWhere = mock(IQuery.class);

        Bundle mockBundle = new Bundle();
        if (mockPatient != null) {
            Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
            entry.setResource(mockPatient);
            mockBundle.addEntry(entry);
        }

        when(mockClient.search()).thenReturn(mockQuery);
        when(mockQuery.forResource(Patient.class)).thenReturn(mockQueryForResource);
        when(mockQueryForResource.where((ICriterion<?>) any())).thenReturn(mockWhere);
        when(mockWhere.returnBundle(Bundle.class)).thenReturn(mockWhere);
        when(mockWhere.execute()).thenReturn(mockBundle);
    }

    private void mockPractitionerRoleSearch(PractitionerRole mockPractitionerRole) {
        IUntypedQuery mockQuery = mock(IUntypedQuery.class);
        IQuery mockQueryForResource = mock(IQuery.class);
        IQuery mockWhere = mock(IQuery.class);
        Bundle mockBundle = new Bundle();

        Bundle.BundleEntryComponent entry = new Bundle.BundleEntryComponent();
        entry.setResource(mockPractitionerRole);
        mockBundle.addEntry(entry);

        when(mockClient.search()).thenReturn(mockQuery);
        when(mockQuery.forResource(PractitionerRole.class)).thenReturn(mockQueryForResource);
        when(mockQueryForResource.where((ICriterion<?>) any())).thenReturn(mockWhere);
        when(mockWhere.returnBundle(Bundle.class)).thenReturn(mockWhere);
        when(mockWhere.execute()).thenReturn(mockBundle);
    }

    private IQuery<Bundle> setupMockQueryChain(Class<?> resourceClass, Bundle mockBundle) {
        IUntypedQuery mockQuery = mock(IUntypedQuery.class);
        IQuery<Bundle> mockQueryForResource = mock(IQuery.class);
        IQuery<Bundle> mockWhere = mock(IQuery.class);
        ISort<Bundle> mockSort = mock(ISort.class);
        IQuery<Bundle> mockDescending = mock(IQuery.class);
        IQuery<Bundle> mockReturnBundle = mock(IQuery.class);

        when(mockClient.search()).thenReturn(mockQuery);
        when(mockQuery.forResource(resourceClass)).thenReturn(mockQueryForResource);
        when(mockQueryForResource.where(any(ICriterion.class))).thenReturn(mockWhere);
        when(mockWhere.where(any(ICriterion.class))).thenReturn(mockWhere);
        when(mockWhere.sort()).thenReturn(mockSort);
        when(mockSort.descending(any(IParam.class))).thenReturn(mockDescending);
        when(mockDescending.returnBundle(Bundle.class)).thenReturn(mockReturnBundle);
        when(mockReturnBundle.execute()).thenReturn(mockBundle);

        return mockQueryForResource;
    }
}