package spoken.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.System.getenv;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import spoken.JedisUtil;
import spoken.JedisUtil.NonTx;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;

@Path("register")
@Produces(TEXT_HTML)
public class RegisterResource {

    private final JedisUtil redis;
    private final TwilioRestClient sms;

    public RegisterResource(final JedisUtil redis) {
        this.sms = new TwilioRestClient(getenv("TWILIO_SID"), getenv("TWILIO_TOKEN"));
        this.redis = redis;
    }

    @GET
    public Response setup(@QueryParam("From") final String number) throws TwilioRestException {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", checkNotNull(emptyToNull(number), "No number")));
        params.add(new BasicNameValuePair("From", "+46769439470"));
        params.add(new BasicNameValuePair("Body",
                "Hej! \nSvara på det här meddelandet med din e-postadress eller ditt Twitter-namn för att registrera dig"));
        this.sms.getAccount().getMessageFactory().create(params);
        final URI start = UriBuilder
                .fromResource(SpokenResource.class)
                .queryParam("From", number)
                .build();
        return Response.temporaryRedirect(start).build();
    }

    @POST
    public Response register(final MultivaluedMap<String, String> form) {
        checkArgument(form.containsKey("From"), "No number");
        checkArgument(form.containsKey("Body"), "No content");
        checkNotNull(emptyToNull(form.getFirst("From")), "Empty number");
        checkNotNull(emptyToNull(form.getFirst("Body")), "Empty content");
        try(NonTx nonTx = this.redis.nonTx()) {
            nonTx.redis().set(
                    "notify:".concat(form.getFirst("From")),
                    form.getFirst("Body"));
        }
        return Response.ok().build();
    }
}
