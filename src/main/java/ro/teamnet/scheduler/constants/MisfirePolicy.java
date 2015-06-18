package ro.teamnet.scheduler.constants;

public enum MisfirePolicy {

    DO_NOTHING("Do nothing"),
    FIRE_ONCE("Fire once"),
    FIRE_ALL("Fire all");

    private String misfireType;

    MisfirePolicy(String misfireType) {
        this.misfireType = misfireType;
    }

    public static MisfirePolicy findByType(String misfireType) {
        for(MisfirePolicy misfire : values()) {
            if(misfire.misfireType.equals(misfireType)) {
                return misfire;
            }
        }
        return null;
    }
}
