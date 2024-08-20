package moe.nea.libautoupdate;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

/**
 * Provider interface for getting the current version of this jar.
 */
public interface CurrentVersion {

    /**
     * Create a {@link CurrentVersion} that compares only numbers. (Other version types will be assumed to be newer.)
     *
     * @param number the current version number
     */
    static CurrentVersion of(int number) {
        return new CurrentVersion() {
            @Override
            public String display() {
                return String.valueOf(number);
            }

            @Override
            public boolean isOlderThan(JsonElement element) {
                if (!element.isJsonPrimitive()) return true;
                JsonPrimitive prim = element.getAsJsonPrimitive();
                if (!prim.isNumber()) return true;
                return prim.getAsInt() > number;
            }

            @Override
            public String toString() {
                return "VersionNumber (" + number + ")";
            }
        };
    }

    /**
     * Create a {@link CurrentVersion} that uses string tag names. Any difference in tag name will treat the current
     * version as outdated..
     *
     * @param tagName the current tag name
     */
    static CurrentVersion ofTag(String tagName) {
       return new CurrentVersion() {
           @Override
           public String display() {
               return tagName;
           }

           @Override
           public boolean isOlderThan(JsonElement element) {
               return !element.isJsonPrimitive() || !element.getAsString().equalsIgnoreCase(tagName);
           }

           @Override
           public String toString() {
               return "VersionTag (" + tagName + ")";
           }
       };
    }

    /**
     * @return a user-friendly representation of this version
     */
    String display();

    /**
     * Compare to another version, represented as JSON.
     *
     * @return true, if this version is older than the other version and an update to that version should occur.
     */
    boolean isOlderThan(JsonElement element);
}
