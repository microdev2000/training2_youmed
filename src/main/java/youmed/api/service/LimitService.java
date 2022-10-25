package youmed.api.service;

import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import youmed.api.config.DBConfig;
import youmed.api.constant.Collection;
import youmed.api.repository.LimitRepository;

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
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> updateLimit(String limitId, String payload) {
		
		JsonObject jsonObject = new JsonObject(payload);
		
		JsonObject query = new JsonObject();
		query.put("_id", limitId);

		JsonObject update = new JsonObject();
		update.put("$set", new JsonObject().put("maximum", jsonObject.getInteger("value")));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.LIMIT, query, update, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
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
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}
}
