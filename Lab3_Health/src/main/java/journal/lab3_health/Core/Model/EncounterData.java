package journal.lab3_health.Core.Model;

import java.time.LocalDateTime;

public class EncounterData {
    private String id;
    private String status;
    //private String statusHistory;
    private String type;
    private String priority;
    private PatientData patient;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private String length;
    private String location;

    public EncounterData(String id, String status, String type, String priority, PatientData patient, LocalDateTime periodStart, LocalDateTime periodEnd, String length, String location) {
        this.id = id;
        this.status = status;
        this.type = type;
        this.priority = priority;
        this.patient = patient;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.length = length;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public PatientData getPatient() {
        return patient;
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
    }

    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "EncounterData{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", priority='" + priority + '\'' +
                ", patient=" + patient +
                ", periodStart=" + periodStart +
                ", periodEnd=" + periodEnd +
                ", length='" + length + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}