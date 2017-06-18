package Domain;

import android.support.v7.widget.LinearLayoutCompat;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mauri on 28-May-17.
 */

public class Account {
    private String userName;
    private String email;
    private String password;
    private boolean isAdmin;

    public Account(String userName, String email, String password, boolean isAdmin) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.isAdmin = isAdmin;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail(){ return email; }

    public void setEmail(String email) { this.email = email; }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public JSONObject GetAsJSON() throws JSONException {
        JSONObject user = new JSONObject();
        user.put("username", this.userName);
        user.put("email", this.email);
        user.put("password", this.password);
        user.put("confirmPassword", this.password);
        user.put("isActive", this.isAdmin);
        return user;
    }

    @Override
    public String toString(){
        return this.userName;
    }
}
