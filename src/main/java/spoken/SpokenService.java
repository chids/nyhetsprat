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
import spoken.resources.CallbackResource;
import spoken.resources.EmailSender;
import spoken.resources.ReadHistory;
import spoken.resources.RegisterResource;
import spoken.resources.SpokenResource;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.twilio.sdk.TwilioRestClient;

public class SpokenService extends Application<Configuration> {

    @Override
    public void initialize(final Bootstrap<Configuration> bootstrap) {}

    @Override
    public void run(final Configuration config, final Environment env) throws Exception {
        final TwilioRestClient twilio = new TwilioRestClient(getenv("TWILIO_SID"), getenv("TWILIO_TOKEN"));
        final JedisUtil redis = redis(env);
        final EmailSender email = new EmailSender(getenv("SENDGRID_USERNAME"), getenv("SENDGRID_PASSWORD"));
        final AccountDatabase accounts = new AccountDatabase(redis, twilio);
        final ReadHistory history = new ReadHistory(redis);
        env.jersey().register(new SpokenResource(accounts, history));
        env.jersey().register(new RegisterResource(accounts));
        env.jersey().register(new CallbackResource(accounts, history, email, twitter()));
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

    private static Twitter twitter() {
        final ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey(getenv("TWITTER_KEY"))
                .setOAuthConsumerSecret(getenv("TWITTER_SECRET"))
                .setOAuthAccessToken(getenv("TWITTER_ACCESS_TOKEN"))
                .setOAuthAccessTokenSecret(getenv("TWITTER_ACCESS_SECRET"));
        final TwitterFactory tf = new TwitterFactory(cb.build());
        return tf.getInstance();
    }

    public static void main(final String[] args) throws Exception {
        new SpokenService().run(args);
    }
}
