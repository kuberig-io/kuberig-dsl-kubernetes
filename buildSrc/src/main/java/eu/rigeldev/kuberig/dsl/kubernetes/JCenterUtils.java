package eu.rigeldev.kuberig.dsl.kubernetes;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

public class JCenterUtils {

    public static boolean exists(String moduleName, String version) {
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://api.bintray.com/packages/teyckmans/rigeldev-oss-maven/{module}")
                    .routeParam("module", moduleName)
                    .asJson();

            if (response.getStatus() == 404) {
                return false;
            }

            final JSONObject object = response.getBody().getObject();
            final JSONArray versions = object.getJSONArray("versions");

            for (int i = 0; i < versions.length(); i++) {
                String availableVersion = versions.getString(i);
                if (availableVersion.equals(version)) {
                    return true;
                }
            }

            return false;
        }
        catch (UnirestException e) {
            throw new RuntimeException(e);
        }
    }
}
