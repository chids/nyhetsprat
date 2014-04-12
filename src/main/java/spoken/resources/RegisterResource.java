package spoken.resources;

import static com.google.common.base.Preconditions.checkArgument;
import static javax.ws.rs.core.MediaType.TEXT_HTML;
import io.dropwizard.views.View;

import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;

@Path("register")
@Produces(TEXT_HTML)
public class RegisterResource {

    @POST
    public DoneView register(final MultivaluedMap<String, String> form) {
        checkArgument(form.containsKey("user"), "No user");
        checkArgument(form.containsKey("twitter"), "No twitter");
        return new DoneView();
    }

    @GET
    public RegisterView load() {
        return new RegisterView();
    }

    public static class DoneView extends View {

        protected DoneView() {
            super("done.mustache");
        }
    }

    public static class RegisterView extends View {

        protected RegisterView() {
            super("register.mustache");
        }

        public String getUser() {
            return UUID.randomUUID().toString();
        }
    }
}
