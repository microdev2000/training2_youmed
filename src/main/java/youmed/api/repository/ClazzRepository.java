package youmed.api.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface ClazzRepository {
	Single<JsonObject> addClazz(String payload);

	Single<List<JsonObject>> getAllClazz();

	Single<JsonObject> getClazzById(String clazzId);

	Single<JsonObject> updateClazz(String clazzId, String payload);

	Single<JsonObject> deleteClazz(String clazzId);

}
