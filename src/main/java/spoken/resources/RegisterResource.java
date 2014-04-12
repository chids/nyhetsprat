package spoken.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static javax.ws.rs.core.MediaType.TEXT_HTML;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

@Path("register")
@Produces(TEXT_HTML)
public class RegisterResource {

    private final AccountDatabase accounts;

    public RegisterResource(final AccountDatabase accounts) {
        this.accounts = accounts;
    }

    @POST
    public Response register(final MultivaluedMap<String, String> form) {
        checkArgument(form.containsKey("From"), "No number");
        checkArgument(form.containsKey("Body"), "No content");
        checkNotNull(emptyToNull(form.getFirst("From")), "Empty number");
        checkNotNull(emptyToNull(form.getFirst("Body")), "Empty content");
        final String number = form.getFirst("From");
        final String address = form.getFirst("Body");
        this.accounts.register(number, address);
        return Response.ok().build();
    }
}
