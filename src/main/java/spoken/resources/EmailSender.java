package spoken.resources;

import java.util.Collection;

import com.github.sendgrid.SendGrid;
import com.google.common.base.Joiner;

public class EmailSender {

    private final String user;
    private final String passwd;

    public EmailSender(final String user, final String passwd) {
        this.user = user;
        this.passwd = passwd;
    }

    public void send(final String to, final Collection<String> recentUrls) {
        send(to, "Hej!\n\nDu lyssnade nyligen på:\n\n"
                + Joiner.on('\n').join(recentUrls)
                + "\n\n Mvh,\nhttp://nyhetspr.at | @nyhetsprat | 040-668 80 44");
    }

    private void send(final String to, final String body) {
        final SendGrid sendGrid = new SendGrid(this.user, this.passwd);
        sendGrid.addTo(to);
        sendGrid.setText(body);
        sendGrid.setFrom("dont-reply@nyhetspr.at");
        sendGrid.setSubject("Dina upplästa nyheter");
        sendGrid.send();
    }
}
