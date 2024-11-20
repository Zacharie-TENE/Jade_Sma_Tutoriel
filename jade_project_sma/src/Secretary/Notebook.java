package Secretary;

public class Notebook {

    private String name;
    private String size;//metre
    private String gender;

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getMedicalHistory() {
        return medicalHistory;
    }

    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private String weight;//kg
    private String bloodType;
    private String patientName;
    private String medicalHistory;
    private String insurance;
    private String contactInfo;
    private String temperature;
    private String bloodPressure;
    private String reason;

    public Notebook(String name,String size, String gender, String weight, String bloodType, String patientName, String medicalHistory, String insurance, String contactInfo, String temperature, String bloodPressure, String reason) {
        this.name=name;
        this.size = size;
        this.gender = gender;
        this.weight = weight;
        this.bloodType = bloodType;
        this.patientName = patientName;
        this.medicalHistory = medicalHistory;
        this.insurance = insurance;
        this.contactInfo = contactInfo;
        this.temperature = temperature;
        this.bloodPressure = bloodPressure;
        this.reason = reason;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String convertNotebookToJson() {

        String jsonBuilder = "{" +
                "\"name\":\"" + this.getName() + "\"," +
                "\"size\":\"" + this.getSize() + "\"," +
                "\"gender\":\"" + this.getGender() + "\"," +
                "\"weight\":\"" + this.getWeight() + "\"," +
                "\"bloodType\":\"" + this.getBloodType() + "\"," +
                "\"patientName\":\"" + this.getPatientName() + "\"," +
                "\"medicalHistory\":\"" + this.getMedicalHistory() + "\"," +
                "\"insurance\":\"" + this.getInsurance() + "\"," +
                "\"contactInfo\":\"" + this.getContactInfo() + "\"," +
                "\"temperature\":\"" + this.getTemperature() + "\"," +
                "\"bloodPressure\":\"" + this.getBloodPressure() + "\"," +
                "\"reason\":\"" + this.getReason() + "\"" +
                "}";

        return jsonBuilder;
    }



}
