package spoken.resources;

import spoken.JedisUtil;
import spoken.JedisUtil.NonTx;

import com.google.common.base.Optional;

public class AccountDatabase {

    private final JedisUtil redis;

    public AccountDatabase(final JedisUtil redis) {
        this.redis = redis;
    }

    public void register(final String number, final String address) {
        try(NonTx nonTx = this.redis.nonTx()) {
            nonTx.redis().set("notify:".concat(number), address);
        }
    }

    public Optional<String> isRegistred(final String number) {
        try(NonTx nonTx = this.redis.nonTx()) {
            return Optional.fromNullable(nonTx.redis().get("notify:".concat(number)));
        }
    }

    public Optional<String> isRegistred(final Optional<String> from) {
        if(from.isPresent()) {
            return isRegistred(from.get());
        }
        return Optional.absent();
    }
}
