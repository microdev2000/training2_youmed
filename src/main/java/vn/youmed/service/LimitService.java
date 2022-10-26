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
import vn.youmed.model.Limit;
import vn.youmed.repository.LimitRepository;

public class LimitService implements LimitRepository {

	private final MongoClient client;

	public LimitService(MongoClient client) {
		this.client = client;
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
					result.onError(new Exception("System error, please try again later!"));
				}
			});
		});
	}

	@Override
	public void limitAction(String limitId, Future<Boolean> future, String action) {
		JsonObject limitQuery = new JsonObject();
		limitQuery.put("_id", limitId);
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
