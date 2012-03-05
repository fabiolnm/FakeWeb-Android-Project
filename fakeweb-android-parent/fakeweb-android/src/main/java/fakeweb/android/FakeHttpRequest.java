package fakeweb.android;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

public class FakeHttpRequest implements ClientHttpRequest {
	private final URI uri;
	private final HttpMethod method;
	private final HttpHeaders headers;

	public final ByteArrayOutputStream body;
	private final FakeHttpResponse fakeResponse;

	public FakeHttpRequest(final URI uri, final HttpMethod method, final FakeHttpResponse fakeResponse) {
		this.uri = uri;
		this.method = method;
		this.headers = fakeResponse.getHeaders();

		fakeResponse.setRequest(this);
		this.fakeResponse = fakeResponse;
		this.body = new ByteArrayOutputStream();
	}

	public HttpMethod getMethod() {
		return method;
	}
	public URI getURI() {
		return uri;
	}
	public HttpHeaders getHeaders() {
		return headers;
	}
	public OutputStream getBody() throws IOException {
		return body;
	}
	public ClientHttpResponse execute() throws IOException {
		return fakeResponse;
	}
}