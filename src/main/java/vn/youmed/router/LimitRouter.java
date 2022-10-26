package vn.youmed.router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import vn.youmed.model.Clazz;
import vn.youmed.model.Limit;
import vn.youmed.service.ClazzService;
import vn.youmed.service.LimitService;

public class LimitRouter extends AbstractVerticle {

	private LimitService limitService;

	@Override
	public void start() {
		limitService = new LimitService();
		HttpServer server = vertx.createHttpServer();
		Router limitRouter = Router.router(vertx);
		limitRouter.get("/limit/get/all").handler(this::getAll);
		limitRouter.put("/limit/update/:limitId").handler(this::updateLimit);
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
		String limitId = rc.request().getParam("limitId");
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
