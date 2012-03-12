package fakeweb.android;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

import com.google.gson.Gson;

public class FakeHttpResponse implements ClientHttpResponse {
	private HttpHeaders headers = new HttpHeaders();
	private HttpStatus statusCode = HttpStatus.OK;
	private String statusText = "", responseBody = "";

	private FakeHttpRequest request;
	private ByteArrayInputStream body;
	private final Gson gson;

	public FakeHttpResponse() {
		gson = new Gson();
		setContentType("text/xml");
	}

	public FakeHttpResponse(Gson gson) {
		this.gson = gson;
		setContentType("application/json");
	}

	public HttpHeaders getHeaders() {
		return headers;
	}
	public void setHeader(final String header, final String value) {
		headers.set(header, value);
	}
	public void setContentType(final String value) {
		setHeader("Content-type", value);
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(final HttpStatus statusCode) {
		this.statusCode = statusCode;
	}

	public String getStatusText() {
		return statusText;
	}
	public void setStatusText(final String statusText) {
		this.statusText = statusText;
	}

	public String getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(final String responseBody) {
		this.responseBody = responseBody;
	}
	public void setResponseBodyAsJson(final Object response) {
		setContentType("application/json");
		responseBody = gson.toJson(response);
	}

	public boolean wasConsumed() {
		return request != null;
	}

	public InputStream getBody() throws IOException {
		if (body == null)
			body = new ByteArrayInputStream(responseBody.getBytes());
		return body;
	}

	public FakeHttpRequest getRequest() {
		return request;
	}
	public void setRequest(final FakeHttpRequest request) {
		this.request = request;
	}

	public void close() {
		try {
			request.body.close();
		} catch (Exception e) {
			throw new Error(e);
		}
		try {
			if (body != null)
				body.close();
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}