package spoken.models;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
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
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

public class Source implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Source.class);
    private final FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
    private final FeedFetcher fetcher = new HttpURLFeedFetcher(this.feedInfoCache);
    private final List<Article> articles;
    private final URI uri;
    private final String name;

    public Source(final String name, final String uri) {
        this.name = name;
        this.uri = URI.create(uri);
        this.articles = Lists.newArrayList();
        final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this, 0, 5, TimeUnit.MINUTES);
    }

    public void say(final TwiMLResponse twiml, final String number) throws TwiMLException {
        final Iterator<Article> it = this.articles.iterator();
        while(it.hasNext()) {
            final Article article = it.next();

            article.say(twiml, this.name);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void run() {
        try {
            final SyndFeed feed = this.fetcher.retrieveFeed(this.uri.toURL());
            final List<SyndEntry> entries = feed.getEntries();
            if(entries.size() > 0) {
                this.articles.clear();
                for(final SyndEntry entry : entries) {
                    try {
                        this.articles.add(new Article(entry));
                    }
                    catch(final NullPointerException npe) {}
                }
            }
        }
        catch(IllegalArgumentException | IOException | FeedException | FetcherException e) {
            LOG.error("Unknown exception", e);
        }

    }
}
