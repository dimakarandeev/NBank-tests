package api.mockTransfer;

import api.BaseTest;
import api.models.CreateUserRequest;
import api.models.TransferResponse;
import api.models.comparison.ModelAssertions;
import api.models.modelTransferUserDeposit.TransferFraudDecision;
import api.models.modelTransferUserDeposit.TransferFraudReason;
import api.models.modelTransferUserDeposit.TransferMessage;
import api.models.modelTransferUserDeposit.TransferStatus;
import api.requests.steps.AccountSteps;
import api.requests.steps.AdminSteps;
import common.annotations.FraudCheckMock;
import common.extensions.TimingExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({
        TimingExtension.class,
        FraudCheckWireMockExtension.class})
public class TransferWithFraudCheckTest extends BaseTest {

    private Long senderAccountId;
    private Long receiverAccountId;

    private TransferResponse transferResponse;

    private final double depositAmount = Math.random() * 4999.9 + 0.1;
    private final double transferAmount = Math.random() * (depositAmount - 0.1) + 0.1;


    @BeforeEach
    public void setupTest() {
        super.setupTest();

        CreateUserRequest userOne = AdminSteps.createUser();
        CreateUserRequest userTwo = AdminSteps.createUser();

        AccountSteps accountStepsUserOne = new AccountSteps(userOne.getUsername(), userOne.getPassword());
        AccountSteps accountStepsUserTwo = new AccountSteps(userTwo.getUsername(), userTwo.getPassword());

        senderAccountId = accountStepsUserOne.createAccount().getId();
        receiverAccountId = accountStepsUserTwo.createAccount().getId();

        accountStepsUserOne.depositToAccount(senderAccountId, depositAmount);

        transferResponse = accountStepsUserOne.transferWithFraudCheck(
                senderAccountId,
                receiverAccountId,
                transferAmount);

        softly.assertThat(transferResponse).isNotNull();
    }

    @AfterEach
    public void afterTest() {
        super.afterTest();
    }

    @Test
    @FraudCheckMock()
    public void testTransferWithFraudCheck() {
        TransferResponse expectedResponse = TransferResponse.builder()
                .status(TransferStatus.APPROVED.getStatus())
                .message(TransferMessage.APPROVED.getMsg())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.2)
                .fraudReason(TransferFraudReason.LOW_RISK.getFraudReason())
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            decision = TransferFraudDecision.BLOCKED,
            riskScore = 0.9,
            reason = TransferFraudReason.BLOCKED_BY_POLICY
    )
    public void testTransferBlocked() {
        TransferResponse expectedResponse = TransferResponse.builder()
                .status(TransferStatus.BLOCKED.getStatus())
                .message(TransferMessage.APPROVED.getMsg())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.9)
                .fraudReason(TransferFraudReason.BLOCKED_BY_POLICY.getFraudReason())
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            decision = TransferFraudDecision.REVIEW_REQUIRED,
            riskScore = 0.6,
            reason = TransferFraudReason.UNUSUAL_PATTERN
    )
    public void testTransferRequiresManualReviewByDecision() {
        TransferResponse expectedResponse = TransferResponse.builder()
                .status(TransferStatus.MANUAL_REVIEW_REQUIRED.getStatus())
                .message(TransferMessage.MANUAL_REVIEW.getMsg())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.6)
                .fraudReason(TransferFraudReason.UNUSUAL_PATTERN.getFraudReason())
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            riskScore = 0.5,
            reason = TransferFraudReason.MANUAL_REVIEW_REQUIRED,
            requiresManualReview = true
    )
    public void testTransferRequiresManualReviewByFlag() {
        TransferResponse expectedResponse = TransferResponse.builder()
                .status(TransferStatus.MANUAL_REVIEW_REQUIRED.getStatus())
                .message(TransferMessage.MANUAL_REVIEW.getMsg())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.5)
                .fraudReason(TransferFraudReason.MANUAL_REVIEW_REQUIRED.getFraudReason())
                .requiresManualReview(true)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();

    }

    @Test
    @FraudCheckMock(
            decision = TransferFraudDecision.VERIFICATION_REQUIRED,
            riskScore = 0.7,
            reason = TransferFraudReason.ADDITIONAL_VERIFICATION_NEEDED
    )
    public void testTransferRequiresVerificationByDecision() {
        TransferResponse expectedResponse = TransferResponse.builder()
                .status(TransferStatus.VERIFICATION_REQUIRED.getStatus())
                .message(TransferMessage.ADDITIONAL_VERIFICATION.getMsg())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.7)
                .fraudReason(TransferFraudReason.ADDITIONAL_VERIFICATION_NEEDED.getFraudReason())
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            riskScore = 0.4,
            reason = TransferFraudReason.VERIFICATION_REQUIRED,
            additionalVerificationRequired = true
    )
    public void testTransferRequiresVerificationByFlag() {
        TransferResponse expectedResponse = TransferResponse.builder()
                .status(TransferStatus.APPROVED.getStatus())
                .message(TransferMessage.APPROVED.getMsg())
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.4)
                .fraudReason(TransferFraudReason.VERIFICATION_REQUIRED.getFraudReason())
                .requiresManualReview(false)
                .requiresVerification(true)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}