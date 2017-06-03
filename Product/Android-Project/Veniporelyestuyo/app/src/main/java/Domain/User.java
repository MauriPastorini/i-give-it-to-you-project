package Domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by faustosanchez on 3/6/17.
 */

public class User {

    private String username;
    private String email;
    private String password;

    public User(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public JSONObject GetAsJSON() throws JSONException{
        JSONObject user = new JSONObject();
        user.put("username", this.username);
        user.put("email", this.email);
        user.put("password", this.password);
        user.put("confirmPassword", this.password);
        return user;
    }

}
