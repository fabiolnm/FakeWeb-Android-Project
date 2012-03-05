package fakeweb.android.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import fakeweb.android.FakeHttpRequestFactory;
import fakeweb.android.FakeHttpResponse;

public class FakeWebTests extends TestCase {
	private FakeHttpRequestFactory factory = new FakeHttpRequestFactory();
	private RestTemplate restTemplate = new RestTemplate(factory);
	private final String dummyUri = "http://dummy.uri";
	private FakeHttpResponse fakeResponse = new FakeHttpResponse();

	@Override
	public void setUp() {
		factory.setFakeResponse(fakeResponse);
	}

	public void testGetResponseBody() {
		fakeResponse.setResponseBody("Teste");

		String response = restTemplate.getForObject(dummyUri, String.class);
		assertEquals(fakeResponse.getResponseBody(), response);
	}

	public void testGetResponseBodyAsJson() {
		String[] testData = new String[] { "teste1", "teste2", "teste2" };
		fakeResponse.setResponseBodyAsJson(testData);

		String[] response = restTemplate.getForObject(dummyUri, String[].class);
		assertEquals(testData.length, response.length);
		for (int i = 0; i < testData.length; i++)
			assertEquals(testData[i], response[i]);
	}

	public void testGetStatusCode() {
		fakeResponse.setStatusCode(HttpStatus.FOUND);

		ResponseEntity<String> response = restTemplate.getForEntity(dummyUri, String.class);
		assertEquals(fakeResponse.getStatusCode(), response.getStatusCode());
	}

	private boolean handledError = false;

	public void testGetStatusText() {
		fakeResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
		fakeResponse.setStatusText("testing error reason");

		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public void handleError(final ClientHttpResponse response) throws IOException {
				assertEquals(fakeResponse.getStatusText(), response.getStatusText());
				handledError = true;
			}
		});
		restTemplate.getForEntity(dummyUri, String.class);
		assertTrue(handledError);
	}

	public void testExpectationTimeout() {
		int timeout = 3;
		long start = System.currentTimeMillis();
		try {
			factory.setFakeResponse(null);
			factory.setTimeoutInSeconds(timeout);
			restTemplate.getForEntity(dummyUri, String.class);
		}
		catch (Throwable e) {
			long duration = (System.currentTimeMillis() - start) / 1000;
			assertTrue("Factory didn't wait for FakeResponse timeout", duration >= timeout);
		}
	}

	public void testWaitConsumeFakeResponse() {
		int timeout = 2;
		executeRestTemplateOnFuture(1);
		factory.waitFakeResponseConsumed(fakeResponse, timeout);
	}

	private void executeRestTemplateOnFuture(final int futureSeconds) {
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(futureSeconds * 1000);
					restTemplate.getForEntity(dummyUri, String.class);
				} catch (Exception e) {
					throw new Error(e);
				}
			}
		}.start();
	}

	public void testNoCallsOnRestTemplateWillNotConsumeFakeResponseAndShouldThrowException() {
		int timeout = 1;
		try {
			factory.waitFakeResponseConsumed(fakeResponse, timeout);
			fail("Consume timeout didn't throw exception");
		} catch (AssertionError e) {
			assertEquals("Fake response not consumed", e.getMessage());
		}
	}

	public void testFakeResponseConsumingTimeout() {
		int timeout = 2;
		long start = System.currentTimeMillis();
		try {
			factory.waitFakeResponseConsumed(fakeResponse, timeout);
		}
		catch (Throwable e) {
			long durationMillis = System.currentTimeMillis() - start, duration = durationMillis / 1000;
			String msg = "Factory didn't wait enough (%dms) for consume timeout (%dms)";
			assertTrue(String.format(msg, durationMillis, timeout * 1000), duration >= timeout);
		}
	}
}