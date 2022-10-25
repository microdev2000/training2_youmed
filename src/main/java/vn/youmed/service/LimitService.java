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
import vn.youmed.model.Limit;
import vn.youmed.repository.LimitRepository;

public class LimitService extends AbstractVerticle implements LimitRepository {

	static MongoClient client;

	@Override
	public void start() {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
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
					System.out.println(res.cause());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> updateLimit(String limitId, Limit limit) {


		JsonObject query = new JsonObject();
		query.put("_id", limitId);

		JsonObject update = new JsonObject();
		update.put("$set", new JsonObject().put("maximum", JsonObject.mapFrom(limit)).getInteger("value"));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.LIMIT, query, update, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("The limit ID does not exist"));
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
	public Single<JsonObject> checkLimit(String limitId) {
		JsonObject query = new JsonObject();
		query.put("_id", limitId);
		return Single.create(result -> {
			client.findOne(Collection.CLAZZ, query, null, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("The limit ID does not exist"));
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
