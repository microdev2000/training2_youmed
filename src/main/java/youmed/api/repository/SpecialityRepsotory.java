package youmed.api.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface SpecialityRepsotory {
	Single<JsonObject> addSpeciality(String payload);

	Single<List<JsonObject>> getAllSpeciality();

	Single<JsonObject> getSpecialityById(String clazzId);

	Single<JsonObject> updateSpeciality(String clazzId, String payload);

	Single<JsonObject> deleteSpeciality(String clazzId);
}
