package spoken.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoken.models.Source;

import com.google.common.collect.ImmutableList;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.verbs.Hangup;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLResponse;
import com.twilio.sdk.verbs.Verb;

@Produces(APPLICATION_XML)
@Consumes(APPLICATION_XML)
@Path("/")
public class SpokenResource {

    private static final Logger LOG = LoggerFactory.getLogger(SpokenResource.class);
    private final Collection<Source> sources = ImmutableList.of(
            new Source("Svenska Dagbladet", "http://www.svd.se/?service=rss"),
            new Source("Dagens Nyheter", "http://www.dn.se/nyheter/m/rss/"),
            new Source("Expressen", "http://www.expressen.se/Pages/OutboundFeedsPage.aspx?id=3642159&viewstyle=rss")
            );
    private final AccountDatabase accounts;
    private final ReadHistory history;

    public SpokenResource(final AccountDatabase accounts, final ReadHistory history) {
        this.accounts = accounts;
        this.history = history;
    }

    @GET
    public Response answer(@QueryParam("From") final String from) throws Exception {
        final TwiMLResponse twiml = new TwiMLResponse();
        LOG.info("Incoming call from " + from);
        twiml.append(greet(from));
        for(final Source source : this.sources) {
            source.say(twiml, from, this.history);
        }
        twiml.append(new Say("kay thanks bye"));
        twiml.append(new Hangup());
        return Response.ok(twiml.toXML()).build();
    }

    private Verb greet(final String number) throws TwilioRestException {
        if(!this.accounts.isRegistred(number).isPresent()) {
            this.accounts.register(number, "-");
            this.accounts.sendWelcomeSms(number);

        }
        return swedish("Välkommen tillbaka");
    }

    public static Say swedish(final String message) {
        final Say say = new Say(message);
        say.setLanguage("sv-SE");
        return say;
    }
}
