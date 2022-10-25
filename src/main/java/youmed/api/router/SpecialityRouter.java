package youmed.api.router;

import java.util.List;
import java.util.NoSuchElementException;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import youmed.api.service.ClazzService;
import youmed.api.service.SpecialityService;

public class SpecialityRouter extends AbstractVerticle {

	private SpecialityService specialityService;

	@Override
	public void start() {
		specialityService = new SpecialityService();
		HttpServer server = vertx.createHttpServer();
		Router classRouter = Router.router(vertx);
		classRouter.post("/speciality/add").handler(this::addSpeciality);
		classRouter.get("/speciality/get/all").handler(this::getAll);
		classRouter.get("/speciality/get/:specialityId").handler(this::getSpecialityById);
		classRouter.put("/speciality/update/:specialityId").handler(this::updateSpeciality);
		classRouter.delete("/speciality/delete/:specialityId").handler(this::deleteSpeciality);
		server.requestHandler(classRouter::accept).listen(4545);

	}

	private void addSpeciality(RoutingContext rc) {
		String speciality = rc.getBodyAsString();
		specialityService.addSpeciality(speciality).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void getAll(RoutingContext rc) {
		specialityService.getAllSpeciality().subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void getSpecialityById(RoutingContext rc) {
		String specialityId = rc.request().getParam("clazzId");
		specialityService.getSpecialityById(specialityId).subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateSpeciality(RoutingContext rc) {
		String specialityId = rc.request().getParam("specialityId");
		String jsonPayLoad = rc.getBodyAsString();
		specialityService.updateSpeciality(specialityId, jsonPayLoad).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void deleteSpeciality(RoutingContext rc) {
		String specialityId = rc.request().getParam("specialityId");
		specialityService.deleteSpeciality(specialityId).subscribe(success -> {
			onSuccessResponse(rc, 204, null);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
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
