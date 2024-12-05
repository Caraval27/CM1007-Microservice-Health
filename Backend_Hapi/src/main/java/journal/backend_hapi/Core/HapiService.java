package journal.backend_hapi.Core;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import journal.backend_hapi.Core.Model.*;
import org.hl7.fhir.r4.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HapiService {
    private FhirContext context;
    private IGenericClient client;
    private static final String patientSystem = "http://electronichealth.se/identifier/personnummer";
    private static final String practitionerSystem = "http://terminology.hl7.org/CodeSystem/v2-0203";
    private static final String practitionerRoleSystem = "http://terminology.hl7.org/CodeSystem/practitioner-role";
    private static final String hapiServerURL = "https://hapi-fhir.app.cloud.cbh.kth.se/fhir";

    public HapiService() {
        context = FhirContext.forR4();
        client = context.newRestfulGenericClient(hapiServerURL);
    }

    public PatientData getPatientData(Patient patient) {
        if (patient == null) {
            return null;
        }
        String ssn = "";
        if (patient.hasIdentifier()) {
            for (Identifier id : patient.getIdentifier()) {
                if (id.hasSystem() && id.getSystem().equals(patientSystem)) {
                    ssn = id.getValue();
                    break;
                }
            }
        }
        String fullName = "";
        if (patient.hasName() && !patient.getName().isEmpty()) {
            fullName = patient.getNameFirstRep().getNameAsSingleString();
        }
        Enumerations.AdministrativeGender administrativeGender = patient.getGender();
        Gender gender = null;
        if (administrativeGender != null) {
            gender =
                    switch (administrativeGender) {
                        case FEMALE -> Gender.Female;
                        case MALE -> Gender.Male;
                        case UNKNOWN -> Gender.Unknown;
                        case OTHER -> Gender.Other;
                        case NULL -> null;
                    };
        }
        String email = "";
        String phone = "";
        if (patient.hasTelecom()) {
            for (ContactPoint contactPoint : patient.getTelecom()) {
                if (phone.isEmpty() && contactPoint.hasSystem() &&
                        contactPoint.getSystem() == ContactPoint.ContactPointSystem.PHONE) {
                    phone = contactPoint.getValue();
                }
                if (email.isEmpty() && contactPoint.hasSystem() &&
                        contactPoint.getSystem() == ContactPoint.ContactPointSystem.EMAIL) {
                    email = contactPoint.getValue();
                }
            }
        }
        String line = "";
        String city = "";
        String postalCode = "";
        if (patient.hasAddress() && !patient.getAddress().isEmpty()) {
            Address address = patient.getAddressFirstRep();
            if (address.hasLine() && !address.getLine().isEmpty()) {
                line = address.getLine().get(0).getValue();
            }
            if (address.hasCity()) {
                city = address.getCity();
            }
            if (address.hasPostalCode()) {
                postalCode = address.getPostalCode();
            }
        }

        return new PatientData(ssn, fullName, gender, email, phone, line, city, postalCode);
    }

    public User getPatientUserByIdentifier(String identifierValue) {
        Patient patient = getPatientByIdentifier(identifierValue);
        if (patient == null) {
            return null;
        }
        String fullName = "";
        if (patient.hasName() && !patient.getName().isEmpty()) {
            fullName = patient.getNameFirstRep().getNameAsSingleString();
        }
        return new User(identifierValue, fullName, Authority.Patient);
    }

    public Patient getPatientByIdentifier(String idValue) {
        Bundle bundle = client
                .search()
                .forResource(Patient.class)
                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(patientSystem, idValue))
                .returnBundle(Bundle.class)
                .execute();
        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
        if (entries.isEmpty()) {
            return null;
        }
        return (Patient) entries.get(0).getResource();
    }

    public Patient getPatientById(String patientId) {
        return client.read()
                .resource(Patient.class)
                .withId(patientId)
                .execute();
    }

    public PractitionerData getPractitionerData(Practitioner practitioner) {
        if (practitioner == null) {
            return null;
        }

        String hsaId = "";
        for (Identifier id : practitioner.getIdentifier()) {
            if (id.getSystem().equals(practitionerSystem)) {
                hsaId = id.getValue();
                break;
            }
        }

        String phone = "";
        String email = "";
        if (practitioner.hasTelecom()) {
            for (ContactPoint contactPoint : practitioner.getTelecom()) {
                if (phone.isEmpty() && contactPoint.hasSystem() &&
                        contactPoint.getSystem() == ContactPoint.ContactPointSystem.PHONE) {
                    phone = contactPoint.getValue();
                }
                if (email.isEmpty() && contactPoint.hasSystem() &&
                        contactPoint.getSystem() == ContactPoint.ContactPointSystem.EMAIL) {
                    email = contactPoint.getValue();
                }
            }
        }

        String fullName = practitioner.getName().get(0).getNameAsSingleString();

        return new PractitionerData(hsaId, fullName, email, phone);
    }

    public List<PractitionerRole> getPractitionerRoleByPractitionerId(String id) {
        Bundle bundle = client.search()
                .forResource(PractitionerRole.class)
                .where(PractitionerRole.PRACTITIONER.hasId("Practitioner/" + id))
                .returnBundle(Bundle.class)
                .execute();
        return bundle.getEntry().stream().map(pR -> (PractitionerRole) pR.getResource())
                .toList();
    }

    public User getPractitionerUserByIdentifier(String identifierValue) {
        Practitioner practitioner = getPractitionerByIdentifier(identifierValue);
        if (practitioner == null) {
            return null;
        }
        String fullName = "";
        if (practitioner.hasName() && !practitioner.getName().isEmpty()) {
            fullName = practitioner.getNameFirstRep().getNameAsSingleString();
        }
        Authority authority = Authority.Staff;
        List<PractitionerRole> practitionerRoles = getPractitionerRoleByPractitionerId(practitioner.getIdPart());
        if (!practitionerRoles.isEmpty()) {
            PractitionerRole practitionerRole = practitionerRoles.get(0);
            if (practitionerRole.hasCode() && !practitionerRole.getCode().isEmpty()) {
                CodeableConcept codeableConcept = practitionerRole.getCodeFirstRep();
                if (codeableConcept.hasCoding()) {
                    for (Coding coding : codeableConcept.getCoding()) {
                        if (coding.getSystem().equals(practitionerRoleSystem)) {
                            if (coding.getCode().equals("doctor")) {
                                authority = Authority.Doctor;
                            }
                        }
                    }
                }
            }
        }
        return new User(identifierValue, fullName, authority);
    }

    public Practitioner getPractitionerByIdentifier(String identifierValue) {
        Bundle bundle = client
                .search()
                .forResource(Practitioner.class)
                .where(Practitioner.IDENTIFIER.exactly().systemAndIdentifier(practitionerSystem, identifierValue))
                .returnBundle(Bundle.class)
                .execute();
        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
        if (entries.isEmpty()) {
            return null;
        }
        return (Practitioner) entries.get(0).getResource();
    }

    public Practitioner getPractitionerById(String practitionerId) {
        return client.read()
                .resource(Practitioner.class)
                .withId(practitionerId)
                .execute();
    }

    public String getGeneralPractitionerByIdentifier(String identifierValue) {
        Patient patient = getPatientByIdentifier(identifierValue);
        if (patient.hasGeneralPractitioner()) {
            Reference generalPractitionerRef = patient.getGeneralPractitionerFirstRep();
            if (generalPractitionerRef.hasReference()) {
                Practitioner practitioner = getPractitionerById(generalPractitionerRef.getReferenceElement().getIdPart());
                if (practitioner.hasIdentifier()) {
                    for (Identifier identifier : practitioner.getIdentifier()) {
                        if (practitionerSystem.equals(identifier.getSystem())) {
                            return identifier.getValue();
                        }
                    }
                }
            }
        }
        return null;
    }

    public List<Observation> getObservationsByPatientIdentifier(String identifierValue) {
        Patient patient = getPatientByIdentifier(identifierValue);
        if (patient == null) {
            return null;
        }
        Bundle bundle = client
                .search()
                .forResource(Observation.class)
                .where(Observation.SUBJECT.hasId("Patient/" + patient.getIdElement().getIdPart()))
                .sort().descending(Observation.DATE)
                .returnBundle(Bundle.class)
                .execute();

        List<Observation> observations = new ArrayList<>(bundle.getEntry().stream()
                .map(o -> (Observation) o.getResource())
                .toList());

        while (bundle.getLink(Bundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            observations.addAll(bundle.getEntry().stream()
                    .map(o -> (Observation) o.getResource())
                    .toList());
        }
        return observations;
    }

    public ObservationData getObservationData(Observation observation) {
        if (observation == null) {
            return null;
        }

        String id = null;
        if (observation.hasId()) {
            id = observation.getIdElement().getIdPart();
        }

        PatientData patientData = null;
        if (observation.getSubject() != null && observation.getSubject().getReference() != null) {
            String patientId = observation.getSubject().getReference().split("/")[1];
            patientData = getPatientData(getPatientById(patientId));
        }

        PractitionerData performerData = null;
        if (!observation.getPerformer().isEmpty() && observation.getPerformer().get(0).getReference() != null) {
            String performerId = observation.getPerformer().get(0).getReference().split("/")[1];
            performerData = getPractitionerData(getPractitionerById(performerId));
        }

        String display = null;
        if (observation.getCode() != null && !observation.getCode().getCoding().isEmpty()) {
            Coding coding = observation.getCode().getCoding().get(0);
            display = coding.getDisplay();
        }

        // finns andra än quantity
        String value = null;
        String unit = null;
        if (observation.getValue() instanceof Quantity quantity) {
            value = quantity.getValue() != null ? quantity.getValue().toString() : null;
            unit = quantity.getUnit();
        }

        String note = null;
        if (observation.hasNote() && !observation.getNote().isEmpty()) {
            note = observation.getNote().stream()
                    .map(Annotation::getText)
                    .filter(text -> text != null && !text.isEmpty())
                    .reduce((first, second) -> first + " | " + second)
                    .orElse(null);
        }

        String status = observation.getStatus() != null ? observation.getStatus().toCode() : null;

        Date date = null;
        if (observation.hasEffectiveDateTimeType()) {
            date = observation.getEffectiveDateTimeType().getValue();
        } else if (observation.getIssued() != null) {
            date = observation.getIssued();
        }

        return new ObservationData(id, patientData, performerData, display, value, unit, note, status, date);
    }

    public void addObservationToPatient(CreateObservation newObservation) {
        Observation observation = new Observation();

        observation.setStatus(Observation.ObservationStatus.FINAL);

        String code = newObservation.getCode();
        String display = newObservation.getDisplay();
        if (code != null && display != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding(new Coding()
                    .setSystem("http://loinc.org")
                    .setCode(code)
                    .setDisplay(display));
            observation.setCode(codeableConcept);
        }

        Patient patient = getPatientByIdentifier(newObservation.getPatientId());
        observation.setSubject(new Reference("Patient/" + patient.getIdElement().getIdPart()));

        String value = newObservation.getValue();
        String unit = newObservation.getUnit();
        if (value != null && unit != null) {
            observation.setValue(new Quantity()
                    .setValue(Double.parseDouble(value))
                    .setUnit(unit));
        }

        observation.setEffective(new DateTimeType(new Date()));

        String note = newObservation.getNote();
        if (note != null && !note.isEmpty()) {
            Annotation annotation = new Annotation();
            annotation.setText(note);
            observation.addNote(annotation);
        }

        Practitioner practitioner = getPractitionerByIdentifier(newObservation.getPerformerId());
        observation.addPerformer(new Reference("Practitioner/" + practitioner.getIdElement().getIdPart()));

        MethodOutcome outcome = client.create()
                .resource(observation)
                .execute();

        if (outcome.getCreated()) {
            System.out.println("Observation created with ID: " + outcome.getId().getIdPart());
        } else {
            System.out.println("Failed to create observation.");
        }
    }

    public List<Condition> getConditionsByPatientIdentifier(String identifierValue) {
        Patient patient = getPatientByIdentifier(identifierValue);
        if (patient == null) {
            return null;
        }
        Bundle bundle = client
                .search()
                .forResource(Condition.class)
                .where(Condition.SUBJECT.hasId("Patient/" + patient.getIdElement().getIdPart()))
                .sort().descending(Condition.RECORDED_DATE)
                .returnBundle(Bundle.class)
                .execute();

        List<Condition> conditions = new ArrayList<>(bundle.getEntry().stream()
                .map(c -> (Condition) c.getResource())
                .toList());

        while (bundle.getLink(Bundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            conditions.addAll(bundle.getEntry().stream()
                    .map(c -> (Condition) c.getResource())
                    .toList());
        }
        return conditions;
    }

    public ConditionData getConditionData(Condition condition) {
        if (condition == null) {
            return null;
        }

        String id = null;
        if (condition.hasId()) {
            id = condition.getIdElement().getIdPart();
        }

        String display = null;
        if (condition.hasCode() && !condition.getCode().getCoding().isEmpty()) {
            Coding coding = condition.getCode().getCoding().get(0);
            display = coding.getDisplay();
        }

        String clinicalStatus = null;
        if (condition.hasClinicalStatus() && !condition.getClinicalStatus().getCoding().isEmpty()) {
            clinicalStatus = condition.getClinicalStatus().getCodingFirstRep().getDisplay();
        }

        String verificationStatus = null;
        if (condition.hasVerificationStatus() && !condition.getVerificationStatus().getCoding().isEmpty()) {
            verificationStatus = condition.getVerificationStatus().getCodingFirstRep().getDisplay();
        }

        String severity = null;
        if (condition.hasSeverity() && !condition.getSeverity().getCoding().isEmpty()) {
            severity = condition.getSeverity().getCodingFirstRep().getDisplay();
        }

        PatientData patient = null;
        if (condition.hasSubject() && condition.getSubject().hasReference()) {
            String patientId = condition.getSubject().getReference().split("/")[1];
            patient = getPatientData(getPatientById(patientId));
        }

        PractitionerData recorder = null;
        if (condition.hasRecorder() && condition.getRecorder().hasReference()) {
            String recorderId = condition.getRecorder().getReference().split("/")[1];
            recorder = getPractitionerData(getPractitionerById(recorderId));
        }

        Date date = null;
        if (condition.hasRecordedDate()) {
            date = condition.getRecordedDate();
        }

        String note = null;
        if (condition.hasNote() && !condition.getNote().isEmpty()) {
            note = condition.getNote().stream()
                    .map(Annotation::getText)
                    .filter(text -> text != null && !text.isEmpty())
                    .reduce((first, second) -> first + " | " + second)
                    .orElse(null);
        }

        return new ConditionData(id, display, clinicalStatus, verificationStatus, severity, patient, recorder, date, note);
    }

    public void addConditionToPatient(CreateCondition newCondition) {
        Condition condition = new Condition();

        String code = newCondition.getCode();
        String display = newCondition.getDisplay();
        if (code != null && display != null) {
            CodeableConcept codeableConcept = new CodeableConcept();
            codeableConcept.addCoding(new Coding()
                    .setSystem("http://hl7.org/fhir/sid/icd-10") // osäker
                    .setCode(code)
                    .setDisplay(display));
            condition.setCode(codeableConcept);
        }

        String clinicalStatus = newCondition.getClinicalStatus();
        if (clinicalStatus != null) {
            condition.setClinicalStatus(new CodeableConcept().addCoding(
                    new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/condition-clinical")
                            .setDisplay(clinicalStatus)));
        }

        String verificationStatus = newCondition.getVerificationStatus();
        if (verificationStatus != null) {
            condition.setVerificationStatus(new CodeableConcept().addCoding(
                    new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/condition-ver-status")
                            .setDisplay(verificationStatus)));
        }

        String severity = newCondition.getSeverity();
        if (severity != null) {
            condition.setSeverity(new CodeableConcept().addCoding(
                    new Coding()
                            .setSystem("http://terminology.hl7.org/CodeSystem/condition-severity")
                            .setDisplay(severity)));
        }

        Patient patient = getPatientByIdentifier(newCondition.getPatientId());
        if (patient != null) {
            condition.setSubject(new Reference("Patient/" + patient.getIdElement().getIdPart()));
        }

        Practitioner practitioner = getPractitionerByIdentifier(newCondition.getRecorderId());
        if (practitioner != null) {
            condition.setRecorder(new Reference("Practitioner/" + practitioner.getIdElement().getIdPart()));
        }

        condition.setRecordedDateElement(new DateTimeType(new Date()));

        String note = newCondition.getNote();
        if (note != null && !newCondition.getNote().isEmpty()) {
            Annotation annotation = new Annotation();
            annotation.setText(note);
            condition.addNote(annotation);
        }

        MethodOutcome outcome = client.create()
                .resource(condition)
                .execute();

        if (outcome.getCreated()) {
            System.out.println("Condition created with ID: " + outcome.getId().getIdPart());
        } else {
            System.out.println("Failed to create condition.");
        }
    }

    public List<Encounter> getEncountersByPractitionerIdentifier(String identifierValue) {
        Practitioner practitioner = getPractitionerByIdentifier(identifierValue);
        if (practitioner == null) {
            return null;
        }

        Bundle bundle = client
                .search()
                .forResource(Encounter.class)
                .where(Encounter.PRACTITIONER.hasId("Practitioner/" + practitioner.getIdElement().getIdPart()))
                //.where(Encounter.DATE.exactly().day(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .sort().descending(Encounter.DATE)
                .returnBundle(Bundle.class)
                .execute();

        List<Encounter> encounters = new ArrayList<>(bundle.getEntry().stream()
                .map(c -> (Encounter) c.getResource())
                .toList());

        while (bundle.getLink(Bundle.LINK_NEXT) != null) {
            bundle = client.loadPage().next(bundle).execute();
            encounters.addAll(bundle.getEntry().stream()
                    .map(c -> (Encounter) c.getResource())
                    .toList());
        }

        return encounters;
    }

    public EncounterData getEncounterData(Encounter encounter) {
        if (encounter == null) {
            return null;
        }

        String id = "";
        if (encounter.hasId()) {
            id = encounter.getIdElement().getIdPart();
        }

        String status = "";
        if (encounter.hasStatus()) {
            status = encounter.getStatus().toCode();
        }

        /*String statusHistory;
        if (encounter.hasStatusHistory()) {

        }*/

        String type = "";
        if (encounter.hasType()) {
            type = encounter.getTypeFirstRep().getText();
        }

        String priority = "";
        if (encounter.hasPriority()) {
            priority = encounter.getPriority().getText();
        }

        PatientData patient = null;
        if (encounter.hasSubject() && encounter.getSubject().hasReference()) {
            String patientId = encounter.getSubject().getReference().split("/")[1];
            patient = getPatientData(getPatientById(patientId));
        }

        LocalDateTime periodStart = null;
        if (encounter.hasPeriod() && encounter.getPeriod().hasStart()) {
            Date startDate = encounter.getPeriod().getStart();
            periodStart = LocalDateTime.ofInstant(startDate.toInstant(), ZoneId.systemDefault());
        }

        LocalDateTime periodEnd = null;
        if (encounter.hasPeriod() && encounter.getPeriod().hasEnd()) {
            Date endDate = encounter.getPeriod().getEnd();
            periodEnd = LocalDateTime.ofInstant(endDate.toInstant(), ZoneId.systemDefault());
        }

        String length = "";
        if (encounter.hasLength()) {
            length = encounter.getLength().getValue().toString();
        }

        String location = "";
        if (encounter.hasLocation()) {
            location = encounter.getLocationFirstRep().getLocation().getDisplay();
        }

        return new EncounterData(id, status, type, priority, patient, periodStart, periodEnd, length, location);
    }
}