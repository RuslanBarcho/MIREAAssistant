package radonsoft.mireaassistant.network;

import io.reactivex.Single;
import radonsoft.mireaassistant.fragments.Schedule;
import radonsoft.mireaassistant.model.RequestWrapper;
import radonsoft.mireaassistant.model.schedule.Day;
import radonsoft.mireaassistant.model.schedule.Schedule_;
import retrofit2.http.POST;

/**
 * Created by Ruska on 14.09.2017.
 */
public interface ScheduleService {
    @POST("schedule/get")
    Single<radonsoft.mireaassistant.model.schedule.Schedule> getScheduleName();

}
