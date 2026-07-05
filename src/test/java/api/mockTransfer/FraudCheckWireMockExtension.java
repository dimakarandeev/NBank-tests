package api.mockTransfer;

import api.models.FraudResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import common.annotations.FraudCheckMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FraudCheckWireMockExtension implements BeforeEachCallback, AfterEachCallback {

    private WireMockServer wireMockServer;
    private int usedPort;

    @Override
    public void beforeEach(ExtensionContext context) {
        // Find the FraudCheckMock annotation on the test method or class
        FraudCheckMock mockConfig = context.getTestMethod()
                .map(method -> method.getAnnotation(FraudCheckMock.class))
                .orElseGet(() -> context.getTestClass()
                        .map(clazz -> clazz.getAnnotation(FraudCheckMock.class))
                        .orElse(null));

        if (mockConfig != null) {
            setupWireMock(mockConfig);
        }
    }

    @SneakyThrows
    private void setupWireMock(FraudCheckMock config) {
        usedPort = config.port();

        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(usedPort));
        wireMockServer.start();
        WireMock.configureFor("0.0.0.0", usedPort);

        // Create the response body based on annotation parameters
        String responseBody = new ObjectMapper().writeValueAsString(new FraudResponse(
                config.status(),
                config.decision(),
                config.riskScore(),
                config.reason(),
                config.requiresManualReview(),
                config.additionalVerificationRequired()));

        // Mock the fraud detection service endpoint
        wireMockServer.stubFor(post(urlPathMatching(config.endpoint()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(responseBody)));
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }

    public String getBaseUrl() {
        if (wireMockServer != null) {
            return "http://host.docker.internal:" + wireMockServer.port();
        }
        return null;
    }
}