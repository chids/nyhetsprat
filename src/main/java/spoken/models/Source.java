package spoken.models;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

public class Source implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Source.class);
    private final URI uri;
    private final String name;
    private final List<String> articles;

    public Source(final String name, final String uri) {
        this.name = name;
        this.uri = URI.create(uri);
        this.articles = Lists.newArrayList();
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
    }

    private static String removeHtml(final String something) {
        return something.replaceAll("\\<[^>]*>", "");
    }

    public void say(final TwiMLResponse twiml) throws TwiMLException {
        say(twiml, 0);
    }

    public void say(final TwiMLResponse twiml, final int rank) throws TwiMLException {
        System.err.println("rank: " + rank + " size: " + this.articles.size());
        if(this.articles.size() > rank) {
            final Say say = new Say(this.name + ": " + this.articles.get(rank));
            say.setLanguage("sv-SE");
            twiml.append(say);
        }
        else {
            LOG.info("Message rank is out of range [source={}]", this.name);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            final FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
            final FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);
            final SyndFeed feed = fetcher.retrieveFeed(this.uri.toURL());
            final List<SyndEntry> entries = feed.getEntries();
            if(entries.size() > 0) {
                this.articles.clear();
                for(final SyndEntry entry : entries) {
                    this.articles.add(removeHtml(entry.getTitle()));
                }
            }
        }
        catch(IllegalArgumentException | IOException | FeedException | FetcherException e) {
            LOG.error("Unknown exception", e);
        }

    }

}
