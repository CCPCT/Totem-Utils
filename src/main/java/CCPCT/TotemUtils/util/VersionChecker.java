package CCPCT.TotemUtils.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChecker {

    private static final String GITHUB_TAGS_URL = "https://api.github.com/repos/CCPCT/Totem-Utils/tags";

    /**
     * Fetches tags from GitHub and returns the newest mod version for the specified MC version.
     * Returns null if no matching version found or error occurs.
     */
    public static String getNewestVersion(String mcVersion) {
        try {
            String json = fetchTagsJson();
            List<String> tags = parseTagNames(json);
            return findNewestModVersion(tags, mcVersion);
        } catch (Exception e) {
            return null;
        }
    }

    private static String fetchTagsJson() throws Exception {
        URL url = URI.create(GITHUB_TAGS_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("GitHub API returned HTTP " + conn.getResponseCode());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder jsonText = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            jsonText.append(line);
        }
        reader.close();

        return jsonText.toString();
    }

    private static List<String> parseTagNames(String json) {
        List<String> tags = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        return tags;
    }

    private static String findNewestModVersion(List<String> tags, String mcVersion) {
        String newestModVersion = "";

        for (String tag : tags) {
            String[] parts = tag.split("-");
            if (parts.length < 2) continue;

            String modVersion = parts[0];
            String tagMcVersion = parts[1];
            System.out.println(modVersion + " - " + tagMcVersion);

            if (!tagMcVersion.equals(mcVersion)) continue;

            if (newestModVersion.isEmpty() || compareVersions(modVersion, newestModVersion) > 0) {
                newestModVersion = modVersion;
            }
        }

        return newestModVersion;
    }

    public static int compareVersions(String v1, String v2) {
        return v1.compareTo(v2);
    }
}
