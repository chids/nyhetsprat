package spoken.resources;

import java.util.Collection;

import spoken.JedisUtil;
import spoken.JedisUtil.NonTx;

public class ReadHistory {

    private final JedisUtil redis;

    public ReadHistory(final JedisUtil redis) {
        this.redis = redis;
    }

    public boolean isUnread(final String uri, final String number) {
        try(NonTx nonTx = this.redis.nonTx()) {
            return !nonTx.redis().sismember(number, uri);
        }
    }

    public void markAsRead(final String uri, final String number) {
        try(NonTx nonTx = this.redis.nonTx()) {
            nonTx.redis().sadd(number, uri);
            nonTx.redis().sadd("recent-" + number, uri);
        }
    }

    public Collection<String> recentUrls(final String number) {
        try(NonTx nonTx = this.redis.nonTx()) {
            return nonTx.redis().smembers("recent-" + number);
        }
    }

    public void clearRecentURls(final String number) {
        try(NonTx nonTx = this.redis.nonTx()) {
            nonTx.redis().del("recent-" + number);
        }
    }
}
