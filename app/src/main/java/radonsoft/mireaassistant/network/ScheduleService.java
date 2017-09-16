package radonsoft.mireaassistant.network;

import io.reactivex.Single;
import radonsoft.mireaassistant.forms.ScheduleForm;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Ruska on 14.09.2017.
 */
public interface ScheduleService {
    @POST("schedule/get")
    Single<radonsoft.mireaassistant.model.schedule.Schedule> getScheduleName(@Body ScheduleForm scheduleForm);

}
