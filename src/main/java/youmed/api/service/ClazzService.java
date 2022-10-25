package youmed.api.service;

import java.nio.channels.AlreadyBoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import youmed.api.config.DBConfig;
import youmed.api.constant.Collection;
import youmed.api.model.Clazz;
import youmed.api.repository.ClazzRepository;

public class ClazzService extends AbstractVerticle implements ClazzRepository {

	static MongoClient client;

	@Override
	public void start() {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
	}

	@Override
	public Single<JsonObject> addClazz(String payload) {
		return Single.create(result -> {
			client.insert(Collection.CLAZZ, new JsonObject(payload), res -> {
				if (res.succeeded()) {
					result.onSuccess(new JsonObject(res.result()));
				} else {
					result.onError(new AlreadyBoundException());
				}
			});
		});
	}

	@Override
	public Single<List<JsonObject>> getAllClazz() {
		return Single.create(result -> {
			client.find(Collection.CLAZZ, new JsonObject(), res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
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
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> updateClazz(String clazzId, String payload) {
		JsonObject query = new JsonObject();
		query.put("_id", clazzId);

		JsonObject update = new JsonObject();
		update.put("$set", new JsonObject(payload));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.CLAZZ, query, update, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
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
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});

		});
	}
}
