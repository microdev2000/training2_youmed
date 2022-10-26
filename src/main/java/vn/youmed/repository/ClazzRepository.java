package vn.youmed.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import vn.youmed.model.Clazz;

public interface ClazzRepository {
	Single<JsonObject> addClazz(Clazz clazz);

	Single<List<JsonObject>> getAllClazz();

	Single<JsonObject> getClazzById(String clazzId);

	Single<JsonObject> updateClazz(String clazzId, Clazz clazz);

	Single<JsonObject> deleteClazz(String clazzId);

}
