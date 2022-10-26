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
import vn.youmed.model.Speciality;
import vn.youmed.service.SpecialityService;

public class SpecialityRouter extends AbstractVerticle {

	private SpecialityService specialityService;
	private MongoClient client;


	@Override
	public void start() throws SAXException, IOException {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
		specialityService = new SpecialityService(client);
		HttpServer server = vertx.createHttpServer();
		Router specRouter = Router.router(vertx);
		specRouter.route("/api/v1/speciality/*").handler(BodyHandler.create());
		specRouter.post("/api/v1/speciality/").handler(this::addSpeciality);
		specRouter.get("/api/v1/speciality/").handler(this::getAll);
		specRouter.get("/api/v1/speciality/:id").handler(this::getSpecialityById);
		specRouter.put("/api/v1/speciality/:id").handler(this::updateSpeciality);
		specRouter.delete("/api/v1/speciality/:id").handler(this::deleteSpeciality);
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
		String specialityId = rc.request().getParam("id");
		specialityService.getSpecialityById(specialityId).subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateSpeciality(RoutingContext rc) {
		String specialityId = rc.request().getParam("id");
		Speciality speciality = mapRequestBodyToSpeciality(rc);
		specialityService.updateSpeciality(specialityId, speciality).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void deleteSpeciality(RoutingContext rc) {
		String specialityId = rc.request().getParam("id");
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
