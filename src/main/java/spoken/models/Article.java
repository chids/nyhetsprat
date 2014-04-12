package spoken.models;

import static com.google.common.base.Preconditions.checkNotNull;
import spoken.resources.ReadHistory;

import com.google.common.base.Strings;
import com.sun.syndication.feed.synd.SyndEntry;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

public class Article {

    private final String title;
    private final String uri;

    public Article(final SyndEntry entry) {
        this.title = convertQuotes(clean(entry.getTitle()));
        this.uri = clean(entry.getUri());
    }

    private static String clean(final String something) {
        return removeHtml(checkNotNull(Strings.emptyToNull(something)));
    }

    public static String convertQuotes(final String something) {
        return something.replaceFirst("\"", "Citat: \"");
    }

    private static String removeHtml(final String something) {
        return something.replaceAll("\\<[^>]*>", "");
    }

    public void say(final TwiMLResponse twiml, final String source) throws TwiMLException {
        final Say say = new Say(source + ": " + this.title);
        say.setLanguage("sv-SE");
        twiml.append(say);
    }

    public boolean isUnread(final ReadHistory history, final String number) {
        return history.isUnread(this.uri, number);
    }

    public void markAsRead(final ReadHistory history, final String number) {
        history.markAsRead(this.uri, number);
    }
}
