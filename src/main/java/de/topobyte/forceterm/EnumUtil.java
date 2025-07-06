package de.topobyte.forceterm;

public class EnumUtil {

    public static String toCamelCaseWithSpaces(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        String[] parts = input.split("_");
        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                // Capitalize the first letter and make the rest lowercase
                result.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    result.append(part.substring(1).toLowerCase());
                }
                result.append(" ");
            }
        }

        // Remove trailing space
        return result.toString().trim();
    }

}
