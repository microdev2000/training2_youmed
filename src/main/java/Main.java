import io.vertx.core.Vertx;
import youmed.api.router.ClazzRouter;
import youmed.api.service.ClazzService;

public class Main {

	public static void main(String[] args) {
		Vertx vertx = Vertx.factory.vertx();
		vertx.deployVerticle(ClazzRouter.class.getName());
		vertx.deployVerticle(ClazzService.class.getName());

	}

}
