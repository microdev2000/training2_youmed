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
import vn.youmed.model.Clazz;
import vn.youmed.repository.ClazzRepository;

public class ClazzService implements ClazzRepository {

	private final MongoClient client;

	public ClazzService(MongoClient client) {
		this.client = client;
	}

	@Override
	public Single<JsonObject> addClazz(Clazz clazz) {
		return Single.create(result -> {
			client.insert(Collection.CLAZZ, JsonObject.mapFrom(clazz), res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new Exception("Class already exists!"));
					} else {
						result.onSuccess(new JsonObject(res.result()));
					}
				} else {
					result.onError(new Exception("System error, please try again later!"));
				}
			});
		});
	}

	@Override
	public Single<List<JsonObject>> getAllClazz() {
		return Single.create(result -> {
			client.find(Collection.CLAZZ, new JsonObject(), res -> {
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
	public Single<JsonObject> getClazzById(String clazzId) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);
		return Single.create(result -> {
			client.findOne(Collection.CLAZZ, query, null, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Class not found!"));
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
	public Single<JsonObject> updateClazz(String clazzId, Clazz clazz) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);
		JsonObject update = new JsonObject();
		update.put("$set", JsonObject.mapFrom(clazz));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.CLAZZ, query, update, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Class does not exist!"));
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
	public Single<JsonObject> deleteClazz(String clazzId) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);
		return Single.create(result -> {
			client.findOneAndDelete(Collection.CLAZZ, query, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Class does not exist!"));
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
	public Single<List<JsonObject>> getAll() {
		return Single.create(result -> {
			client.find(Collection.LIMIT, new JsonObject(), res -> {
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

	public Single<JsonObject> updateLimit(String clazzId, int value) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);

		JsonObject update = new JsonObject();
		update.put("$set", new JsonObject().put("maximum", value));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.LIMIT, query, update, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Class does not exist"));
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
	public void limitAction(String clazzId, Future<Boolean> future, String action) {
		JsonObject limitQuery = new JsonObject();
		limitQuery.put("_id", clazzId);
		client.findOne(Collection.CLAZZ, limitQuery, null, res -> {
			if (res.succeeded()) {
				if (res.result() == null) {
					future.fail("Class does not exist");
				} else {
					int maximum = res.result().getInteger("maximum");
					int total = res.result().getInteger("totoal");
					if (total == maximum) {
						future.fail("The allowed limit has been reached!");
					} else {
						JsonObject limitUpdate = new JsonObject();
						total = action.equalsIgnoreCase(LimitAction.INCREASE) ? (total + 1) : (total - 1);
						limitUpdate.put("$set", new JsonObject().put("total", total + 1));
						client.findOneAndUpdate(Collection.LIMIT, limitQuery, limitUpdate, res2 -> {
							if (res2.succeeded()) {
								if (res2.result() == null) {
									future.fail("Class does not exist");
								} else {
									future.complete();
								}
							} else {
								future.fail("System error, please try again later!");
							}
						});
					}
				}
			} else {
				future.fail("System error, please try again later!");
			}
		});
	}
}
