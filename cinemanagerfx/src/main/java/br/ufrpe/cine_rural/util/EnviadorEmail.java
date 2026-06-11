package br.ufrpe.cine_rural.util;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

    public class EnviadorEmail {

        private static final String EMAIL = "juliaellen20203ju@gmail.com";
        private static final String SENHA = "zaqbrfzunisjldws";

        public static void enviarEmail(
                String destinatario,
                String assunto,
                String mensagem
        ) throws MessagingException {

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

            message.setFrom(
                    new InternetAddress(EMAIL)
            );

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(destinatario)
            );

            message.setSubject(assunto);

            message.setText(mensagem);

            Transport.send(message);
        }
    }
