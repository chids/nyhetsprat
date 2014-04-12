package spoken;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import spoken.resources.RegisterResource;
import spoken.resources.SpokenResource;

public class SpokenService extends Application<Configuration> {

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
    }

    @Override
    public void run(final Configuration config, final Environment env) throws Exception {
        env.jersey().register(SpokenResource.class);
        env.jersey().register(RegisterResource.class);
    }

    public static void main(final String[] args) throws Exception {
        new SpokenService().run(args);
    }
}
