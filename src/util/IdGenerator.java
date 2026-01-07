package util;

import java.util.Collection;

public final class IdGenerator {
    private IdGenerator() {
    }

    public static String nextId(String prefix, Collection<String> existingIds, int width) {
        int max = 0;
        if (existingIds != null) {
            for (String id : existingIds) {
                if (id != null && id.startsWith(prefix)) {
                    String digits = id.substring(prefix.length());
                    try {
                        int value = Integer.parseInt(digits);
                        if (value > max) {
                            max = value;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        int next = max + 1;
        return prefix + String.format("%0" + width + "d", next);
    }
}
