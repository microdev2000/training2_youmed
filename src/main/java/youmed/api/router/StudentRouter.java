package youmed.api.router;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import youmed.api.model.Student;
import youmed.api.service.LimitService;
import youmed.api.service.StudentService;

public class StudentRouter extends AbstractVerticle {

	private StudentService studentService;
	private LimitService limitService;

	@Override
	public void start() {
		studentService = new StudentService();
		limitService = new LimitService();
		HttpServer server = vertx.createHttpServer();
		Router classRouter = Router.router(vertx);
		classRouter.post("/student/add").handler(this::addStudent);
		classRouter.get("/student/get/all").handler(this::getAll);
		classRouter.get("/student/get/:studentId").handler(this::getStudentById);
		classRouter.put("/student/update/:studentId").handler(this::updateStudent);
		classRouter.delete("/student/delete/:studentId").handler(this::deleteStudent);
		server.requestHandler(classRouter::accept).listen(4545);

	}

	private void addStudent(RoutingContext rc) {
		Student student = rc.getBodyAsJson().mapTo(Student.class);
		limitService.checkLimit(student.getClazz().getId()).subscribe(success -> {
			studentService.addStudent(student).subscribe(su -> {
				onSuccessResponse(rc, 201, su);
			}, error -> {
				onErrorResponse(rc, 400, error);
			});
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
		String jsonPayLoad = rc.getBodyAsString();
		studentService.updateStudent(studentId, jsonPayLoad).subscribe(success -> {
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
