package journal.backend_hapi.Core.Model;

import java.util.Date;

public class ConditionData {
    private String id;
    private String display;
    private String clinicalStatus;
    private String verificationStatus;
    private String severity;
    private PatientData patient;
    private PractitionerData recorder;
    private Date recordDate;
    private String note;

    public ConditionData(String id, String display, String clinicalStatus, String verificationStatus, String severity, PatientData patient, PractitionerData recorder, Date recordDate, String note) {
        this.id = id;
        this.display = display;
        this.clinicalStatus = clinicalStatus;
        this.verificationStatus = verificationStatus;
        this.severity = severity;
        this.patient = patient;
        this.recorder = recorder;
        this.recordDate = recordDate;
        this.note = note;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getClinicalStatus() {
        return clinicalStatus;
    }

    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public PatientData getPatient() {
        return patient;
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
    }

    public PractitionerData getRecorder() {
        return recorder;
    }

    public void setRecorder(PractitionerData recorder) {
        this.recorder = recorder;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "ConditionData{" +
                "id='" + id + '\'' +
                ", display='" + display + '\'' +
                ", clinicalStatus='" + clinicalStatus + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", severity='" + severity + '\'' +
                ", patient=" + patient +
                ", recorder=" + recorder +
                ", recordDate=" + recordDate +
                ", note='" + note + '\'' +
                '}';
    }
}
