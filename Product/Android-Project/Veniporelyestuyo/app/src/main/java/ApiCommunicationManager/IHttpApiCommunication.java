package ApiCommunicationManager;

import MyExceptions.PostReturnFunctionException;

/**
 * Created by Mauri on 17-May-17.
 */

public interface IHttpApiCommunication {
    void postFunctionReturn(Object obj) throws PostReturnFunctionException;
}
