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
import vn.youmed.model.Student;
import vn.youmed.service.StudentService;

public class StudentRouter extends AbstractVerticle {

	private StudentService studentService;
	private MongoClient client;

	@Override
	public void start() throws SAXException, IOException {
		client = MongoClient.createShared(vertx, DBConfig.dbConfig());
		studentService = new StudentService(client);
		HttpServer server = vertx.createHttpServer();
		Router stuRouter = Router.router(vertx);
		stuRouter.route("/api/v1/student/*").handler(BodyHandler.create());
		stuRouter.post("/api/v1/student/").handler(this::addStudent);
		stuRouter.get("/api/v1/student/").handler(this::getAll);
		stuRouter.get("/api/v1/student/:id").handler(this::getStudentById);
		stuRouter.put("/api/v1/student/:id").handler(this::updateStudent);
		stuRouter.delete("/api/v1/student/:id").handler(this::deleteStudent);
		server.requestHandler(stuRouter::accept).listen(4545);

	}

	private void addStudent(RoutingContext rc) {
		Student student = rc.getBodyAsJson().mapTo(Student.class);
		studentService.addStudent(student).subscribe(su -> {
			onSuccessResponse(rc, 201, su);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void getAll(RoutingContext rc) {
		studentService.getAllStudent().subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onSuccessResponse(rc, 404, error);
		});
	}

	private void getStudentById(RoutingContext rc) {
		String studentId = rc.request().getParam("clazzId");
		studentService.getStudentById(studentId).subscribe(success -> {
			onSuccessResponse(rc, 200, success);
		}, error -> {
			onErrorResponse(rc, 404, error);
		});
	}

	private void updateStudent(RoutingContext rc) {
		String studentId = rc.request().getParam("clazzId");
		Student student = mapRequestBodyToStudent(rc);
		studentService.updateStudent(studentId, student).subscribe(success -> {
			onSuccessResponse(rc, 201, success);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private void deleteStudent(RoutingContext rc) {
		String studentId = rc.request().getParam("clazzId");
		studentService.deleteStudent(studentId).subscribe(success -> {
			onSuccessResponse(rc, 204, null);
		}, error -> {
			onErrorResponse(rc, 400, error);
		});
	}

	private Student mapRequestBodyToStudent(RoutingContext rc) {
		Student student = new Student();

		try {
			student = rc.getBodyAsJson().mapTo(Student.class);
		} catch (IllegalArgumentException ex) {
			onErrorResponse(rc, 400, ex);
		}

		return student;
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
