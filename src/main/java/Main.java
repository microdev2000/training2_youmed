import io.vertx.core.Vertx;
import vn.youmed.router.ClazzRouter;
import vn.youmed.router.LimitRouter;
import vn.youmed.router.SpecialityRouter;
import vn.youmed.router.StudentRouter;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.factory.vertx();
		vertx.deployVerticle(ClazzRouter.class.getName());
		vertx.deployVerticle(StudentRouter.class.getName());
		vertx.deployVerticle(SpecialityRouter.class.getName());
		vertx.deployVerticle(LimitRouter.class.getName());
	}

}
