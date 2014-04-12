package spoken.resources;

import spoken.JedisUtil;
import spoken.JedisUtil.NonTx;

public class ReadHistory {

    private final JedisUtil redis;

    public ReadHistory(final JedisUtil redis) {
        this.redis = redis;
    }

    public boolean isUnread(final String number, final String uri) {
        try(NonTx nonTx = this.redis.nonTx()) {
            return !nonTx.redis().sismember(number, uri);
        }
    }

    public void markAsRead(final String number, final String uri) {}
}
