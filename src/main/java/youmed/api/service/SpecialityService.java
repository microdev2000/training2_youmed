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
import youmed.api.repository.SpecialityRepsotory;

public class SpecialityService extends AbstractVerticle implements SpecialityRepsotory {
	static MongoClient client;

	@Override
	public void start() {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
	}

	@Override
	public Single<JsonObject> addSpeciality(String payload) {
		System.out.println(payload);
		return Single.create(result -> {
			client.insert(Collection.SPECIALITY, new JsonObject(payload), res -> {
				if (res.succeeded()) {
					result.onSuccess(new JsonObject(res.result()));
				} else {
					result.onError(new AlreadyBoundException());
				}
			});
		});
	}

	@Override
	public Single<List<JsonObject>> getAllSpeciality() {
		return Single.create(result -> {
			client.find(Collection.SPECIALITY, new JsonObject(), res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> getSpecialityById(String specialityId) {
		JsonObject query = new JsonObject();
		query.put("_id", specialityId);
		return Single.create(result -> {
			client.findOne(Collection.SPECIALITY, query, null, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> updateSpeciality(String specialityId, String payload) {
		JsonObject query = new JsonObject();
		query.put("_id", specialityId);

		JsonObject update = new JsonObject();
		update.put("$set", new JsonObject(payload));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.SPECIALITY, query, update, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});
		});
	}

	@Override
	public Single<JsonObject> deleteSpeciality(String specialityId) {
		JsonObject query = new JsonObject();
		query.put("_id", specialityId);
		return Single.create(result -> {
			client.findOneAndDelete(Collection.SPECIALITY, query, res -> {
				if (res.succeeded()) {
					result.onSuccess(res.result());
				} else {
					result.onError(new NoSuchElementException());
				}
			});

		});
	}

}
