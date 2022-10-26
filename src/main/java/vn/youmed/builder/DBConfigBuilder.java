package vn.youmed.builder;


import io.vertx.core.AbstractVerticle;
import io.vertx.ext.mongo.MongoClient;
import vn.youmed.config.DBConfig;

public class DBConfigBuilder extends AbstractVerticle {

	public static MongoClient mongoClient;
	@Override
	public void start() {
		mongoClient = MongoClient.createShared(vertx, DBConfig.dbConfig());
	}
}

