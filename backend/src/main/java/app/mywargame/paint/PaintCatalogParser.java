package app.mywargame.paint;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Parses one brand file from Arcturus5404/miniature-paints: a "# Brand" heading followed by a
 * Markdown table. Columns differ between brands (some have a Code column), so cells are looked
 * up by header name.
 */
public final class PaintCatalogParser {

    public record ParsedPaint(String name, String code, String set, String hex, int r, int g, int b) {
    }

    public record ParsedBrand(String name, List<ParsedPaint> paints) {
    }

    private static final Pattern HEX = Pattern.compile("`(#[0-9A-Fa-f]{6})`");

    private PaintCatalogParser() {
    }

    public static ParsedBrand parse(String fallbackBrandName, List<String> lines) {
        String brandName = fallbackBrandName;
        Map<String, Integer> columns = null;
        var paints = new ArrayList<ParsedPaint>();
        var seen = new HashSet<String>();

        for (String raw : lines) {
            String line = raw.strip();
            if (line.startsWith("# ")) {
                brandName = line.substring(2).strip();
                continue;
            }
            if (!line.startsWith("|")) {
                continue;
            }
            List<String> cells = cells(line);
            if (columns == null) {
                columns = new HashMap<>();
                for (int i = 0; i < cells.size(); i++) {
                    columns.put(cells.get(i).toLowerCase(Locale.ROOT), i);
                }
                continue;
            }
            if (cells.getFirst().startsWith("---") || cells.size() < columns.size()) {
                continue;
            }
            ParsedPaint paint = toPaint(columns, cells);
            if (paint != null && seen.add(key(paint))) {
                paints.add(paint);
            }
        }
        return new ParsedBrand(brandName, paints);
    }

    private static ParsedPaint toPaint(Map<String, Integer> columns, List<String> cells) {
        String name = cell(columns, cells, "name");
        if (name == null) {
            return null;
        }
        try {
            int r = Integer.parseInt(cell(columns, cells, "r"));
            int g = Integer.parseInt(cell(columns, cells, "g"));
            int b = Integer.parseInt(cell(columns, cells, "b"));
            String hexCell = cell(columns, cells, "hex");
            var matcher = HEX.matcher(hexCell == null ? "" : hexCell);
            String hex = matcher.find()
                    ? matcher.group(1).toUpperCase(Locale.ROOT)
                    : String.format("#%02X%02X%02X", r, g, b);
            return new ParsedPaint(name, cell(columns, cells, "code"), cell(columns, cells, "set"), hex, r, g, b);
        } catch (NumberFormatException | NullPointerException e) {
            return null;
        }
    }

    private static List<String> cells(String line) {
        String inner = line.substring(1, line.endsWith("|") ? line.length() - 1 : line.length());
        return Arrays.stream(inner.split("\\|", -1)).map(String::strip).toList();
    }

    /** Returns null for missing, blank or literal "null" cells. */
    private static String cell(Map<String, Integer> columns, List<String> cells, String column) {
        Integer index = columns.get(column);
        if (index == null) {
            return null;
        }
        String value = cells.get(index);
        return value.isEmpty() || value.equals("null") ? null : value;
    }

    private static String key(ParsedPaint p) {
        return (p.name() + "|" + p.code() + "|" + p.set()).toLowerCase(Locale.ROOT);
    }
}
