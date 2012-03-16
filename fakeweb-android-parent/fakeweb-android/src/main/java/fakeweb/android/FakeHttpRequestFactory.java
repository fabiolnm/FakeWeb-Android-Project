package fakeweb.android;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import android.util.Log;

public class FakeHttpRequestFactory implements ClientHttpRequestFactory, Serializable {
	private static final long serialVersionUID = 7113900116927260776L;

	private int timeoutInSeconds = 3;
	private FakeHttpResponse fakeResponse;

	private boolean stopped;

	public ClientHttpRequest createRequest(final URI uri, final HttpMethod method) throws IOException {
		waitFakeResponseExpectation(method, uri);
		return new FakeHttpRequest(uri, method, fakeResponse);
	}

	private void waitFakeResponseExpectation(HttpMethod method, URI uri) {
		int count = 0;
		while(fakeResponse == null || fakeResponse.wasConsumed()) {
			if (stopped || ++count > timeoutInSeconds)
				break;
			sleep();
		}
		if (!stopped) {
			if (fakeResponse == null)
				throw new AssertionError(String.format("%s %s: Missing faked responses? (count: %s)", method, uri, count));
			else if (fakeResponse.wasConsumed())
				throw new AssertionError(String.format("%s %s: Current faked response was already consumed", method, uri));
		}
	}

	public void setTimeoutInSeconds(final int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
	}

	public void setFakeResponse(final FakeHttpResponse fakeResponse) {
		this.fakeResponse = fakeResponse;
	}

	public void waitFakeResponseConsumed(final int consumeTimeout) {
		int count = 0;
		while(fakeResponse == null || !fakeResponse.wasConsumed()) {
			if (stopped || ++count > consumeTimeout)
				break;
			sleep();
		}
		if (!stopped) {
			if (fakeResponse == null)
				throw new AssertionError("Fake response is null");
			else if (!fakeResponse.wasConsumed())
				throw new AssertionError("Fake response not consumed");
		}
	}

	public void waitFakeResponseConsumed(final FakeHttpResponse fakeResponse, final int consumeTimeout) {
		setFakeResponse(fakeResponse);
		waitFakeResponseConsumed(consumeTimeout);
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Log.e(FakeHttpRequestFactory.class.getName(), "sleep error", e);
		}
	}

	public void stop() {
		stopped = true;
	}
}