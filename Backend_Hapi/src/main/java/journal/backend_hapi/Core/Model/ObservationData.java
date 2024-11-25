package journal.backend_hapi.Core.Model;

import java.util.Date;

public class ObservationData {
    private String id;
    private PatientData patient;
    private PractitionerData performer;
    private String display;
    private String value;
    private String unit;
    private String note;
    private String status;
    private Date date;

    public ObservationData(String id, PatientData patient, PractitionerData performer, String display, String value, String unit, String note, String status, Date date) {
        this.id = id;
        this.patient = patient;
        this.performer = performer;
        this.display = display;
        this.value = value;
        this.unit = unit;
        this.note = note;
        this.status = status;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PatientData getPatient() {
        return patient;
    }

    public void setPatient(PatientData patient) {
        this.patient = patient;
    }

    public PractitionerData getPerformer() {
        return performer;
    }

    public void setPerformer(PractitionerData performer) {
        this.performer = performer;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "ObservationData{" +
                "id='" + id + '\'' +
                ", patient=" + patient +
                ", performer=" + performer +
                ", display='" + display + '\'' +
                ", value='" + value + '\'' +
                ", unit='" + unit + '\'' +
                ", note='" + note + '\'' +
                ", status='" + status + '\'' +
                ", date=" + date +
                '}';
    }
}