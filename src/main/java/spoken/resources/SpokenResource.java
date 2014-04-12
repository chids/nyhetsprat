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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.twilio.sdk.verbs.Hangup;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

@Produces(APPLICATION_XML)
@Consumes(APPLICATION_XML)
@Path("/")
public class SpokenResource {

    private static final Logger LOG = LoggerFactory.getLogger(SpokenResource.class);
    private Collection<Source> sources = ImmutableList.of(
            new Source("Svenska Dagbladet", "http://www.svd.se/?service=rss"),
            new Source("Dagens Nyheter", "http://www.dn.se/nyheter/m/rss/"),
            new Source("Expressen", "http://www.expressen.se/Pages/OutboundFeedsPage.aspx?id=3642159&viewstyle=rss")
            );

    @GET
    public Response answer(@QueryParam("From") final Optional<String> from) throws TwiMLException {
        final TwiMLResponse twiml = new TwiMLResponse();
        LOG.info("Incoming call from " + from.or("unknown"));
        twiml.append(new Say("LOL"));
        for(final Source source : this.sources) {
            source.say(twiml);
        }
        twiml.append(new Say("kay thanks bye"));
        twiml.append(new Hangup());
        return Response.ok(twiml.toXML()).build();
    }

}
