import io.vertx.core.Vertx;
import vn.youmed.model.Limit;
import vn.youmed.model.Speciality;
import vn.youmed.model.Student;
import vn.youmed.router.ClazzRouter;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.factory.vertx();
		vertx.deployVerticle(ClazzRouter.class.getName());
		vertx.deployVerticle(Student.class.getName());
		vertx.deployVerticle(Speciality.class.getName());
		vertx.deployVerticle(Limit.class.getName());
	}

}
