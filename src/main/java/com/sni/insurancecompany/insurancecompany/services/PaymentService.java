package com.sni.insurancecompany.insurancecompany.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.sni.insurancecompany.insurancecompany.model.*;
import com.sni.insurancecompany.insurancecompany.model.enums.PaymentType;
import com.sni.insurancecompany.insurancecompany.model.enums.TransactionStatus;
import com.sni.insurancecompany.insurancecompany.repositories.PurchaseRepository;
import com.sni.insurancecompany.insurancecompany.repositories.SessionRepository;
import com.sni.insurancecompany.insurancecompany.repositories.TransactionReposistory;
import com.sni.insurancecompany.insurancecompany.repositories.UserPolicyRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${stripe.api.key}")
    public String stripeApiKey;

    private final PurchaseRepository purchaseRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final SIEMService siemService;
    private final EmailService emailService;

    private final TransactionService transactionService;
    private final TransactionReposistory transactionReposistory;
    public Purchase processPayment(User user, Double amount, String policyName,
                                   String paymentMethodId, Policy policy, String cardNumber) throws Exception {

        Stripe.apiKey = stripeApiKey;


        if ("4242424242424242".equals(cardNumber.replace(" ", ""))) {
            System.out.println("Using Stripe test card");
            siemService.logSecurityEvent("Test card used",
                    "User " + user.getUsername() + " used test card");
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (amount * 100))
                .setCurrency("usd")
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .setReturnUrl("https://your-domain.com/payment-success")
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setAmount(amount);
        purchase.setPolicy(policy);
        purchase.setTransactionId(paymentIntent.getId());
        purchase.setStatus(TransactionStatus.WAITING);
        purchase = purchaseRepository.save(purchase);

        UserPolicy userPolicy = new UserPolicy();
        userPolicy.setPolicy(policy);
        userPolicy.setUser(user);
        userPolicy.setPurchaseDate(LocalDate.now().atStartOfDay());
        userPolicy = userPolicyRepository.save(userPolicy);

        if ("succeeded".equals(paymentIntent.getStatus())) {
            purchase.setStatus(TransactionStatus.SUCCESS);
            purchaseRepository.save(purchase);

            byte[] pdfData = generatePolicyPdf(user, policy);
            sendPolicyEmail(user, policyName, pdfData);

            siemService.logSecurityEvent("Payment success",
                    "User " + user.getUsername() + " successfully purchased " + policyName);
        } else {
            purchase.setStatus(TransactionStatus.ERROR);
            purchaseRepository.save(purchase);
            siemService.logSecurityEvent("Payment failed",
                    "Payment failed for user " + user.getUsername());
        }
        return purchase;
    }

    private byte[] generatePolicyPdf(User user, Policy policy) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("Insurance Policy Document for client", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 12);

            document.add(new Paragraph("Policy Information:", headerFont));
            document.add(new Paragraph("Policy Name: " + policy.getPolicyname(), contentFont));
            document.add(new Paragraph("Policy Type: " + policy.getType(), contentFont));
            document.add(new Paragraph("Price: $" + policy.getPrice(), contentFont));
            document.add(new Paragraph("Description: " + policy.getDescription(), contentFont));
            document.add(new Paragraph("Policy Holder:", headerFont));
            document.add(new Paragraph("Name: " + user.getFirstName() + " " + user.getLastName(), contentFont));
            document.add(new Paragraph("Email: " + user.getEmail(), contentFont));
            document.add(new Paragraph("Purchase Date: " + LocalDate.now(), contentFont));
            document.add(new Paragraph("Dear user, "+user.getFirstName() + " thank you for buying policy insuracne! You will be safe!"));

        } catch (DocumentException e) {
            siemService.logSecurityEvent("PDF Generation Error",
                    "Error generating PDF for user " + user.getUsername());
            throw new Exception("Failed to generate policy PDF");
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }

    private void sendPolicyEmail(User user, String policyName, byte[] pdfAttachment) {
        try {
            String subject = "Your " + policyName + " Policy Documents";
            String body = "Dear " + user.getFirstName() + ",\n\n" +
                    "Thank you for purchasing the " + policyName + " policy. " +
                    "Please find your policy documents attached.\n\n" +
                    "If you have any questions, please contact our support team.\n\n" +
                    "Best regards,\n" +
                    "Insurance System Team";

            emailService.sendEmailWithAttachment(
                    user.getEmail(),
                    subject,
                    body,
                    pdfAttachment,
                    policyName.replaceAll(" ", "_") + "_Policy.pdf"
            );

            siemService.logSecurityEvent("Policy Email Sent",
                    "Sent policy documents to " + user.getEmail());

        } catch (Exception e) {
            siemService.logSecurityEvent("Email Send Error",
                    "Failed to send policy email to " + user.getEmail());
        }
    }
}
