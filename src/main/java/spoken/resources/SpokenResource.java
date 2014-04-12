package spoken.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spoken.models.Source;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.twilio.sdk.verbs.Gather;
import com.twilio.sdk.verbs.Hangup;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
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
    public Response answer(@QueryParam("From") final Optional<String> from) throws TwiMLException {
        final TwiMLResponse twiml = new TwiMLResponse();
        LOG.info("Incoming call from " + from.or("unknown"));
        twiml.append(greet(from));
        for(final Source source : this.sources) {
            source.say(twiml, from.or("unknown"), this.history);
        }
        twiml.append(new Say("kay thanks bye"));
        twiml.append(new Hangup());
        return Response.ok(twiml.toXML()).build();
    }

    private Verb greet(final Optional<String> from) throws TwiMLException {
        if(this.accounts.isRegistred(from).isPresent()) {
            return swedish("Välkommen tillbaka");
        }
        final Gather welcome = new Gather();
        welcome.setNumDigits(1);
        welcome.setMethod("GET");
        welcome.setAction(UriBuilder.fromResource(RegisterResource.class).build().toString());
        welcome.setTimeout(3);
        welcome.append(swedish("Hej, och välkommen till Spoken News"));
        welcome.append(swedish("Tryck en siffra för att registrera dig eller dröj kvar"));
        return welcome;
    }

    public static Say swedish(final String message) {
        final Say say = new Say(message);
        say.setLanguage("sv-SE");
        return say;
    }
}
