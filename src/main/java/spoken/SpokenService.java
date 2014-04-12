package spoken;

import static java.lang.System.getenv;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.net.URI;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;
import spoken.resources.AccountDatabase;
import spoken.resources.ReadHistory;
import spoken.resources.RegisterResource;
import spoken.resources.SpokenResource;

public class SpokenService extends Application<Configuration> {

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {}

    @Override
    public void run(final Configuration config, final Environment env) throws Exception {
        final AccountDatabase accounts = new AccountDatabase(redis(env));
        final ReadHistory history = new ReadHistory(redis(env));
        env.jersey().register(new SpokenResource(accounts, history));
        env.jersey().register(new RegisterResource(accounts));
    }

    private static JedisUtil redis(final Environment env) {
        final URI uri = URI.create(getenv("REDISCLOUD_URL"));
        final JedisPool pool = new JedisPool(new JedisPoolConfig(),
                uri.getHost(),
                uri.getPort(),
                Protocol.DEFAULT_TIMEOUT,
                uri.getUserInfo().split(":", 2)[1]);
        final JedisUtil redis = new JedisUtil(pool);
        env.lifecycle().manage(redis);
        return redis;
    }

    public static void main(final String[] args) throws Exception {
        new SpokenService().run(args);
    }
}
