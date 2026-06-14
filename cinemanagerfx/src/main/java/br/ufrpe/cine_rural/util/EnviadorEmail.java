package br.ufrpe.cine_rural.util;
import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.ArrayList;
import java.util.Properties;

    public class EnviadorEmail {

        private static final String EMAIL = "cinemanagerufrpe@gmail.com";
        private static final String SENHA = "mhinzciptbgfripq";

        public static void enviarEmail(

                String destinatario,
                String assunto,
                String mensagem
        ) throws MessagingException {
            System.out.println("DESTINATARIO = " + destinatario);

            Properties props = new Properties();

            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(
                    props,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    EMAIL,
                                    SENHA
                            );
                        }
                    }
            );

            Message message = new MimeMessage(session);


            try {

                message.setFrom(
                        new InternetAddress(
                                EMAIL,
                                "Cine Rural"
                        )
                );

            } catch (java.io.UnsupportedEncodingException e) {

                message.setFrom(
                        new InternetAddress(EMAIL)
                );
            }

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );

            message.setSubject(assunto);

            String html =
                    "<html>" +
                            "<body>" +
                            "<h2>🎬 Cine Rural</h2>" +
                            "<p>" + mensagem.replace("\n", "<br>") + "</p>" +
                            "<hr>" +
                            "<p><b>Equipe Cine Rural</b></p>" +
                            "</body>" +
                            "</html>";

            message.setContent(
                    html,
                    "text/html; charset=UTF-8"
            );

            System.out.println("ANTES DO SEND");

            Transport.send(message);
            System.out.println("DEPOIS DO SEND");
        }

        public static void enviarConfirmacaoCompraProduto(
                Cliente cliente,
                ArrayList<ItemVenda> itens,
                double total
        ) throws MessagingException {

            StringBuilder mensagem = new StringBuilder();

            mensagem.append("Olá ")
                    .append(cliente.getNome())
                    .append("!\n\n");

            mensagem.append("Sua compra foi confirmada com sucesso.\n\n");

            mensagem.append("Itens comprados:\n");

            for (ItemVenda item : itens) {

                mensagem.append("- ")
                        .append(item.getProduto().getNome())
                        .append(" x")
                        .append(item.getQuantidade())
                        .append(" = R$ ")
                        .append(String.format("%.2f", item.getSubtotal()))
                        .append("\n");
            }

            mensagem.append("\n");

            mensagem.append("Total pago: R$ ")
                    .append(String.format("%.2f", total))
                    .append("\n\n");

            mensagem.append("Obrigado por comprar no Cine Rural!");

            enviarEmail(
                    cliente.getEmail(),
                    "Compra confirmada - Cine Rural",
                    mensagem.toString()
            );
        }

        public static void enviarConfirmacaoIngresso(
                Cliente cliente,
                ArrayList<Ingresso> ingressos,
                double total
        ) throws MessagingException {

            StringBuilder mensagem = new StringBuilder();

            mensagem.append("Olá ")
                    .append(cliente.getNome())
                    .append("!\n\n");

            mensagem.append("Sua compra foi confirmada.\n\n");

            mensagem.append("Ingressos:\n");

            for (Ingresso ingresso : ingressos) {

                mensagem.append("Assento ")
                        .append(ingresso.getAssento().getCodigo())
                        .append(" - R$ ")
                        .append(String.format("%.2f", ingresso.getPreco()))
                        .append("\n");
            }

            mensagem.append("\nTotal pago: R$ ")
                    .append(String.format("%.2f", total));

            enviarEmail(
                    cliente.getEmail(),
                    "Ingresso confirmado - Cine Rural",
                    mensagem.toString()
            );
        }
    }
