package spoken.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

@Produces(APPLICATION_XML)
@Consumes(APPLICATION_XML)
@Path("/")
public class SpokenResource {

    private static final Logger LOG = LoggerFactory.getLogger(SpokenResource.class);

    @GET
    public Response answer(@QueryParam("From") final Optional<String> from) throws TwiMLException {
        final TwiMLResponse twiml = new TwiMLResponse();
        LOG.info("Incoming call from " + from.or("unknown"));
        twiml.append(say("Hallå där :)"));
        return Response.ok(twiml.toXML()).build();
    }

    private static Say say(final String message) {
        final Say say = new Say(message);
        say.setLanguage("sv-SE");
        return say;
    }
}
