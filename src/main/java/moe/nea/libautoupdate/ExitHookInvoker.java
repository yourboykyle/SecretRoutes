package moe.nea.libautoupdate;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A Utility class for setting up the exit hook, which then launches the next stage (postexit) using the same java runtime.
 */
public class ExitHookInvoker {

    private static boolean isExitHookRegistered = false;
    private static List<UpdateAction> actions;
    private static File updaterJar;
    private static boolean cancelled = false;
    private static String identifer;
    private static UUID uuid;

    /**
     * Set up the exit hook to run post exit actions.
     *
     * <p><b>N.B.:</b> Calling this multiple times will only invoke the last set of actions.
     * In case of multiple updates the update actions should be joined in the same list.</p>
     *
     * @param identifier the identifier for this updater for logging purposes
     * @param uuid       the uuid of this update for logging purposes
     * @param updaterJar the extracted updater jar
     * @param actions    the actions to execute
     */
    public static synchronized void setExitHook(String identifier, UUID uuid, File updaterJar, List<UpdateAction> actions) {
        if (!isExitHookRegistered) {
            Runtime.getRuntime().addShutdownHook(new Thread(ExitHookInvoker::runExitHook));

            isExitHookRegistered = true;
        }
        ExitHookInvoker.identifer = identifier;
        ExitHookInvoker.uuid = uuid;
        ExitHookInvoker.cancelled = false;
        ExitHookInvoker.actions = actions;
        ExitHookInvoker.updaterJar = updaterJar;
    }

    /**
     * Cancel the exit hook, invalidating any previous calls to {@link #setExitHook}
     */
    public static synchronized void cancelExitHook() {
        cancelled = true;
    }

    private static synchronized String[] buildInvocation() {
        boolean isWindows = System.getProperty("os.name", "").startsWith("Windows");
        File javaBinary = new File(System.getProperty("java.home"), "bin/java" + (isWindows ? ".exe" : ""));


        List<String> arguments = new ArrayList<>();
        arguments.add(javaBinary.getAbsolutePath());
        arguments.add("-jar");
        arguments.add(updaterJar.getAbsolutePath());

        arguments.add(identifer);
        arguments.add(String.valueOf(uuid));

        for (UpdateAction action : actions) {
            action.encode(arguments);
        }

        return arguments.toArray(new String[0]);
    }

    private static void runExitHook() {
        try {
            if (cancelled) {
                System.out.println("Skipping cancelled update");
                return;
            }
            String[] invocation = buildInvocation();
            System.out.println("Running post updater using: " + String.join(" ", invocation));
            Runtime.getRuntime().exec(invocation);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


}
