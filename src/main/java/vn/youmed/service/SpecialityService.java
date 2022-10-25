package vn.youmed.service;

import java.nio.channels.AlreadyBoundException;
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
import vn.youmed.model.Speciality;
import vn.youmed.repository.SpecialityRepsotory;

public class SpecialityService extends AbstractVerticle implements SpecialityRepsotory {
	static MongoClient client;

	@Override
	public void start() {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
	}

	@Override
	public Single<JsonObject> addSpeciality(Speciality speciality) {
		return Single.create(result -> {
			client.insert(Collection.SPECIALITY, JsonObject.mapFrom(speciality), res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new Exception("Speciality already exists!"));
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
	public Single<List<JsonObject>> getAllSpeciality() {
		return Single.create(result -> {
			client.find(Collection.SPECIALITY, new JsonObject(), res -> {
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
	public Single<JsonObject> getSpecialityById(String specialityId) {
		JsonObject query = new JsonObject();
		query.put("_id", specialityId);
		return Single.create(result -> {
			client.findOne(Collection.SPECIALITY, query, null, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Specility does not exist!"));
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
	public Single<JsonObject> updateSpeciality(String specialityId, Speciality speciality) {
		JsonObject query = new JsonObject();
		query.put("_id", specialityId);

		JsonObject update = new JsonObject();
		update.put("$set", JsonObject.mapFrom(speciality));
		return Single.create(result -> {
			client.findOneAndUpdate(Collection.SPECIALITY, query, update, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Specility does not exist!"));
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
	public Single<JsonObject> deleteSpeciality(String specialityId) {
		JsonObject query = new JsonObject();
		query.put("_id", specialityId);
		return Single.create(result -> {
			client.findOneAndDelete(Collection.SPECIALITY, query, res -> {
				if (res.succeeded()) {
					if (res.result() == null) {
						result.onError(new NoSuchElementException("Specility does not exist!"));
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
