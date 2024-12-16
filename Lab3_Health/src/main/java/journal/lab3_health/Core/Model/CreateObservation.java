package journal.lab3_health.Core.Model;

public class CreateObservation {
    private String patientId;
    private String code;
    private String display;
    private String value;
    private String unit;
    private String note;
    private String performerId;

    public CreateObservation(String patientId, String code, String display, String value, String unit, String note, String performerId) {
        this.patientId = patientId;
        this.code = code;
        this.display = display;
        this.value = value;
        this.unit = unit;
        this.note = note;
        this.performerId = performerId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public String getPerformerId() {
        return performerId;
    }

    public void setPerformerId(String performerId) {
        this.performerId = performerId;
    }

    @Override
    public String toString() {
        return "CreateObservation{" +
                "patientId='" + patientId + '\'' +
                ", code='" + code + '\'' +
                ", display='" + display + '\'' +
                ", value='" + value + '\'' +
                ", unit='" + unit + '\'' +
                ", note='" + note + '\'' +
                ", performerId='" + performerId + '\'' +
                '}';
    }
}
