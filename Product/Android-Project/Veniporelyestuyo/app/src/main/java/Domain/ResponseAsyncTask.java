package Domain;

/**
 * Created by Mauri on 28-May-17.
 */

public class ResponseAsyncTask<T> {
    public enum TypeResponse{
        OK,EXCEPTION
    }
    private TypeResponse typeResponse;
    private T dataResponse;

    public ResponseAsyncTask(TypeResponse typeResponse, T dataResponse) {
        this.typeResponse = typeResponse;
        this.dataResponse = dataResponse;
    }

    public TypeResponse getTypeResponse() {
        return typeResponse;
    }

    public void setTypeResponse(TypeResponse typeResponse) {
        this.typeResponse = typeResponse;
    }

    public T getDataResponse() {
        return dataResponse;
    }

    public void setDataResponse(T dataResponse) {
        this.dataResponse = dataResponse;
    }
}
