package storage.shapes;

import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by eharvey on 4/20/16.
 */
public class SendPostRequest extends AsyncTask<ArrayList<String>, Void, String> {

    /**
     * This method unpacks the params which is of the form [field1,val1,field2,val2,...]
     * and then sends the post request to the given url.
     *
     * @return data: upon sucessfully connecting to the database.
     *           -1: upon failing to connect to the database.
     */
    @Override
    protected String doInBackground(ArrayList<String>... param) {
        final String DB_URL = "https://shapes.evanharvey.net/services";
        URL url;
        HttpsURLConnection connection;
        DataOutputStream DataOutputStream;
        JSONObject jsonObject;
        String service = null, request = "", charset = "UTF-8";

        try {
            // Unpack the url
            service = DB_URL + "/?" + param[0].get(0) + "=" + URLEncoder.encode(param[0].get(1), charset) +
                    "&" + URLEncoder.encode(param[0].get(2), charset) + "=" + URLEncoder.encode(param[0].get(3), charset);

            // Unpack the post request parameters
            for (int i = 4; i < param[0].size() - 1; i += 2) {
                if(param[0].get(i) == null || param[0].get(i+1) == null) {
                    return "-1";
                }
                if (i == 4)
                    request = request + param[0].get(i) + "=" + URLEncoder.encode(param[0].get(i + 1), charset);
                else
                    request = request + "&" + param[0].get(i) + "=" + URLEncoder.encode(param[0].get(i + 1), charset);

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "-1";
        }

        try {
            url = new URL(service);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try {
                DataOutputStream = new DataOutputStream(connection.getOutputStream());
            } catch (NetworkOnMainThreadException e) {
                return "-1";
            }
            DataOutputStream.writeBytes(request);
            DataOutputStream.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            // Read the server's reply
            String rx = br.readLine();

            // Parse the json object
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(rx);

            System.out.println(jsonObject.get("status"));
            System.out.println(jsonObject.get("data"));

            if ((Boolean)jsonObject.get("status")) {
                return (String) jsonObject.get("data");
            } else {
                return "-1";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "-1";
    }
}
