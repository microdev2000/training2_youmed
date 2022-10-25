package youmed.api.service;

import java.nio.channels.AlreadyBoundException;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import youmed.api.config.DBConfig;
import youmed.api.constant.Collection;
import youmed.api.model.Student;
import youmed.api.repository.StudentRepository;

public class StudentService extends AbstractVerticle implements StudentRepository {

	static MongoClient client;

	@Override
	public void start() {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
	}

	@Override
	public Single<JsonObject> addStudent(Student student) {
		return Single.create(result -> {		
			client.insert(Collection.STUDENT, JsonObject.mapFrom(student), res -> {
				if (res.succeeded()) {
					result.onSuccess(new JsonObject(res.result()));
				} else {
					result.onError(new AlreadyBoundException());
				}
			});
		});
	}

	@Override
	public Single<List<JsonObject>> getAllStudent() {
		return Single.create(result -> {
			client.find(Collection.STUDENT, new JsonObject(), res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> getStudentById(String clazzId) {
		return Single.create(result -> {
			client.findOne(Collection.STUDENT, new JsonObject(), null, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> updateStudent(String clazzId, String payload) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);

		JsonObject update = new JsonObject();
		update.put("$set", new JsonObject(payload));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.STUDENT, query, update, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> deleteStudent(String clazzId) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);
		return Single.create(result -> {
			client.findOneAndDelete(Collection.STUDENT, query, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});

		});
	}
}
