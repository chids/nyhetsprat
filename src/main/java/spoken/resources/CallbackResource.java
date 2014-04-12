package spoken.resources;

import static javax.ws.rs.core.MediaType.APPLICATION_XML;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.google.common.base.Optional;

@Produces(APPLICATION_XML)
@Consumes(APPLICATION_XML)
@Path("callback")
public class CallbackResource {

    private final AccountDatabase accounts;
    private final ReadHistory history;
    private final EmailSender email;

    public CallbackResource(final AccountDatabase accounts, final ReadHistory history, final EmailSender email) {
        this.accounts = accounts;
        this.history = history;
        this.email = email;
    }

    @GET
    public Response callback(@QueryParam("From") final String from) {
        final Optional<String> user = this.accounts.isRegistred(from);
        if(user.isPresent()) {
            final Collection<String> recentUrls = this.history.recentUrls(from);
            if(isTwitterHandle(user.get())) {
                // yolo
            }
            if(isEmail(user.get())) {
                this.email.send(user.get(),
                        "Hej!\n\nDu lyssnade nyligen på:\n\n"
                                + formatUrls(recentUrls)
                                + "\n\n Mvh, nyhetspr.at");
            }
        }
        return Response.ok().build();
    }

    private static String formatUrls(final Collection<String> urls) {
        final StringBuilder sb = new StringBuilder();
        for(final String url : urls) {
            sb.append(url);
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    private static boolean isTwitterHandle(final String something) {
        return something.startsWith("@");
    }

    private static boolean isEmail(final String something) {
        return !something.startsWith("@") && something.contains("@");
    }

}
