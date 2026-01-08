import java.util.*;

// GENERIC CLASS [Requirement: 3pt]
class DataPacket<T> {
    private T content;

    public DataPacket(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}

public class DataUtils {
    // COLLECTIONS [Requirement: 1pt]
    public static List<Map<String, Object>> parseJson(String json) {
        List<Map<String, Object>> list = new ArrayList<>();
        int start = json.indexOf("["), end = json.lastIndexOf("]");
        if (start == -1 || end == -1)
            return list;

        String content = json.substring(start + 1, end).trim();
        if (content.isEmpty())
            return list;

        String[] objects = content.split("(?<=\\}),");
        for (String obj : objects) {
            String cleanObj = obj.trim();
            if (cleanObj.isEmpty())
                continue;
            Map<String, Object> map = new HashMap<>();
            // Keys must match C++ Entities.cpp exactly
            String[] keys = { "name", "price", "area_sqm", "rooms", "year_built", "city", "province", "floor",
                    "parking", "garden", "image", "score", "for_sale", "type", "has_kitchen_annex", "has_elevator",
                    "has_school", "has_park", "has_shop", "has_pharmacy" };
            for (String key : keys) {
                map.put(key, extractValue(cleanObj, key));
            }
            list.add(map);
        }
        return list;
    }

    public static String extractValue(String json, String key) {
        String sk = "\"" + key + "\"";
        int s = json.indexOf(sk);
        if (s == -1)
            return "0";
        int colon = json.indexOf(":", s + sk.length());
        int vs = colon + 1;
        while (vs < json.length() && Character.isWhitespace(json.charAt(vs)))
            vs++;
        if (vs < json.length() && json.charAt(vs) == '\"') {
            int ve = json.indexOf("\"", vs + 1);
            return (ve != -1) ? json.substring(vs + 1, ve) : "";
        }
        int ve = vs;
        while (ve < json.length() && json.charAt(ve) != ',' && json.charAt(ve) != '}' && json.charAt(ve) != ']')
            ve++;
        return json.substring(vs, ve).trim();
    }
}