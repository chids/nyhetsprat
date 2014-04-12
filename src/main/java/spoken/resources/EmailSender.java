package spoken.resources;

import com.github.sendgrid.SendGrid;

public class EmailSender {

    private final String user;
    private final String passwd;

    public EmailSender(final String user, final String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public void send(final String to, final String body) {
        final SendGrid sendGrid = new SendGrid(this.user, this.passwd);
        sendGrid.addTo(to);
        sendGrid.setText(body);
        sendGrid.setFrom("dont-reply@nyhetspr.at");
        sendGrid.setSubject("Dina upplästa nyheter");
        sendGrid.send();
    }
}
