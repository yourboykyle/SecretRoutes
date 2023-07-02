package xyz.yourboykyle.secretroutes.customevents;

import xyz.yourboykyle.secretroutes.Main;

public class SecretCompleted {
    public static void onSecretCompleted() {
        Main.nextPath();
    }
}
