import javax.servlet.ServletException;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;

/**
 * This class contains the main method to initiate Undertow Servlet. Specify
 * path such as "/q1" or "/q2" in the main method, as well as the corresponding
 * servlet class.
 */
public class Main {

	public static final String PATH = "/";

	public static void main(final String[] args) {
		try {
			DeploymentInfo servletBuilder = Servlets.deployment().setClassLoader(Main.class.getClassLoader())
					.setContextPath(PATH).setDeploymentName("handler.war")
					.addServlets(Servlets.servlet("Q1Servlet", Q1Servlet.class).addMapping("/q1"),
							Servlets.servlet("Q2Servlet", Q2MySQLServletPhase2V2.class).addMapping("/q2"),
							Servlets.servlet("Q3Servlet", Q3MySQLServlet.class).addMapping("/q3"),
							Servlets.servlet("Q4Servlet", Q4Servlet2.class).addMapping("/q4"));

			DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
			manager.deploy();

			HttpHandler servletHandler = manager.start();
			PathHandler path = Handlers.path(Handlers.redirect(PATH)).addPrefixPath(PATH, servletHandler);

			Undertow server = Undertow.builder().addHttpListener(8080, "0.0.0.0").setHandler(path).build();
			server.start();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}