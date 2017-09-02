package radonsoft.mireaassistant.network;

import io.reactivex.Single;
import radonsoft.mireaassistant.model.RequestWrapper;
import retrofit2.http.POST;

/**
 * Created by Ruslan on 02.09.17.
 */
public interface GroupsService {

    @POST("group/get/all")
    Single<RequestWrapper> getGroups();

}
