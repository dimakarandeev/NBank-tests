package api.mockTransfer;

import api.BaseTest;
import api.models.CreateUserRequest;
import api.models.TransferResponse;
import api.models.comparison.ModelAssertions;
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
    }

    @AfterEach
    public void afterTest() {
        super.afterTest();
    }

    @Test
    @FraudCheckMock()
    public void testTransferWithFraudCheck() {
        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.2)
                .fraudReason("Low risk transaction")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            decision = "BLOCKED",
            riskScore = 0.9,
            reason = "Transaction blocked by fraud policy"
    )
    public void testTransferBlocked() {
        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("BLOCKED")
                .message("Transfer approved and processed immediately")
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.9)
                .fraudReason("Transaction blocked by fraud policy")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            decision = "REVIEW_REQUIRED",
            riskScore = 0.6,
            reason = "Unusual transaction pattern"
    )
    public void testTransferRequiresManualReviewByDecision() {
        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("MANUAL_REVIEW_REQUIRED")
                .message("Transfer requires manual review")
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.6)
                .fraudReason("Unusual transaction pattern")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            riskScore = 0.5,
            reason = "Manual review required",
            requiresManualReview = true
    )
    public void testTransferRequiresManualReviewByFlag() {
        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("MANUAL_REVIEW_REQUIRED")
                .message("Transfer requires manual review")
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.5)
                .fraudReason("Manual review required")
                .requiresManualReview(true)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();

    }

    @Test
    @FraudCheckMock(
            decision = "VERIFICATION_REQUIRED",
            riskScore = 0.7,
            reason = "Additional verification needed"
    )
    public void testTransferRequiresVerificationByDecision() {
        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("VERIFICATION_REQUIRED")
                .message("Additional verification required")
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.7)
                .fraudReason("Additional verification needed")
                .requiresManualReview(false)
                .requiresVerification(false)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }

    @Test
    @FraudCheckMock(
            riskScore = 0.4,
            reason = "Verification required",
            additionalVerificationRequired = true
    )
    public void testTransferRequiresVerificationByFlag() {
        softly.assertThat(transferResponse).isNotNull();

        TransferResponse expectedResponse = TransferResponse.builder()
                .status("APPROVED")
                .message("Transfer approved and processed immediately")
                .amount(transferAmount)
                .senderAccountId(senderAccountId)
                .receiverAccountId(receiverAccountId)
                .fraudRiskScore(0.4)
                .fraudReason("Verification required")
                .requiresManualReview(false)
                .requiresVerification(true)
                .build();

        ModelAssertions.assertThatModels(expectedResponse, transferResponse).match();
    }
}