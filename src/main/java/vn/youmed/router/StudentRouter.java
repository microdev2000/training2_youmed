package vn.youmed.router;

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
import vn.youmed.model.Limit;
import vn.youmed.model.Student;
import vn.youmed.service.LimitService;
import vn.youmed.service.StudentService;

public class StudentRouter extends AbstractVerticle {

	private StudentService studentService;
	private LimitService limitService;

	@Override
	public void start() {
		studentService = new StudentService();
		limitService = new LimitService();
		HttpServer server = vertx.createHttpServer();
		Router stuRouter = Router.router(vertx);
		stuRouter.post("/student/add").handler(this::addStudent);
		stuRouter.get("/student/get/all").handler(this::getAll);
		stuRouter.get("/student/get/:studentId").handler(this::getStudentById);
		stuRouter.put("/student/update/:studentId").handler(this::updateStudent);
		stuRouter.delete("/student/delete/:studentId").handler(this::deleteStudent);
		server.requestHandler(stuRouter::accept).listen(4545);

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
