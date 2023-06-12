package com.technology.technologysoftware.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        log.debug("Intercept request for logging");

        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);

        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
//        log.info("""
//
//                        =========================== [Request Begin] ===========================
//                        URI         : {}
//                        Method      : {}
//                        Headers     : {}
//                        Request body: {}
//                        ===========================  [Request End]  ===========================""",
//                request.getURI(), request.getMethod(), request.getHeaders(), new String(body, StandardCharsets.UTF_8));
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().is2xxSuccessful()) {
            StringBuilder inputStringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
            bufferedReader.close();

//            log.info("""
//
//                            =========================== [Response Begin] ===========================
//                            Status code  : {}
//                            Status text  : {}
//                            Headers      : {}
//                            Response body: {}
//                            ===========================  [Response End]  ===========================""",
//                    response.getStatusCode(), response.getStatusText(), response.getHeaders(), inputStringBuilder);

        }
    }
}
