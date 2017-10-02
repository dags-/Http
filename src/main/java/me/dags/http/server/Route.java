package me.dags.http.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dags <dags@dags.me>
 */
public class Route {

    private final Pattern pattern;
    private final String[] names;

    private Route(Pattern pattern, String[] names) {
        this.pattern = pattern;
        this.names = names;
    }

    public Map<String, String> parse(String uri) {
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            int count = matcher.groupCount();
            if (count == names.length) {
                Map<String, String> map = new HashMap<>();
                for (int i = 0; i < count; i++) {
                    String group = matcher.group(i + 1);
                    map.put(names[i], group);
                }
                return map;
            }
        }
        return null;
    }

    public static Route parseRoute(String in) {
        List<String> keys = new LinkedList<>();
        StringBuilder patternBuilder = new StringBuilder("^");

        int last = 0;
        for (int i = 0; i < in.length(); i++) {
            if (in.charAt(i) == '{') {
                patternBuilder.append(in.substring(last, i));

                int start = i + 1;
                int end = start + 1;

                while (end < in.length()) {
                    if (in.charAt(end) == '}') {
                        break;
                    }
                    end++;
                }

                keys.add(in.substring(start, end).toLowerCase());
                patternBuilder.append("(.+)");
                last = i = end + 1;
            }
        }

        if (last < in.length()) {
            patternBuilder.append(in.substring(last, in.length()));
        }

        patternBuilder.append("$");

        Pattern pattern = Pattern.compile(patternBuilder.toString(), Pattern.CASE_INSENSITIVE);
        String[] names = keys.toArray(new String[keys.size()]);
        return new Route(pattern, names);
    }
}
