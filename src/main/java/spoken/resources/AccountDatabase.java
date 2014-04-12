package spoken.resources;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.System.getenv;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import spoken.JedisUtil;
import spoken.JedisUtil.NonTx;

import com.google.common.base.Optional;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;

public class AccountDatabase {
    private static final String TWILIO_NUMBER = getenv("TWILIO_NUMBER");
    private final JedisUtil redis;
    private final TwilioRestClient twilio;

    public AccountDatabase(final JedisUtil redis, final TwilioRestClient twilio) {
        this.redis = redis;
        this.twilio = twilio;
    }

    public void register(final String number, final String address) {
        try(NonTx nonTx = this.redis.nonTx()) {
            nonTx.redis().set("notify:".concat(number), address);
        }
    }

    public Optional<String> isRegistred(final String number) {
        try(NonTx nonTx = this.redis.nonTx()) {
            return Optional.fromNullable(nonTx.redis().get("notify:".concat(number)));
        }
    }

    public Optional<String> isRegistred(final Optional<String> from) {
        if(from.isPresent()) {
            return isRegistred(from.get());
        }
        return Optional.absent();
    }

    public void sendWelcomeSms(final String number) throws TwilioRestException {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("To", checkNotNull(emptyToNull(number), "No number")));
        params.add(new BasicNameValuePair("From", TWILIO_NUMBER));
        params.add(new BasicNameValuePair("Body",
                "Hej! \n" +
                        "Svara på det här meddelandet med din e-postadress eller ditt Twitter-namn om du vill att vi " +
                        "skickar dig länkarna till de artiklar du lyssnat på"));
        this.twilio.getAccount().getMessageFactory().create(params);
        UriBuilder
                .fromResource(SpokenResource.class)
                .queryParam("From", number)
                .build();

    }
}
