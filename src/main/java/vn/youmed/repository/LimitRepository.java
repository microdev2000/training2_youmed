package vn.youmed.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import vn.youmed.model.Limit;

public interface LimitRepository {
	Single<List<JsonObject>> getAll();
	Single<JsonObject> updateLimit(String clazzId, Limit limit);
	void limitAction(String clazzId, Future<Boolean> future, String action);
}
 