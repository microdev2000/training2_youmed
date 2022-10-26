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
import vn.youmed.model.Limit;
import vn.youmed.service.LimitService;

public class LimitRouter extends AbstractVerticle {

	private LimitService limitService;
	
	private MongoClient client;


	@Override
	public void start() throws SAXException, IOException {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
		limitService = new LimitService(client);
		HttpServer server = vertx.createHttpServer();
		Router limitRouter = Router.router(vertx);
		limitRouter.route("/api/v1/limit/*").handler(BodyHandler.create());
		limitRouter.get("/api/v1/limit/").handler(this::getAll);
		limitRouter.put("/api/v1/limit/:id").handler(this::updateLimit);
		server.requestHandler(limitRouter::accept).listen(4545);

	}

	private void getAll(RoutingContext rc) {
		limitService.getAll().subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateLimit(RoutingContext rc) {
		String limitId = rc.request().getParam("id");
		Limit limit = mapRequestBodyToLimit(rc);
		limitService.updateLimit(limitId, limit).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private Limit mapRequestBodyToLimit(RoutingContext rc) {
		Limit limit = new Limit();

		try {
			limit = rc.getBodyAsJson().mapTo(Limit.class);
		} catch (IllegalArgumentException ex) {
			onErrorResponse(rc, 400, ex);
		}

		return limit;
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
