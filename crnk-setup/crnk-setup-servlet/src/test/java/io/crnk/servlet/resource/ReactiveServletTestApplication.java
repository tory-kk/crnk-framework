package io.crnk.servlet.resource;

import io.crnk.client.CrnkClient;
import io.crnk.core.boot.CrnkBoot;
import io.crnk.core.module.SimpleModule;
import io.crnk.reactive.ReactiveModule;
import io.crnk.servlet.AsyncCrnkServlet;
import io.crnk.servlet.reactive.model.SlowResourceRepository;
import io.crnk.test.mock.ClientTestModule;
import io.crnk.test.mock.reactive.ReactiveTestModule;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@RestController
@SpringBootApplication
public class ReactiveServletTestApplication implements ApplicationListener<ServletWebServerInitializedEvent> {

	private int port;

	private CrnkClient client;


	private ReactiveTestModule testModule = new ReactiveTestModule();

	@Override
	public void onApplicationEvent(ServletWebServerInitializedEvent event) {
		port = event.getWebServer().getPort();
		client = new CrnkClient("http://localhost:" + port + "/api");
		client.addModule(new ClientTestModule());
	}

	public static void main(String[] args) {
		SpringApplication.run(ReactiveServletTestApplication.class, args);
	}

	@Bean
	public SlowResourceRepository slowRepository() {
		return new SlowResourceRepository();
	}

	@Bean
	public ReactiveServletTestContainer testContainer(CrnkBoot boot) {
		return new ReactiveServletTestContainer(testModule, () -> client, boot);
	}

	@Bean
	public ConfigurableServletWebServerFactory webServerFactory() {
		TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
		factory.addConnectorCustomizers(connector -> {
			// Same as in io.crnk.spring.setup.boot.core.CrnkTomcatAutoConfiguration
            connector.setProperty("relaxedQueryChars", "[]{}");
        });
		return factory;
	}

	// tag::reactive[]
	@Bean
	public AsyncCrnkServlet asyncCrnkServlet(SlowResourceRepository slowResourceRepository) {
		SimpleModule slowModule = new SimpleModule("slow");
		slowModule.addRepository(slowResourceRepository);

		AsyncCrnkServlet servlet = new AsyncCrnkServlet();
		servlet.getBoot().addModule(new ReactiveModule());
		servlet.getBoot().addModule(testModule);
		servlet.getBoot().addModule(slowModule);

		return servlet;
	}

	@Bean
	public ServletRegistrationBean crnkServletRegistration(AsyncCrnkServlet servlet) {
		ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/api/*");
		bean.setLoadOnStartup(1);
		return bean;
	}

	@Bean
	public CrnkBoot crnkBoot(AsyncCrnkServlet servlet) {
		return servlet.getBoot();
	}
	// end::reactive[]

}
