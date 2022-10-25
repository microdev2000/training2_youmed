package youmed.api.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;

public interface LimitRepository {
	Single<List<JsonObject>> getAll();
	Single<JsonObject> updateLimit(String clazzId, String payload);
	Single<JsonObject> checkLimit(String clazzId);
}
