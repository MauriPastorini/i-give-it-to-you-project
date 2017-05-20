package Domain;

/**
 * Created by Mauri on 20-May-17.
 */

public class ResponseHttp {
    public int codeResponse;
    public String message;

    public int getCodeResponse() {
        return codeResponse;
    }

    public void setCodeResponse(int codeResponse) {
        this.codeResponse = codeResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseHttp(int codeResponse) {
        this.codeResponse = codeResponse;
    }
}
