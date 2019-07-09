/**
 * This file is part of Waarp Project.
 * <p>
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the COPYRIGHT.txt in the
 * distribution for a full listing of individual contributors.
 * <p>
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <p>
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along with Waarp. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.database;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 *
 * A simple standalone JDBC connection pool manager.
 * <p/>
 * The public methods of this class are thread-safe.
 * <p/>
 * Nothe that JDBC4 is needed and isValid() must be implemented (not yet in PostGre in April 2012)
 * <p/>
 *
 * @author Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland<br>
 *         Multi-licensed: EPL/LGPL/MPL.
 * @author Frederic Bregier <br>
 *         Add TimerTask support to close after some "delay" any still connected sessions
 *
 */
public class DbConnectionPool {
    private ConnectionPoolDataSource dataSource;

    private int maxConnections;

    private int timeout;

    private long timeOutForceClose = 300000; // 5 minutes

    // private PrintWriter logWriter;
    private Semaphore semaphore;

    private Queue<Con> recycledConnections;

    private int activeConnections;

    private PoolConnectionEventListener poolConnectionEventListener;

    private boolean isDisposed;

    /**
     * Constructs a MiniConnectionPoolManager object with no timeout and no limit.
     *
     * @param dataSource
     *            the data source for the connections.
     */
    public DbConnectionPool(ConnectionPoolDataSource dataSource) {
        this(dataSource, 0, DbConstant.DELAYMAXCONNECTION);
    }

    /**
     * Constructs a MiniConnectionPoolManager object with no timeout and no limit.
     *
     * @param dataSource
     *            the data source for the connections.
     * @param timer
     * @param delay
     *            in ms period of time to check existing connections and limit to get a new
     *            connection
     *
     */
    public DbConnectionPool(ConnectionPoolDataSource dataSource, Timer timer, long delay) {
        this(dataSource, 0, (int) (delay / 1000));
        timer.schedule(new TimerTaskCheckConnections(timer, delay, this),
                       delay);
    }

    /**
     * Constructs a MiniConnectionPoolManager object with a timeout of DbConstant.DELAYMAXCONNECTION
     * seconds.
     *
     * @param dataSource
     *            the data source for the connections.
     * @param maxConnections
     *            the maximum number of connections. 0 means no limit
     */
    public DbConnectionPool(ConnectionPoolDataSource dataSource,
                            int maxConnections) {
        this(dataSource, maxConnections, DbConstant.DELAYMAXCONNECTION);
    }

    /**
     * Constructs a ConnectionPool object.
     *
     * @param dataSource
     *            the data source for the connections.
     * @param maxConnections
     *            the maximum number of connections. 0 means no limit
     * @param timeout
     *            the maximum time in seconds to wait for a free connection.
     */
    public DbConnectionPool(ConnectionPoolDataSource dataSource,
                            int maxConnections, int timeout) {
        this.dataSource = dataSource;
        this.maxConnections = maxConnections;
        this.timeout = timeout;
        if (maxConnections != 0) {
            // if (maxConnections < 1) throw new
            // IllegalArgumentException("Invalid maxConnections value.");
            if (timeout <= 0) {
                throw new IllegalArgumentException("Invalid timeout value.");
            }
            semaphore = new Semaphore(maxConnections, true);
        }
        recycledConnections = new ArrayDeque<Con>();
        poolConnectionEventListener = new PoolConnectionEventListener();
    }

    /**
     * Release all idle connections
     */
    public synchronized void freeIdleConnections() {
        Iterator<Con> conIterator = recycledConnections.iterator();
        long now = System.currentTimeMillis();
        while (conIterator.hasNext()) {
            Con c = conIterator.next();
            if (c.lastRecyle + timeOutForceClose < now) {
                conIterator.remove();
                closeConnectionNoEx(c.pooledCon);
            }
        }
    }

    public void resetPoolDataSource(ConnectionPoolDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     *
     * @return the max number of connections
     */
    public int getMaxConnections() {
        return this.maxConnections;
    }

    /**
     *
     * @return the Login Timeout in second
     */
    public long getLoginTimeout() {
        return this.timeout;
    }

    /**
     *
     * @return the Force Close Timeout in ms
     */
    public long getTimeoutForceClose() {
        return this.timeOutForceClose;
    }

    /**
     * Closes all unused pooled connections.
     *
     * @throws java.sql.SQLException
     *             //
     */
    public synchronized void dispose() throws SQLException {
        if (isDisposed) {
            return;
        }
        isDisposed = true;
        SQLException e = null;
        while (!recycledConnections.isEmpty()) {
            Con c = recycledConnections.remove();
            PooledConnection pconn = c.pooledCon;
            try {
                pconn.close();
            } catch (SQLException e2) {
                if (e == null) {
                    e = e2;
                }
            }
        }
        if (e != null) {
            throw e;
        }
    }

    /**
     * Retrieves a connection from the connection pool. If <code>maxConnections</code> connections
     * are already in use, the method waits until a connection becomes available or <code>timeout</code> seconds elapsed. When the
     * application is finished using the connection,
     * it must close it in order to return it to the pool.
     *
     * @return a new Connection object.
     * @throws TimeoutException
     *             when no connection becomes available within <code>timeout</code> seconds.
     * @throws java.sql.SQLException
     *             //
     */
    public Connection getConnection() throws SQLException {
        // This routine is unsynchronized, because semaphore.tryAcquire() may
        // block.
        synchronized (this) {
            if (isDisposed) {
                throw new IllegalStateException(
                        "Connection pool has been disposed.");
            }
        }
        if (semaphore != null) {
            try {
                if (!semaphore.tryAcquire(timeout, TimeUnit.SECONDS)) {
                    throw new TimeoutException();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(
                        "Interrupted while waiting for a database connection.",
                        e);
            }
        }
        boolean ok = false;
        try {
            Connection conn = getConnection2();
            ok = true;
            return conn;
        } finally {
            if (semaphore != null) {
                if (!ok) {
                    semaphore.release();
                }
            }
        }
    }

    private synchronized Connection getConnection2() throws SQLException {
        if (isDisposed) {
            throw new IllegalStateException(
                    "Connection pool has been disposed."); // test again with
        }
        // lock
        long time = System.currentTimeMillis() + timeout * 1000;
        while (true) {
            PooledConnection pconn;
            if (!recycledConnections.isEmpty()) {
                pconn = recycledConnections.remove().pooledCon;
            } else {
                pconn = dataSource.getPooledConnection();
            }

            Connection conn = pconn.getConnection();
            if (conn.isValid(DbConstant.VALIDTESTDURATION)) {
                activeConnections++;
                pconn.addConnectionEventListener(poolConnectionEventListener);
                assertInnerState();
                return conn;
            }
            if (time > System.currentTimeMillis()) {
                // too long
                break;
            }
        }

        throw new SQLException("Could not get a valid connection before timeout");
    }

    private synchronized void recycleConnection(PooledConnection pconn) {
        if (isDisposed) {
            disposeConnection(pconn);
            return;
        }
        try {
            if (!pconn.getConnection().isValid(DbConstant.VALIDTESTDURATION)) {
                disposeConnection(pconn);
                return;
            }
        } catch (SQLException e) {
            disposeConnection(pconn);
            return;
        }
        if (activeConnections <= 0) {
            throw new AssertionError();
        }
        activeConnections--;
        if (semaphore != null) {
            semaphore.release();
        }
        recycledConnections.add(new Con(pconn));
        assertInnerState();
    }

    private synchronized void disposeConnection(PooledConnection pconn) {
        if (activeConnections <= 0) {
            throw new AssertionError();
        }
        activeConnections--;
        if (semaphore != null) {
            semaphore.release();
        }
        closeConnectionNoEx(pconn);
        assertInnerState();
    }

    private void closeConnectionNoEx(PooledConnection pconn) {
        try {
            pconn.close();
        } catch (SQLException e) {
            //
        }
    }

    private void assertInnerState() {
        if (activeConnections < 0) {
            throw new AssertionError();
        }
        if (semaphore != null) {
            if (activeConnections + recycledConnections.size() > maxConnections) {
                throw new AssertionError();
            }
            if (activeConnections + semaphore.availablePermits() > maxConnections) {
                throw new AssertionError();
            }
        }
    }

    /**
     * Returns the number of active (open) connections of this pool. This is the number of <code>Connection</code> objects that
     * have been issued by {@link #getConnection()} for which <code>Connection.close()</code> has not yet been called.
     *
     * @return the number of active connections.
     */
    public synchronized int getActiveConnections() {
        return activeConnections;
    }

    static class Con {
        final PooledConnection pooledCon;

        long lastRecyle;

        Con(PooledConnection pooledCon) {
            this.pooledCon = pooledCon;
            lastRecyle = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || !(o instanceof Con)) {
                return false;
            }

            Con con = (Con) o;

            return pooledCon.equals(con.pooledCon);
        }

        @Override
        public int hashCode() {
            return pooledCon.hashCode();
        }
    }

    /**
     * Class to check validity of connections in the pool
     *
     * @author Frederic Bregier
     *
     */
    private static class TimerTaskCheckConnections extends TimerTask {
        DbConnectionPool pool;
        Timer timer;
        long delay;

        /**
         *
         * @param timer
         * @param delay
         * @param pool
         */
        private TimerTaskCheckConnections(Timer timer, long delay, DbConnectionPool pool) {
            if (pool == null || timer == null || delay < 1000) {
                throw new IllegalArgumentException(
                        "Invalid values. Need pool, timer and delay >= 1000");
            }
            this.pool = pool;
            this.timer = timer;
            this.delay = delay;
        }

        public void run() {
            Iterator<Con> conIterator = pool.recycledConnections.iterator();
            long now = System.currentTimeMillis();
            while (conIterator.hasNext()) {
                Con c = conIterator.next();
                if (c.lastRecyle + pool.timeOutForceClose < now) {
                    conIterator.remove();
                    pool.closeConnectionNoEx(c.pooledCon);
                } else {
                    try {
                        if (!c.pooledCon.getConnection().isValid(DbConstant.VALIDTESTDURATION)) {
                            conIterator.remove();
                            pool.closeConnectionNoEx(c.pooledCon);
                        }
                    } catch (SQLException e) {
                        conIterator.remove();
                        pool.closeConnectionNoEx(c.pooledCon);
                    }
                }
            }
            timer.schedule(this, delay);
        }

    }

    /**
     * Thrown in when no free connection becomes available within <code>timeout</code> seconds.
     */
    public static class TimeoutException extends RuntimeException {
        private static final long serialVersionUID = 1;

        public TimeoutException() {
            super("Timeout while waiting for a free database connection.");
        }
    }

    private class PoolConnectionEventListener implements
                                              ConnectionEventListener {
        public void connectionClosed(ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection) event.getSource();
            pconn.removeConnectionEventListener(this);
            recycleConnection(pconn);
        }

        public void connectionErrorOccurred(ConnectionEvent event) {
            PooledConnection pconn = (PooledConnection) event.getSource();
            pconn.removeConnectionEventListener(this);
            disposeConnection(pconn);
        }
    }
}
