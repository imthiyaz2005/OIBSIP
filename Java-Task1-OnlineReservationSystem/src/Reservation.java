public class Reservation {

    private String pnr;
    private String passengerName;
    private String trainNumber;
    private String trainName;
    private String classType;
    private String source;
    private String destination;
    private String travelDate;

    public Reservation(
            String pnr,
            String passengerName,
            String trainNumber,
            String trainName,
            String classType,
            String source,
            String destination,
            String travelDate) {

        this.pnr = pnr;
        this.passengerName = passengerName;
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.classType = classType;
        this.source = source;
        this.destination = destination;
        this.travelDate = travelDate;
    }

    public String getPnr() {
        return pnr;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public String getTrainNumber() {
        return trainNumber;
    }

    public String getTrainName() {
        return trainName;
    }

    public String getClassType() {
        return classType;
    }

    public String getSource() {
        return source;
    }

    public String getDestination() {
        return destination;
    }

    public String getTravelDate() {
        return travelDate;
    }
}