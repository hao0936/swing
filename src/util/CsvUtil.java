package util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvUtil {
    private CsvUtil() {
    }

    public static List<String[]> read(Path path) throws IOException {
        List<String[]> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            rows.add(parseLine(line));
        }
        return rows;
    }

    public static void write(Path path, String[] header, List<String[]> rows) throws IOException {
        List<String> lines = new ArrayList<>();
        if (header != null) {
            lines.add(toLine(header));
        }
        for (String[] row : rows) {
            lines.add(toLine(row));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    private static String[] parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private static String toLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(escape(fields[i]));
        }
        return sb.toString();
    }

    private static String escape(String field) {
        if (field == null) {
            return "";
        }
        boolean mustQuote = field.contains(",") || field.contains("\"") || field.contains("\n");
        String escaped = field.replace("\"", "\"\"");
        if (mustQuote) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
