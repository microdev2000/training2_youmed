package vn.youmed.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import vn.youmed.model.Clazz;

public interface ClazzRepository {
	Single<JsonObject> addClazz(Clazz clazz);

	Single<List<JsonObject>> getAllClazz();

	Single<JsonObject> getClazzById(String clazzId);

	Single<JsonObject> updateClazz(String clazzId, Clazz clazz);

	Single<JsonObject> deleteClazz(String clazzId);

	Single<List<JsonObject>> getAll();

	Single<JsonObject> updateLimit(String clazzId, int value);

	void limitAction(String clazzId, Future<Boolean> future, String action);

}
