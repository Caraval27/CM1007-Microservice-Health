package journal.lab3_health.Core.Model;

public class CreateCondition {
    private String code;
    private String display;
    private String clinicalStatus;
    private String verificationStatus;
    private String severity;
    private String patientId;
    private String recorderId;
    private String note;

    public CreateCondition(String code, String display, String clinicalStatus, String verificationStatus, String severity, String patientId, String recorderId, String note) {
        this.code = code;
        this.display = display;
        this.clinicalStatus = clinicalStatus;
        this.verificationStatus = verificationStatus;
        this.severity = severity;
        this.patientId = patientId;
        this.recorderId = recorderId;
        this.note = note;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRecorderId() {
        return recorderId;
    }

    public void setRecorderId(String recorderId) {
        this.recorderId = recorderId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "CreateCondition{" +
                "code='" + code + '\'' +
                ", display='" + display + '\'' +
                ", clinicalStatus='" + clinicalStatus + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", severity='" + severity + '\'' +
                ", patientId='" + patientId + '\'' +
                ", recorderId='" + recorderId + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
