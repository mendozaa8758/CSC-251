import java.util.ArrayList;
import java.util.List;

/**
 * Small helper for reading/writing CSV lines so commas and quotes inside
 * a field (like a customer name with a comma in it) don't break parsing.
 *
 * Contribution: Aiden Canady (Service class + CsvUtil helper)
 */
public class CsvUtil {

    // Wrap a field in quotes if it contains a comma, quote, or newline,
    // and double up any internal quotes the way Excel/CSV expects.
    public static String escape(String field) {
        if (field == null) {
            return "";
        }
        boolean needsQuotes = field.contains(",") || field.contains("\"") || field.contains("\n");
        String escaped = field.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }

    // Splits one CSV line into fields, respecting quoted sections.
    public static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"'); // escaped quote
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
