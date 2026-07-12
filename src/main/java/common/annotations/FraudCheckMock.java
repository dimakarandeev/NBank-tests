package common.annotations;

import api.models.modelTransferUserDeposit.TransferFraudDecision;
import api.models.modelTransferUserDeposit.TransferFraudReason;
import api.models.modelTransferUserDeposit.TransferStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface FraudCheckMock {

    /**
     * The fraud check status to return
     */
    TransferStatus status() default TransferStatus.SUCCESS;

    /**
     * The fraud check decision
     */
    TransferFraudDecision decision() default TransferFraudDecision.APPROVED;

    /**
     * The risk score (0.0 to 1.0)
     */
    double riskScore() default 0.2;

    /**
     * The reason for the fraud check result
     */
    TransferFraudReason reason() default TransferFraudReason.LOW_RISK;

    /**
     * Whether manual review is required
     */
    boolean requiresManualReview() default false;

    /**
     * Whether additional verification is required
     */
    boolean additionalVerificationRequired() default false;

    /**
     * The WireMock port to use
     */
    int port() default 8080;

    /**
     * The endpoint path to mock
     */
    String endpoint() default "/fraud-check";

    /**
     * HTTP status code to return in the mock response
     */
    int httpStatus() default 200;

    /**
     * Content-Type header value for the mock response
     */
    String contentType() default "application/json";
}