package xyz.yourboykyle.secretroutes.utils;

public class RotationUtils {
    public static float actualToRelativeYaw(float yaw, String direction) {
        switch(direction) {
            case "SW":
                return yaw;
            case "NW":
                return yaw - 90;
            case "NE":
                return yaw - 180;
            case "SE":
                return yaw - 270;
            default:
                return yaw;
        }
    }

    public static float relativeToActualYaw(float yaw, String direction) {
        switch(direction) {
            case "SW":
                return yaw;
            case "NW":
                return yaw + 90;
            case "NE":
                return yaw + 180;
            case "SE":
                return yaw + 270;
            default:
                return yaw;
        }
    }
}
