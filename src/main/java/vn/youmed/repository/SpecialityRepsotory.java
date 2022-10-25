package vn.youmed.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import vn.youmed.model.Speciality;

public interface SpecialityRepsotory {
	Single<JsonObject> addSpeciality(Speciality speciality);

	Single<List<JsonObject>> getAllSpeciality();

	Single<JsonObject> getSpecialityById(String clazzId);

	Single<JsonObject> updateSpeciality(String clazzId, Speciality speciality);

	Single<JsonObject> deleteSpeciality(String clazzId);
}
