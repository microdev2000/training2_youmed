package vn.youmed.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import vn.youmed.config.DBConfig;
import vn.youmed.constant.Collection;
import vn.youmed.model.Student;
import vn.youmed.repository.StudentRepository;

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
					if (res.result() == null) {
						result.onError(new Exception("Student already exist"));
					} else {
						String clazzAndLimit = new JsonObject(res.result()).getString("clazz");
						JsonObject queryClazzAndLimit = new JsonObject();
						queryClazzAndLimit.put("_id", clazzAndLimit);
						client.findOne(Collection.CLAZZ, queryClazzAndLimit, null, res1 -> {
							if (res1.succeeded()) {
								if (res1.result() == null) {
									result.onError(new Exception("Class does not exist!"));
								} else {
									client.findOne(Collection.LIMIT, queryClazzAndLimit, null, res2 -> {
										if (res2.succeeded()) {
											if (res2.result() == null) {
												result.onError(new Exception("Limit ID does not exist!"));
											} else {
												int maximum = res2.result().getInteger("maximum");
												int total = res2.result().getInteger("total");
												if (total == maximum) {
													result.onError(new Exception("Total student over limit"));
													return;
												}
												JsonObject updateLimit = new JsonObject();
												updateLimit.put("$set", new JsonObject().put("total", total + 1));
												client.findOneAndUpdate(Collection.LIMIT, queryClazzAndLimit,
														updateLimit, res3 -> {
															if (res3.failed()) {
																System.out.println(res3.cause());
															}
														});
											}
										} else {
											System.out.println(res2.cause());

										}
									});
								}
							} else {
								System.out.println(res1.cause());

							}
						});
					}
				} else {
					System.out.println(res.cause());
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
					System.out.println(res.cause());
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
					System.out.println(res.cause());
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
					System.out.println(res.cause());
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
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Student does not exist"));
					} else {
						String limitId = res.result().getString("clazz");
						JsonObject queryLimit = new JsonObject();
						queryLimit.put("_id", limitId);
						client.findOne(Collection.LIMIT, new JsonObject().put("_id", limitId), null, res1 -> {
							if (res1.succeeded()) {
								if (res1.result() == null) {
									result.onError(new NoSuchElementException("Limit ID does not exist"));
								} else {
									int total = res.result().getInteger("total");
									JsonObject updateLimit = new JsonObject();
									updateLimit.put("$set", new JsonObject().put("total", total - 1));
									client.findOneAndUpdate(Collection.LIMIT, query, updateLimit, res2 -> {
										if (res2.failed()) {
											System.out.println(res2.cause());
										}
									});
								}
							} else {
								System.out.println(res.cause());
							}
						});
					}
				} else {
					System.out.println(res.cause());
				}
			});

		});
	}
}
