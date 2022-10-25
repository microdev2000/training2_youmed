import io.vertx.core.Vertx;
import vn.youmed.model.Limit;
import vn.youmed.model.Speciality;
import vn.youmed.model.Student;
import vn.youmed.router.ClazzRouter;
import vn.youmed.service.ClazzService;
import vn.youmed.service.StudentService;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.factory.vertx();
		vertx.deployVerticle(ClazzRouter.class.getName());
		vertx.deployVerticle(Student.class.getName());
		vertx.deployVerticle(Speciality.class.getName());
		vertx.deployVerticle(Limit.class.getName());

		vertx.deployVerticle(ClazzService.class.getName());
		vertx.deployVerticle(StudentService.class.getName());
		vertx.deployVerticle(Speciality.class.getName());
		vertx.deployVerticle(Limit.class.getName());

	}

}
