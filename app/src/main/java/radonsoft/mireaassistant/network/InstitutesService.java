package radonsoft.mireaassistant.network;

import io.reactivex.Single;
import radonsoft.mireaassistant.model.RequestWrapper;
import retrofit2.http.POST;

/**
 * Created by Ruska on 02.09.2017.
 */
public interface InstitutesService {

    @POST("group/get/all")
    Single<RequestWrapper> getInstitutes();


}
