package youmed.api.repository;

import java.util.List;

import io.reactivex.Single;
import io.vertx.core.json.JsonObject;
import youmed.api.model.Student;

public interface StudentRepository {
	Single<JsonObject> addStudent(Student student);

	Single<List<JsonObject>> getAllStudent();

	Single<JsonObject> getStudentById(String clazzId);

	Single<JsonObject> updateStudent(String clazzId, String payload);

	Single<JsonObject> deleteStudent(String clazzId);
}
