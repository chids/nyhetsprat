package spoken.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@Produces(APPLICATION_XML)
@Consumes(APPLICATION_XML)
@Path("callback")
public class CallbackResource {

    private final AccountDatabase accounts;
    private final ReadHistory history;
    private final EmailSender email;
    private final Twitter twitter;

    public CallbackResource(final AccountDatabase accounts,
                            final ReadHistory history,
                            final EmailSender email,
                            final Twitter twitter) {
        this.accounts = accounts;
        this.history = history;
        this.email = email;
        this.twitter = twitter;
    }

    @GET
    public Response callback(@QueryParam("From") final String from) throws TwitterException {
        final Optional<String> user = this.accounts.isRegistred(from);
        if(user.isPresent()) {
            final Collection<String> recentUrls = this.history.recentUrls(from);
            this.history.clearRecentURls(from);
            if(isTwitterHandle(user.get())) {
                for(final List<String> segments : Lists.partition(new ArrayList<>(recentUrls), 2)) {
                    this.twitter.updateStatus(user.get().concat(" ").concat(Joiner.on(' ').join(segments)));
                }
            }
            if(isEmail(user.get())) {
                this.email.send(user.get(),
                        "Hej!\n\nDu lyssnade nyligen på:\n\n"
                                + Joiner.on('\n').join(recentUrls)
                                + "\n\n Mvh,\nhttp://nyhetspr.at | @nyhetsprat | 040-668 80 44");
            }
        }
        return Response.ok().build();
    }

    private static boolean isTwitterHandle(final String something) {
        return something.startsWith("@");
    }

    private static boolean isEmail(final String something) {
        return !isTwitterHandle(something) && something.contains("@");
    }

}
