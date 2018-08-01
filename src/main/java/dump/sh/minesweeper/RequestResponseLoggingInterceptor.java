package dump.sh.minesweeper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;

public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) throws IOException {
        System.out.println("===========================request begin================================================");
        System.out.println(request.getURI());
        System.out.println(request.getMethod());
        System.out.println(request.getHeaders());
        System.out.println(new String(body, "UTF-8"));
        System.out.println("==========================request end================================================");
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        System.out.println("============================response begin==========================================");
        System.out.println(response.getStatusCode());
        System.out.println(response.getStatusText());
        System.out.println(response.getHeaders());
        System.out.println(StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
        System.out.println("=======================response end=================================================");
    }
}