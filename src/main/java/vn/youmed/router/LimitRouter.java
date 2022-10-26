package vn.youmed.router;

import java.io.IOException;

import org.xml.sax.SAXException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import vn.youmed.config.DBConfig;
import vn.youmed.model.Clazz;
import vn.youmed.service.ClazzService;

public class LimitRouter extends AbstractVerticle {

	private ClazzService clazzService;
	
	private MongoClient client;


	@Override
	public void start() throws SAXException, IOException {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
		clazzService = new ClazzService(client);
		HttpServer server = vertx.createHttpServer();
		Router limitRouter = Router.router(vertx);
		limitRouter.route("/api/v1/limit/*").handler(BodyHandler.create());
		limitRouter.get("/api/v1/limit/").handler(this::getAll);
		limitRouter.put("/api/v1/limit/:id").handler(this::updateLimit);
		server.requestHandler(limitRouter::accept).listen(4545);

	}

	private void getAll(RoutingContext rc) {
		clazzService.getAll().subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateLimit(RoutingContext rc) {
		String clazzId = rc.request().getParam("id");
		Clazz limit = mapRequestBodyToLimit(rc);
		clazzService.updateLimit(clazzId, limit.getMaximum()).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private Clazz mapRequestBodyToLimit(RoutingContext rc) {
		Clazz clazz = new Clazz();

		try {
			clazz = rc.getBodyAsJson().mapTo(Clazz.class);
		} catch (IllegalArgumentException ex) {
			onErrorResponse(rc, 400, ex);
		}

		return clazz;
	}

	private void onSuccessResponse(RoutingContext rc, int status, Object object) {
		rc.response().setStatusCode(status).putHeader("Content-Type", "application/json")
				.end(Json.encodePrettily(object));
	}

	private void onErrorResponse(RoutingContext rc, int status, Throwable throwable) {
		final JsonObject error = new JsonObject().put("error", throwable.getMessage());

		rc.response().setStatusCode(status).putHeader("Content-Type", "application/json")
				.end(Json.encodePrettily(error));
	}
}
