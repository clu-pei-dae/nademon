package net.jmhering.nademon.json;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by clupeidae on 09.06.15.
 */
public class JsonReader {

    private String url;
    private String jsonCode;
    private JSONObject json;

    static final Logger l = LogManager.getLogger("NaDeMon");

    public JsonReader(String url) throws IOException {
        this.url = url;
        l.trace("JsonReader initialized.");
        this.readJsonFromUrl();
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private void readJsonFromUrl() throws IOException, JSONException {
        InputStream is = new URL(this.url).openStream();
        try {
            l.debug("Opening file " + this.url);
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

            l.trace("Downloading content");
            this.jsonCode = readAll(rd);

            l.trace("Parsing JSON");
            this.json = new JSONObject(this.jsonCode);
        } finally {
            is.close();
        }
    }

    public JSONObject getResult() {
        return this.json;
    }
}
