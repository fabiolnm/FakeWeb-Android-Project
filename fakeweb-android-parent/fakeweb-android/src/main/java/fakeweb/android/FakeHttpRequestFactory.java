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

	public ClientHttpRequest createRequest(final URI uri, final HttpMethod method) throws IOException {
		waitFakeResponseExpectation();
		ClientHttpRequest request = new FakeHttpRequest(uri, method, fakeResponse);
		fakeResponse = null;
		return request;
	}

	private void waitFakeResponseExpectation() {
		int count = 0;
		while(fakeResponse == null) {
			if (++count <= timeoutInSeconds)
				sleep();
			else break;
		}
		if (fakeResponse == null)
			throw new AssertionError("Missing faked responses?");
	}

	public void setTimeoutInSeconds(final int timeoutInSeconds) {
		this.timeoutInSeconds = timeoutInSeconds;
	}

	public void setFakeResponse(final FakeHttpResponse fakeResponse) {
		this.fakeResponse = fakeResponse;
	}

	public void waitFakeResponseConsumed(final FakeHttpResponse fakeResponse, final int consumeTimeout) {
		setFakeResponse(fakeResponse);
		int count = 0;
		while(fakeResponse.getRequest() == null) {
			if (++count <= consumeTimeout)
				sleep();
			else throw new AssertionError("Fake response not consumed");
		}
	}

	private void sleep() {
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			Log.e(FakeHttpRequestFactory.class.getName(), "sleep error", e);
		}
	}
}