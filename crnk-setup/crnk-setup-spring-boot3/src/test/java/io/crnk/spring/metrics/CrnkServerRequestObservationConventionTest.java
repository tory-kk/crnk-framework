package io.crnk.spring.metrics;

import io.crnk.core.boot.CrnkBoot;
import io.crnk.spring.setup.boot.monitor.CrnkServerRequestObservationConvention;
import io.crnk.test.mock.TestModule;
import io.micrometer.common.KeyValue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class CrnkServerRequestObservationConventionTest {

	private CrnkBoot boot;

	private CrnkServerRequestObservationConvention crnkServerRequestObservationConvention;

	@Before
	public void setup() {
		boot = new CrnkBoot();
		boot.addModule(new TestModule());
		boot.boot();
		crnkServerRequestObservationConvention = new CrnkServerRequestObservationConvention(boot);
	}

	@Test
	public void useFallbackIfNotCrnkResource() {
		ServerRequestObservationContext context = new ServerRequestObservationContext(new MockHttpServletRequest(), new MockHttpServletResponse());
		context.setPathPattern("/any");

		Iterable<KeyValue> keyValues = crnkServerRequestObservationConvention.getLowCardinalityKeyValues(context);
		assertEquals("/any", getUriTag(keyValues));
	}

	@SuppressWarnings("unused")
	private Object[] handleCrnkResourceParameters() {
		String id = "124";

		return new Object[]{
				new Object[]{
						"/tasks",
						"/tasks"
				},
				new Object[]{
						"/tasks/" + id,
						"/tasks/{id}"
				},
				new Object[]{
						"/tasks/" + id + "/name",
						"/tasks/{id}/name"
				},
				new Object[]{
						"/tasks/" + id + "/relationships/project",
						"/tasks/{id}/relationships/project"
				}
		};
	}

	@Test
	@Parameters(method = "handleCrnkResourceParameters")
	public void handleCrnkResource(final String requestUrl, final String expected) {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.setRequestURI(requestUrl);

		ServerRequestObservationContext context = new ServerRequestObservationContext(request, new MockHttpServletResponse());

		Iterable<KeyValue> keyValues = crnkServerRequestObservationConvention.getLowCardinalityKeyValues(context);

		assertEquals(expected, getUriTag(keyValues));
	}

	private String getUriTag(Iterable<KeyValue> keyValues) {
		for (KeyValue keyValue : keyValues) {
			if (keyValue.getKey().equals("uri")) {
				return keyValue.getValue();
			}
		}
		throw new IllegalStateException();
	}
}
