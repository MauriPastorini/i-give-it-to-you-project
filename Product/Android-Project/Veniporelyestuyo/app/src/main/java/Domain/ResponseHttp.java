package Domain;

import static android.os.Build.VERSION_CODES.M;

/**
 * Created by Mauri on 20-May-17.
 */

public class ResponseHttp {
    private int codeResponse;
    private String message;
    private Object messageObject;

    public Object getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(Object messageObject) {
        this.messageObject = messageObject;
    }

    private String messageToClient;
    private CategoryCodeResponse typeCode;

    public enum CategoryCodeResponse{
        INFORMATIONAL,SUCCESS,REDIRECTION,CLIENT_ERROR,SERVER_ERROR
    }

    public ResponseHttp(int codeResponse) {
        setCodeResponse(codeResponse);
    }

    public int getCodeResponse() {
        return codeResponse;
    }

    public void setCodeResponse(int codeResponse) {
        this.codeResponse = codeResponse;
        this.setTypeCode(codeResponse);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public CategoryCodeResponse getTypeCode() {
        return typeCode;
    }

    private void setTypeCode(int typeCode) {
        if(typeCode>=100 && typeCode < 200){
            this.typeCode = CategoryCodeResponse.INFORMATIONAL;
        }
        else if(typeCode>=200 && typeCode < 300){
            this.typeCode = CategoryCodeResponse.SUCCESS;
        }
        else if(typeCode>=300 && typeCode < 400){
            this.typeCode = CategoryCodeResponse.REDIRECTION;
        }
        else if(typeCode>=400 && typeCode < 500){
            this.typeCode = CategoryCodeResponse.CLIENT_ERROR;
        }
        else if(typeCode>=500 && typeCode < 600){
            this.typeCode = CategoryCodeResponse.SERVER_ERROR;
        }
    }

    public String getMessageToClient() {
        if(typeCode == CategoryCodeResponse.CLIENT_ERROR)
            return "Hubo un problema en su solicitud.";
        if(typeCode == CategoryCodeResponse.SERVER_ERROR)
            return "Oops! Hubo un problema en nuestro sistema. Intente mas tarde.";
        if(typeCode == CategoryCodeResponse.SUCCESS)
            return "Solicitud realizada";
        return "";
    }

}
