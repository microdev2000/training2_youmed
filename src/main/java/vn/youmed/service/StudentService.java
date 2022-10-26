package vn.youmed.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Single;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import vn.youmed.constant.Collection;
import vn.youmed.constant.LimitAction;
import vn.youmed.model.Student;
import vn.youmed.repository.StudentRepository;

public class StudentService implements StudentRepository {
	
	private final MongoClient client;
	
	public StudentService(MongoClient client) {
		this.client = client;
	}
	
	@Override
	public Single<JsonObject> addStudent(Student student) {
		String limitId = JsonObject.mapFrom(student).getString("clazzId");
		return Single.create(result -> {
			Future<Boolean> checkLimit = Future.future();
			LimitService limitService = new LimitService(client);
			limitService.limitAction(limitId, checkLimit, LimitAction.INCREASE);
			checkLimit.setHandler(res -> {
				if (res.failed()) {
					result.onError(new Exception(res.cause().getMessage()));
				} else {
					client.insert(Collection.STUDENT, JsonObject.mapFrom(student), res2 -> {
						if (res2.succeeded()) {
							if (res2.result() == null) {
								result.onError(new Exception("Student already exist1"));
							} else {
								result.onSuccess(new JsonObject(res2.result()));
							}
						} else {
							result.onError(new Exception("System error, please try again later!"));
						}
					});
				}
			});

		});
	}

	@Override
	public Single<List<JsonObject>> getAllStudent() {
		return Single.create(result -> {
			client.find(Collection.STUDENT, new JsonObject(), res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onSuccess(new ArrayList<JsonObject>());
					} else {
						result.onSuccess(res.result());
					}
				} else {
					result.onError(new Exception("System error, please try again later!"));
				}
			});
		});
	}

	@Override
	public Single<JsonObject> getStudentById(String clazzId) {
		return Single.create(result -> {
			client.findOne(Collection.STUDENT, new JsonObject(), null, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Student not found!"));
					} else {
						result.onSuccess(res.result());
					}
				} else {
					result.onError(new Exception("System error, please try again later!"));
				}
			});
		});
	}

	@Override
	public Single<JsonObject> updateStudent(String clazzId, Student student) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);

		JsonObject update = new JsonObject();
		update.put("$set", JsonObject.mapFrom(student));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.STUDENT, query, update, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Student does not exist!"));
					} else {
						result.onSuccess(res.result());
					}
				} else {
					result.onError(new Exception("System error, please try again later!"));
				}
			});
		});
	}

	@Override
	public Single<JsonObject> deleteStudent(String clazzId) {
		return Single.create(result -> {
			Future<Boolean> future = Future.future();

			JsonObject query = new JsonObject();
			query.put("_id", clazzId);
			new LimitService(client).limitAction(clazzId, future, LimitAction.REDUCED);
			future.setHandler(res -> {
				if (res.failed()) {
					result.onError(new Exception(res.cause().getMessage()));
				} else {
					client.findOneAndDelete(clazzId, query, res2 -> {
						if (res2.succeeded()) {
							if (res2.result() == null) {
								result.onError(new Exception("Student does not exits"));
							} else {
								result.onSuccess(new JsonObject());
							}
						} else {
							result.onError(new Exception("System error, please try again later!"));

						}
					});
				}
			});

		});
	}
}