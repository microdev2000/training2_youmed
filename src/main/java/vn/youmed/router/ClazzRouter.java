package vn.youmed.router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import vn.youmed.model.Clazz;
import vn.youmed.service.ClazzService;

public class ClazzRouter extends AbstractVerticle {

	private ClazzService clazzService;

	@Override
	public void start() {
		clazzService = new ClazzService();
		HttpServer server = vertx.createHttpServer();
		Router classRouter = Router.router(vertx);
		classRouter.route("/class/*").handler(BodyHandler.create());
		classRouter.post("/clazz/add").handler(this::addClazz);
		classRouter.get("/clazz/get/all").handler(this::getAll);
		classRouter.get("/clazz/get/:clazzId").handler(this::getClazzById);
		classRouter.put("/clazz/update/:clazzId").handler(this::updateClazz);
		classRouter.delete("/clazz/delete/:clazzId").handler(this::deleteClazz);
		server.requestHandler(classRouter::accept).listen(4545);

	}

	private void addClazz(RoutingContext rc) {
		Clazz clazz = mapRequestBodyToClazz(rc);
		System.out.println(clazz);
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
		String clazzId = rc.request().getParam("clazzId");
		clazzService.getClazzById(clazzId).subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateClazz(RoutingContext rc) {
		String clazzId = rc.request().getParam("clazzId");
		Clazz clazz = mapRequestBodyToClazz(rc);
		clazzService.updateClazz(clazzId, clazz).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void deleteClazz(RoutingContext rc) {
		String clazzId = rc.request().getParam("clazzId");
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
