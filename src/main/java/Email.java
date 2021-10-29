import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//mostly based on this wonderful tutorial: https://www.baeldung.com/java-email
public class Email {
    public String smtpHost;
    public int smtpPort;
    public boolean useTls;
    public String emailFrom;
    public Set<String> emailTo;
    //todo: something proper with username/password
    private final String emailUsername = "NULL";
    private final String emailPassword = "NULL";

    private Session session = null;

    public Email(String smtpHost, int smtpPort, boolean useTls, String emailFrom, Set<String> emailTo){
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.useTls = useTls;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;
        configure();
    }

    public Email(){}

    public boolean sendEmail(String subject, String text) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailFrom));
        for(String to : emailTo){
            try{
                message.addRecipient(Message.RecipientType.TO, InternetAddress.parse(to)[0]);
            }
            catch(AddressException e){
                //do nothing for now
            }
        }
        message.setSubject(subject);
        MimeBodyPart body = new MimeBodyPart();
        body.setContent(text, "text/html");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(body);
        message.setContent(multipart);
        Transport.send(message);
        return true;
    }

    private void configure(){
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", useTls);
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.ssl.trust", smtpHost);
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });
    }

    /**
     * Check if all emails are valid, according to the RFC 5322 spec
     * @param emails    String of comma-separated emails
     * @return          true if all emails are valid
     */
    public static boolean validateEmails(String emails) {
        String[] emailArray = emails.replaceAll(" ", "").split(",");
        //taken from RFC 5322, regex for email validation.
        //see https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression
        String regex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b" +
                "\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0" +
                "-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9]" +
                "[0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[" +
                "\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        Pattern pattern = Pattern.compile(regex);
        Matcher m;
        for (String email : emailArray) {
            m = pattern.matcher(email);
            if(!m.matches()){
                return false;
            }
        }
        return true;
    }
}
