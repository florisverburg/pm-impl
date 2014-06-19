package helpers;

import com.fasterxml.jackson.databind.JsonNode;
import play.Play;
import play.libs.WS;
import play.mvc.*;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.SecureRandom;

/**
 * Created by Freek on 16/05/14.
 * Linkedin helper for OAuth 2 authentication and api calls to Linkedin.
 */
public class LinkedinConnection {

    /**
     * The timeout parameter after
     */
    public static final int TIMEOUT = 10000;

    /**
     * he amount of random bits that needs to be generated for the state
     */
    private static final int STATE_RANDOM_BITS = 130;

    /**
     * The base number of the random state generated number
     */
    private static final int STATE_RANDOM_BASE = 32;

    /**
     * The redirection URL
     */
    private static final String REDIRECT_URL = "http://localhost:9000/auth";

    /**
     * The client identifier of the Linkedin API
     */
    private static final String CLIENT_ID = Play.application().configuration().getString("linkedin.client_id");

    /**
     * The client secret of the Linkedin API
     */
    private static final String CLIENT_SECRET = Play.application().configuration().getString("linkedin.client_secret");


    /**
     * The access token from linkedin
     */
    private String accessToken;

    /**
     * In how much time the authentication token expires
     */
    private Long expires;

    /**
     * Gets access token.
     * @return The access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Sets access token.
     * @param accessToken The access token
     */
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    /**
     * Gets expires.
     * @return The expires
     */
    public Long getExpires() {
        return expires;
    }

    /**
     * Sets expires.
     * @param expires The expires
     */
    public void setExpires(Long expires) {
        this.expires = expires;
    }

    /**
     * Instantiates a new Linkedin connection.
     * @param accessToken the access token
     * @param expires the expires
     */
    public LinkedinConnection(String accessToken, Long expires) {
        this.accessToken = accessToken;
        this.expires = expires;
    }

    /**
     * Generates a random state of length 32
     * @return The generated state
     */
    public static String generateState() {
        // Generate a random state and save in a session
        String state = new BigInteger(STATE_RANDOM_BITS, new SecureRandom()).toString(STATE_RANDOM_BASE);
        return state;
    }

    /**
     * Generate a new redirect URL for the Linkedin OAuth 2 authentication
     * @param state The session state check
     * @return The redirect URL
     */
    public static String generateRedirectUri(String state) {
        // Create the URL
        try {
            return "https://www.linkedin.com/uas/oauth2/authorization?"
                    + "response_type=code"
                    + "&client_id=" + URLEncoder.encode(CLIENT_ID, "UTF-8")
                    + "&state=" + URLEncoder.encode(state, "UTF-8")
                    + "&redirect_uri=" + URLEncoder.encode(REDIRECT_URL, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "#";
    }

    /**
     * Generates a new Linkedin OAuth connection from an Linkedin code.
     * @param code The access code
     * @return The Linkedin OAuth object if successful else null
     */
    public static LinkedinConnection fromAccesToken(String code) {
        WS.Response response = WS.url("https://www.linkedin.com/uas/oauth2/accessToken")
                .setQueryParameter("grant_type", "authorization_code")
                .setQueryParameter("code", code)
                .setQueryParameter("redirect_uri", REDIRECT_URL)
                .setQueryParameter("client_id", CLIENT_ID)
                .setQueryParameter("client_secret", CLIENT_SECRET)
                .get().get(TIMEOUT);

        // Check if valid response
        if(response.getStatus() != Http.Status.OK) {
            return null;
        }

        // Parse the JSON
        JsonNode json = response.asJson();
        Long expiresIn = json.get("expires_in").asLong();
        String accessToken = json.get("access_token").asText();

        return new LinkedinConnection(accessToken, expiresIn);
    }

    /**
     * Generate a new Linkedin OAuth connection from the current session
     * @return The Linkedin OAuth connection of successful else null
     */
    public static LinkedinConnection fromSession() {
        Http.Session currentSession = Http.Context.current().session();

        // Check if exists and isn't expired
        if(currentSession.get("linkedin_expires") != null
                && Long.parseLong(currentSession.get("linkedin_expires")) > 0) {
            return null;
        }

        return new LinkedinConnection(currentSession.get("linkedin_accessToken"),
                Long.parseLong(currentSession.get("linkedin_expires")));
    }

    /**
     * Save the current OAuth Linkedin connection information to the session
     */
    public void toSession() {
        Http.Session currentSession = Http.Context.current().session();

        currentSession.put("linkedin_accessToken", this.getAccessToken());
        currentSession.put("linkedin_expires", this.getExpires().toString());
    }

    /**
     * Do an API call using the access token
     * @param url The url of the API call
     * @return The JSon response if successful else null
     */
    public JsonNode apiCall(String url) {
        WS.Response response = WS.url("https://api.linkedin.com/v1/"+url)
                .setQueryParameter("format", "json")
                .setQueryParameter("oauth2_access_token", this.getAccessToken())
                .get().get(TIMEOUT);

        // Check if valid response
        if(response.getStatus() != Http.Status.OK) {
            return null;
        }

        return response.asJson();
    }

    /**
     * Retrieve the current person id from the API
     * @return The person id if found
     */
    public String getPersonId() {
        JsonNode response = apiCall("people/~:(id)");
        return (response == null)? null : response.get("id").asText();
    }

    /**
     * Create a new user based on the information from Linkedin
     * @return The created user
     */
    public JsonNode getPerson() {
        return apiCall("people/~:(first-name,last-name,email-address)");
    }
}
