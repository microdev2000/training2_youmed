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

public class ClazzRouter extends AbstractVerticle {

	private ClazzService clazzService;
	
	private MongoClient client;

	@Override
	public void start() throws SAXException, IOException {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
		clazzService = new ClazzService(client);
		HttpServer server = vertx.createHttpServer();
		Router classRouter = Router.router(vertx);
		classRouter.route("/api/v1/class/*").handler(BodyHandler.create());
		classRouter.post("/api/v1/class").handler(this::addClazz);
		classRouter.get("/api/v1/class").handler(this::getAll);
		classRouter.get("/api/v1/class/:id").handler(this::getClazzById);
		classRouter.put("/api/v1/class/:id").handler(this::updateClazz);
		classRouter.delete("/api/v1/class/:id").handler(this::deleteClazz);
		server.requestHandler(classRouter::accept).listen(4545);

	}

	private void addClazz(RoutingContext rc) {
		Clazz clazz = mapRequestBodyToClazz(rc);
		clazzService.addClazz(clazz).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void getAll(RoutingContext rc) {
		clazzService.getAllClazz().subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void getClazzById(RoutingContext rc) {
		String clazzId = rc.request().getParam("id");
		clazzService.getClazzById(clazzId).subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateClazz(RoutingContext rc) {
		String clazzId = rc.request().getParam("id");
		Clazz clazz = mapRequestBodyToClazz(rc);
		clazzService.updateClazz(clazzId, clazz).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void deleteClazz(RoutingContext rc) {
		String clazzId = rc.request().getParam("id");
		clazzService.deleteClazz(clazzId).subscribe(success -> {
			onSuccessResponse(rc, 204, null);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private Clazz mapRequestBodyToClazz(RoutingContext rc) {
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
