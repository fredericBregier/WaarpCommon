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

import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.logging.WaarpLogger;
import org.waarp.common.logging.WaarpLoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class to handle request
 *
 * @author Frederic Bregier
 */
public class DbRequest {
    /**
     * Internal Logger
     */
    private static final WaarpLogger logger = WaarpLoggerFactory
            .getLogger(DbRequest.class);
    /**
     * Internal DB Session
     */
    private final DbSession ls;
    /**
     * Internal Statement
     */
    private Statement stmt = null;
    /**
     * Internal Result Set
     */
    private ResultSet rs = null;

    /**
     * Create a new request from the DbSession
     *
     * @param ls
     *
     * @throws WaarpDatabaseNoConnectionException
     */
    public DbRequest(DbSession ls) throws WaarpDatabaseNoConnectionException {
        if (ls.isDisActive()) {
            ls.checkConnection();
        }
        this.ls = ls;
    }

    /**
     * Test if value is null and create the string for insert/update
     *
     * @param value
     *
     * @return the string as result
     */
    public static String getIsNull(String value) {
        return value == null? " is NULL" : " = '" + value + "'";
    }

    /**
     * Create a statement with some particular options
     *
     * @return the new Statement
     *
     * @throws WaarpDatabaseNoConnectionException
     * @throws WaarpDatabaseSqlException
     */
    private Statement createStatement()
            throws WaarpDatabaseNoConnectionException,
                   WaarpDatabaseSqlException {
        if (ls == null) {
            throw new WaarpDatabaseNoConnectionException("No connection");
        }
        if (ls.getConn() == null) {
            throw new WaarpDatabaseNoConnectionException("No connection");
        }
        if (ls.isDisActive()) {
            ls.checkConnection();
        }
        try {
            return ls.getConn().createStatement();
        } catch (SQLException e) {
            ls.checkConnection();
            try {
                return ls.getConn().createStatement();
            } catch (SQLException e1) {
                throw new WaarpDatabaseSqlException(
                        "Error while Create Statement", e);
            }
        }
    }

    /**
     * Execute a SELECT statement and set of Result. The statement must not be an update/insert/delete. The previous
     * statement and resultSet are closed.
     *
     * @param select
     *
     * @throws WaarpDatabaseSqlException
     * @throws WaarpDatabaseNoConnectionException
     */
    public void select(String select)
            throws WaarpDatabaseNoConnectionException,
                   WaarpDatabaseSqlException {
        close();
        stmt = createStatement();
        // rs = stmt.executeQuery(select);
        // or alternatively, if you don't know ahead of time that
        // the query will be a SELECT...
        try {
            if (stmt.execute(select)) {
                rs = stmt.getResultSet();
            }
        } catch (SQLException e) {
            logger.error("SQL Exception Request:" + select + " " +
                         e.getMessage());
            DbSession.error(e);
            ls.checkConnectionNoException();
            throw new WaarpDatabaseSqlException("SQL Exception Request:" +
                                                select, e);
        }
    }

    /**
     * Execute a SELECT statement and set of Result. The statement must not be an update/insert/delete. The previous
     * statement and resultSet are closed. The timeout is applied if > 0.
     *
     * @param select
     * @param timeout in seconds
     *
     * @throws WaarpDatabaseSqlException
     * @throws WaarpDatabaseNoConnectionException
     */
    public void select(String select, int timeout)
            throws WaarpDatabaseNoConnectionException,
                   WaarpDatabaseSqlException {
        close();
        stmt = createStatement();
        if (timeout > 0) {
            try {
                stmt.setQueryTimeout(timeout);
            } catch (SQLException e1) {
                // ignore
            }
        }
        // rs = stmt.executeQuery(select);
        // or alternatively, if you don't know ahead of time that
        // the query will be a SELECT...
        try {
            if (stmt.execute(select)) {
                rs = stmt.getResultSet();
            }
        } catch (SQLException e) {
            logger.error("SQL Exception Request:" + select + " " +
                         e.getMessage());
            DbSession.error(e);
            ls.checkConnectionNoException();
            throw new WaarpDatabaseSqlException("SQL Exception Request:" +
                                                select, e);
        }
    }

    /**
     * Execute a UPDATE/INSERT/DELETE statement and returns the number of row. The previous statement and resultSet are
     * closed.
     *
     * @param query
     *
     * @return the number of row in the query
     *
     * @throws WaarpDatabaseSqlException
     * @throws WaarpDatabaseNoConnectionException
     */
    public int query(String query) throws WaarpDatabaseNoConnectionException,
                                          WaarpDatabaseSqlException {
        close();
        stmt = createStatement();
        try {
            int rowcount = stmt.executeUpdate(query);
            logger.debug("QUERY(" + rowcount + "): {}", query);
            return rowcount;
        } catch (SQLException e) {
            logger.error("SQL Exception Request:" + query + " " +
                         e.getMessage());
            DbSession.error(e);
            ls.checkConnectionNoException();
            throw new WaarpDatabaseSqlException("SQL Exception Request:" +
                                                query, e);
        }
    }

    /**
     * Finished a Request (ready for a new one)
     */
    public void close() {
        // it is a good idea to release
        // resources in a finally{} block
        // in reverse-order of their creation
        // if they are no-longer needed
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException sqlEx) {
                ls.checkConnectionNoException();
            } // ignore
            rs = null;
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException sqlEx) {
                ls.checkConnectionNoException();
            } // ignore
            stmt = null;
        }
    }

    /**
     * Get the last ID autoincrement from the last request
     *
     * @return the long Id or DbConstant.ILLEGALVALUE (Long.MIN_VALUE) if an error occurs.
     *
     * @throws WaarpDatabaseNoDataException
     */
    public long getLastId() throws WaarpDatabaseNoDataException {
        ResultSet rstmp;
        long result = DbConstant.ILLEGALVALUE;
        try {
            rstmp = stmt.getGeneratedKeys();
            if (rstmp.next()) {
                result = rstmp.getLong(1);
            }
            rstmp.close();
            rstmp = null;
        } catch (SQLException e) {
            DbSession.error(e);
            ls.checkConnectionNoException();
            throw new WaarpDatabaseNoDataException("No data found", e);
        }
        return result;
    }

    /**
     * Move the cursor to the next result
     *
     * @return True if there is a next result, else False
     *
     * @throws WaarpDatabaseNoConnectionException
     * @throws WaarpDatabaseSqlException
     */
    public boolean getNext() throws WaarpDatabaseNoConnectionException,
                                    WaarpDatabaseSqlException {
        if (rs == null) {
            logger.error("SQL ResultSet is Null into getNext");
            throw new WaarpDatabaseNoConnectionException(
                    "SQL ResultSet is Null into getNext");
        }
        if (ls.isDisActive()) {
            ls.checkConnection();
            throw new WaarpDatabaseSqlException(
                    "Request cannot be executed since connection was recreated between");
        }
        try {
            return rs.next();
        } catch (SQLException e) {
            logger.warn("SQL Exception to getNextRow" + " " + e.getMessage());
            DbSession.error(e);
            ls.checkConnectionNoException();
            throw new WaarpDatabaseSqlException("SQL Exception to getNextRow",
                                                e);
        }
    }

    /**
     * @return The resultSet (can be used in conjunction of getNext())
     *
     * @throws WaarpDatabaseNoConnectionException
     */
    public ResultSet getResultSet() throws WaarpDatabaseNoConnectionException {
        if (rs == null) {
            throw new WaarpDatabaseNoConnectionException(
                    "SQL ResultSet is Null into getResultSet");
        }
        return rs;
    }
}
