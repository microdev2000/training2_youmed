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
import vn.youmed.model.Clazz;
import vn.youmed.repository.ClazzRepository;

public class ClazzService extends AbstractVerticle implements ClazzRepository {

	static MongoClient client;

	@Override
	public void start() {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
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
					System.out.println(res.cause());
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
					System.out.println(res.cause());
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
					System.out.println(res.cause());
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
					System.out.println(res.cause());
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
					System.out.println(res.cause());
				}
			});

		});
	}
}
