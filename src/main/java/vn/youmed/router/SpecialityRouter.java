package vn.youmed.router;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import vn.youmed.model.Speciality;
import vn.youmed.service.SpecialityService;

public class SpecialityRouter extends AbstractVerticle {

	private SpecialityService specialityService;

	@Override
	public void start() {
		specialityService = new SpecialityService();
		HttpServer server = vertx.createHttpServer();
		Router specRouter = Router.router(vertx);
		specRouter.post("/speciality/add").handler(this::addSpeciality);
		specRouter.get("/speciality/get/all").handler(this::getAll);
		specRouter.get("/speciality/get/:specialityId").handler(this::getSpecialityById);
		specRouter.put("/speciality/update/:specialityId").handler(this::updateSpeciality);
		specRouter.delete("/speciality/delete/:specialityId").handler(this::deleteSpeciality);
		server.requestHandler(specRouter::accept).listen(4545);

	}

	private void addSpeciality(RoutingContext rc) {
		Speciality speciality = mapRequestBodyToSpeciality(rc);
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
		Speciality speciality = mapRequestBodyToSpeciality(rc);
		specialityService.updateSpeciality(specialityId, speciality).subscribe(success -> {
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

	private Speciality mapRequestBodyToSpeciality(RoutingContext rc) {
		Speciality speciality = new Speciality();

		try {
			speciality = rc.getBodyAsJson().mapTo(Speciality.class);
		} catch (IllegalArgumentException ex) {
			onErrorResponse(rc, 400, ex);
		}

		return speciality;
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
