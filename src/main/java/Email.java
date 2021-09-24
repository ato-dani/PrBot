import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.Set;

public class Email {
    public String smtpHost;
    public int smtpPort;
    public boolean useTls;
    public String emailFrom;
    public Set<String> emailTo;
    //todo: something proper with username/password
    private String emailUsername = "NULL";
    private String emailPassword = "NULL";

    private Properties properties = null;
    private Session session = null;

    public Email(String smtpHost, int smtpPort, boolean useTls, String emailFrom, Set<String> emailTo){
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.useTls = useTls;
        this.emailFrom = emailFrom;
        this.emailTo = emailTo;
    }

    public Email(){}

    public boolean sendEmail(String subject, String text){
        configure();
        try{
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));
            for(String s : emailTo){
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
            }
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private void configure(){
        properties = new Properties();
        properties.put("mail.smtp.auth", true);
        //todo: allow use without tls nor ssl
        if(useTls){
            properties.put("mail.smtp.starttls.enable", "true");
        }
        else{
            properties.put("mail.smtp.ssl.enable", "true");
        }
        if(smtpHost != null && !smtpHost.equals("")){
            properties.put("mail.smtp.host", smtpHost);
        }
        if(smtpPort > 0){
            properties.put("mail.smtp.port", String.valueOf(smtpPort));
        }
        session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });
    }
}
