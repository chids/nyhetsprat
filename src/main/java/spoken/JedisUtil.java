package spoken;

import io.dropwizard.lifecycle.Managed;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

public class JedisUtil implements Managed {

    private final JedisPool pool;

    public JedisUtil(final JedisPool pool) {
        this.pool = pool;
    }

    public JedisTx tx() {
        return new JedisTx();
    }

    public NonTx nonTx() {
        return new NonTx();
    }

    @Override
    public void start() throws Exception {}

    @Override
    public void stop() throws Exception {
        this.pool.destroy();
    }

    abstract class Connection implements AutoCloseable {

        protected final Jedis jedis;

        protected Connection() {
            this.jedis = JedisUtil.this.pool.getResource();
        }

        @Override
        public void close() {
            JedisUtil.this.pool.returnResource(this.jedis);
        }
    }

    public class NonTx extends Connection {

        public Jedis redis() {
            return super.jedis;
        }
    }

    public class JedisTx extends Connection {
        public final Transaction jedis;

        JedisTx() {
            this.jedis = super.jedis.multi();
        }

        public Transaction tx() {
            return this.jedis;
        }

        @Override
        public void close() {
            try {
                this.jedis.exec();
            }
            catch(final Exception e) {}
            super.close();
        }
    }
}
