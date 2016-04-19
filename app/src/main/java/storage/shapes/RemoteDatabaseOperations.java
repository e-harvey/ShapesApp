/**
 * Remote storage method implementations.
 */

package storage.shapes;

import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;


class RemoteDatabaseOperations implements RemoteDbOperations, SharedDbOperations  {
    private final String DB_URL = "https://shapes.evanharvey.net/services";
    private URL url;
    private HttpsURLConnection connection;
    private String charset;
    private String data, token = null;

    private enum PostRequestStatus {
        Success, Failure
    }

    /* Begin constructors */
    RemoteDatabaseOperations() {
        charset = "UTF-8";
    }
    /* End constructors */

    /* Begin Private methods */

    /**
     * This method upacks the params which is of the form [field1,val1,field2,val2,...]
     * and then send the post request to the given url.
     *
     * @return Success: upon sucessfully connecting to the database.
     *         Failure: upon fauling to connect to the database.
     */
    private PostRequestStatus sendPostRequest(ArrayList<String> param) {
        PostRequestStatus postRequestStatus = PostRequestStatus.Failure;
        DataOutputStream DataOutputStream;
        DataInputStream dataInputStream;
        JSONObject jsonObject;

        String service = null, request = "";

        try {
            service = DB_URL + "/?" + param.get(0) + "=" + URLEncoder.encode(param.get(1), charset) +
            "&" + URLEncoder.encode(param.get(2), charset) + "=" + URLEncoder.encode(param.get(3), charset);

            for (int i = 4; i < param.size() - 1; i += 2) {
                if (i == 4)
                    request = request + param.get(i) + "=" + URLEncoder.encode(param.get(i + 1), charset);
                else
                    request = request + "&" + param.get(i) + "=" + URLEncoder.encode(param.get(i + 1), charset);

            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return postRequestStatus;
        }

        System.out.print(service + " ");
        System.out.println(request);

        try {
            url = new URL(service);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            DataOutputStream = new DataOutputStream(connection.getOutputStream());
            DataOutputStream.writeBytes(request);
            DataOutputStream.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String whatWeGot = br.readLine();
            System.out.println(whatWeGot);
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(whatWeGot);

            System.out.println(jsonObject.get("status"));
            System.out.println(jsonObject.get("data"));

            if ((Boolean)jsonObject.get("status"))
                postRequestStatus = PostRequestStatus.Success;

            data = (String) jsonObject.get("data");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return postRequestStatus;

    }

    /**
     * This method prints out the connection status information based on the
     * value of postRequestStatus.
     *
     * @param postRequestStatus the status of the remote connection.
     * todo: Print messages to the user's screen.
     */
    private void printPostRequestStatus(PostRequestStatus postRequestStatus)
    {
        String userStatus = null;

        switch(postRequestStatus) {
            case Success:
                userStatus = "You have successfully connected to the database!";
                break;

            default:
                userStatus = "Please check your local network connection.";
        }
        System.out.println(userStatus);
    }

    /* Begin RemoteDbOperation methods */
    public boolean getLoginStatus(String username)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getLoginstatus");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, token);

        return sendPostRequest(params) == PostRequestStatus.Success;
    }

    public long getBlockSeed(String username)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getBlockSeed");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, token);

        data = "0";
        sendPostRequest(params);
        return Long.valueOf(data);
    }

    public void setBlockSeed(String username, long seed)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "setBlockSeed");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, token);

        params.add(8, "seed");
        params.add(9, String.valueOf(seed));

        sendPostRequest(params);
    }
    /* End RemoteDbOperation methods */

    /* Begin SharedDatabaseOperations methods */
    public boolean addUser(String username, String password)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "addUser");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "password");
        params.add(7, password);

        return sendPostRequest(params) == PostRequestStatus.Success;
    }

    public boolean deleteUser(String username, String password) {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "deleteUser");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "password");
        params.add(7, password);

        return sendPostRequest(params) == PostRequestStatus.Success;
    }

    public void setHighScore(String username, long score)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "setHighScore");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, token);

        params.add(8, "score");
        params.add(9, String.valueOf(score));

        sendPostRequest(params);
    }

    public long getHighScore(String username)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "get");

        params.add(2, "action");
        params.add(3, "getHighScore");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, token);

        data = "0";
        sendPostRequest(params);
        return Long.valueOf(data);
    }

    public boolean login(String username, String password)
    {
        boolean ret;

        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "login");

        params.add(2, "action");
        params.add(3, "login");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "password");
        params.add(7, password);

        ret = sendPostRequest(params) == PostRequestStatus.Success;

        token = data;

        return ret;
    }

    public void logout(String username)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "login");

        params.add(2, "action");
        params.add(3, "logout");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "token");
        params.add(7, token);

        sendPostRequest(params);
    }

    public boolean setPassword(String username, String oldPassword, String newPassword)
    {
        ArrayList<String> params = new ArrayList<String>();

        params.add(0, "service");
        params.add(1, "set");

        params.add(2, "action");
        params.add(3, "setPassword");

        params.add(4, "username");
        params.add(5, username);

        params.add(6, "oldPassword");
        params.add(7, oldPassword);

        params.add(8, "newPassword");
        params.add(9, newPassword);

        return sendPostRequest(params) == PostRequestStatus.Success;
    }
    /* End SharedDatabaseOperations methods */
}
