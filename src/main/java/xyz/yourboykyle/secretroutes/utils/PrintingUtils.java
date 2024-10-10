package xyz.yourboykyle.secretroutes.utils;

public class PrintingUtils {
    public static long lastPrinted;
    public PrintingUtils PrintingUtils(){
        lastPrinted = System.currentTimeMillis();
        return new PrintingUtils();
    }
    public static void print(String message, int delay){
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastPrinted > delay){
            ChatUtils.sendVerboseMessage(message);
            lastPrinted = currentTime;
        }
    }
}
