package tourGuide.model;

public class NearAttraction {
    private String attractionName;
    private Double attractionLatitude;
    private Double attractionLongitude;
    private Double userLocationLatitude;
    private Double userLocationLongitude;
    private Double distance;
    private int rewardPoints;

    public NearAttraction(String attractionName, Double attractionLatitude, Double attractionLongitude, Double userLocationLatitude, Double userLocationLongitude, Double distance, int rewardPoints) {
        this.attractionName = attractionName;
        this.attractionLatitude = attractionLatitude;
        this.attractionLongitude = attractionLongitude;
        this.userLocationLatitude = userLocationLatitude;
        this.userLocationLongitude = userLocationLongitude;
        this.distance = distance;
        this.rewardPoints = rewardPoints;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public Double getAttractionLatitude() {
        return attractionLatitude;
    }

    public void setAttractionLatitude(Double attractionLatitude) {
        this.attractionLatitude = attractionLatitude;
    }

    public Double getAttractionLongitude() {
        return attractionLongitude;
    }

    public void setAttractionLongitude(Double attractionLongitude) {
        this.attractionLongitude = attractionLongitude;
    }

    public Double getUserLocationLatitude() {
        return userLocationLatitude;
    }

    public void setUserLocationLatitude(Double userLocationLatitude) {
        this.userLocationLatitude = userLocationLatitude;
    }

    public Double getUserLocationLongitude() {
        return userLocationLongitude;
    }

    public void setUserLocationLongitude(Double userLocationLongitude) {
        this.userLocationLongitude = userLocationLongitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}

