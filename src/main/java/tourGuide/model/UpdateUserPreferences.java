package tourGuide.model;

public class UpdateUserPreferences {
    private int attractionProximity;
    private String currency;
    private int lowerPricePoint;
    private int highPricePoint;
    private int tripDuration;
    private int ticketQuantity;
    private int numberOfAdults;
    private int numberOfChildren;

    public UpdateUserPreferences(int attractionProximity, String currency, int lowerPricePoint,
                                 int highPricePoint, int tripDuration, int ticketQuantity,
                                 int numberOfAdults, int numberOfChildren) {
        this.attractionProximity = attractionProximity;
        this.currency = currency;
        this.lowerPricePoint = lowerPricePoint;
        this.highPricePoint = highPricePoint;
        this.tripDuration = tripDuration;
        this.ticketQuantity = ticketQuantity;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
    }

    public int getAttractionProximity() {
        return attractionProximity;
    }

    public String getCurrency() {
        return currency;
    }

    public int getLowerPricePoint() {
        return lowerPricePoint;
    }

    public int getHighPricePoint() {
        return highPricePoint;
    }

    public int getTripDuration() {
        return tripDuration;
    }

    public int getTicketQuantity() {
        return ticketQuantity;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }
}
