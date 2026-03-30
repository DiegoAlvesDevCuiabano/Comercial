package com.controle_comercial.service;

import com.controle_comercial.model.entity.Orcamento;
import com.controle_comercial.util.RelatorioGenerator;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${mail.from:noreply@unisenai.com}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarOrcamentoPorEmail(Orcamento orcamento) {
        String destinatario = orcamento.getCliente().getEmail();

        if (destinatario == null || destinatario.isBlank()) {
            throw new IllegalArgumentException("Cliente não possui email cadastrado");
        }

        try {
            ByteArrayInputStream pdfStream = RelatorioGenerator.gerarPdfOrcamento(orcamento);
            byte[] pdfBytes = pdfStream.readAllBytes();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(destinatario);
            helper.setSubject("Proposta Comercial - " + orcamento.getNumeroOrcamento());
            helper.setText(
                    "Prezado(a) " + orcamento.getCliente().getNome() + ",\n\n" +
                    "Segue em anexo a proposta comercial " + orcamento.getNumeroOrcamento() + ".\n\n" +
                    "Qualquer dúvida, estamos à disposição.\n\n" +
                    "Atenciosamente,\n" +
                    "UniSENAI - Sistema de Controle Comercial"
            );

            helper.addAttachment(
                    orcamento.getNumeroOrcamento() + ".pdf",
                    new ByteArrayResource(pdfBytes),
                    "application/pdf"
            );

            mailSender.send(message);
            logger.info("Email enviado para {} - Orçamento {}", destinatario, orcamento.getNumeroOrcamento());

        } catch (Exception e) {
            logger.error("Erro ao enviar email para {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("Erro ao enviar email: " + e.getMessage(), e);
        }
    }
}
