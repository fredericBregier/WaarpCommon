/**
 * This file is part of Waarp Project.
 * 
 * Copyright 2009, Frederic Bregier, and individual contributors by the @author tags. See the
 * COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 * 
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with Waarp. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;

import org.apache.commons.dbcp.BasicDataSource;

import org.waarp.common.database.exception.WaarpDatabaseNoConnectionException;
import org.waarp.common.database.exception.WaarpDatabaseSqlException;
import org.waarp.common.database.model.DbModel;
import org.waarp.common.database.model.DbModelFactory;
import org.waarp.common.database.model.DbType;
import org.waarp.common.database.model.EmptyDbModel;
import org.waarp.common.logging.WaarpLogger;
import org.waarp.common.logging.WaarpLoggerFactory;
import org.waarp.common.utility.UUID;
import org.waarp.common.utility.WaarpThreadFactory;

/**
 * A wrapping of org.apache.commons.dbcp.BasicDataSource
 * to store Database connection information
 * and handle Datanase connection request.
 * 
 * @author Frederic Bregier
 * 
 * @deprecated Will be removed for a proper ConnectionFactory in a future version of WaarpCommon
 */ 
// TODO 4.0 remove
public class DbAdmin {
    /**
     * Internal Logger
     */
    private static final WaarpLogger logger = WaarpLoggerFactory
            .getLogger(DbAdmin.class);

    public static int RETRYNB = 3;

    public static long WAITFORNETOP = 100;

    /**
     * The datasource for connection pooling
     */
    private BasicDataSource ds;

    /**
     * Database type
     */
    protected final DbType typeDriver;

    /**
     * DbModel
     */
    private final DbModel dbModel;

    /**
     * The connection url to be passed to the JDBC driver to establish a connection.
     */
    private final String server;

    /**
     * The connection username to be passed to the JDBC driver to establish a connection.
     */
    private final String user;

    /**
     * The connection password to be passed to the JDBC driver to establish a connection.
     */
    private final String password;

    /**
     * The connection readOnly property.
     */
    private boolean readOnly = false;

    /**
     * session is the Session object for all type of requests
     */
    private DbSession session = null;
    
    /**
     * Number of HttpSession
     */
    private static int nbHttpSession = 0;

    /**
     * Number of HttpSession
     */
    protected static final Timer dbSessionTimer = new HashedWheelTimer(new WaarpThreadFactory("TimerClose"),
            50, TimeUnit.MILLISECONDS, 1024);

    /**
     * @return the session
     *
     * @deprecated Use {@link #getConnection()} instead.
     */
    @Deprecated
    public DbSession getSession() {
         return session;
    }

    public Connection getConnection(UUID id) throws WaarpDatabaseNoConnectionException {
	try {
	    Connection con = ds.getConnection();
	    DbAdmin.addConnection(id, con);
	    return con;
	} catch (SQLException e) {
	    throw new WaarpDatabaseNoConnectionException("Cannot access database", e);
	}
    }

    /**
     * @param session the session to set
     */
    @Deprecated
    public void setSession(DbSession session) {
        // Do Nothing
    }

    /**
     * @return True if the connection is ReadOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @return true
     *
     * @deprecated You should not use this function ask for a connection and catch errors instead.
     */
    @Deprecated
    public boolean isActive() {
        return true;
    }

    /**
     * @param isActive the isActive to set
     *
     * @deprecated This will be removed in a future version of WaarpCommon. 
     * DBCP instegration set active to true.
     */
    @Deprecated
    public void setActive(boolean isActive) {
    	// Do Nothing
    }

    /**
     * Validate connection
     * 
     * @throws WaarpDatabaseNoConnectionException if a database access errors occurs
     * 
     * @deprecated Use {@link #validateConnection()} instead.
     */
    @Deprecated
    public void validConnection() throws WaarpDatabaseNoConnectionException {
    	validateConnection();
    }

    /**
     * Validate connection
     * 
     * @throws WaarpDatabaseNoConnectionException if a database access errors occurs
     */
    public void validateConnection() throws WaarpDatabaseNoConnectionException {
    	try {
	    ds.getConnection().close();
	} catch (SQLException e) {
	    throw new WaarpDatabaseNoConnectionException("Cannot access database", e);
	}
    }

    /**
     * Use a default server for basic connection. Later on, specific connection to database for the
     * scheme that provides access to the table R66DbIndex for one specific Legacy could be done.
     * 
     * A this time, only one driver is possible! If a new driver is needed, then we need to create a
     * new DbSession object. Be aware that DbSession.initialize should be call only once for each
     * driver, whatever the number of DbSession objects that could be created (=> need a hashtable
     * for specific driver when created). Also, don't know if two drivers at the same time (two
     * different DbSession) is allowed by JDBC.
     * 
     * @param model
     * @param server
     * @param user
     * @param password
     * @throws WaarpDatabaseNoConnectionException
     */
    public DbAdmin(DbModel model, String server, String user, String password)
            throws WaarpDatabaseNoConnectionException {
        this(model, server, user, password, true);
    }

    /**
     * Use a default server for basic connection. Later on, specific connection to database for the
     * scheme that provides access to the table R66DbIndex for one specific Legacy could be done.
     * 
     * A this time, only one driver is possible! If a new driver is needed, then we need to create a
     * new DbSession object. Be aware that DbSession.initialize should be call only once for each
     * driver, whatever the number of DbSession objects that could be created (=> need a hashtable
     * for specific driver when created). Also, don't know if two drivers at the same time (two
     * different DbSession) is allowed by JDBC.
     * 
     * @param model
     * @param server
     * @param user
     * @param password
     * @param write
     * @throws WaarpDatabaseSqlException
     * @throws WaarpDatabaseNoConnectionException
     */
    public DbAdmin(DbModel model, String server, String user, String password,
            boolean write) throws WaarpDatabaseNoConnectionException {
        this.server = server;
        this.user = user;
        this.password = password;
        this.dbModel = model;
        this.typeDriver = model.getDbType();
        if (typeDriver == null) {
            logger.error("Cannot find TypeDriver");
            throw new WaarpDatabaseNoConnectionException(
                    "Cannot find database driver");
        }

	ds = new BasicDataSource();
	try {
	    ds.setDriverClassName(DriverManager.getDriver(server).getClass().getName());
	} catch (SQLException e) {
            throw new WaarpDatabaseNoConnectionException(
                    "Cannot find database driver");
	}
	ds.setUrl(this.server);
	ds.setUsername(this.user);
	ds.setPassword(this.password);
	
	ds.setDefaultAutoCommit(true);
	ds.setDefaultReadOnly(!write);
	ds.setValidationQuery(this.dbModel.getValidationQuery());
	readOnly = !write;

	validateConnection();
	this.session = new DbSession(this, readOnly); 
    }

    /**
     * Empty constructor for no Database support (very thin client)
     */
    public DbAdmin() {
        server = null;
        user = null;
        password = null;
        dbModel = new EmptyDbModel();
        typeDriver = DbType.none;
        DbModelFactory.classLoaded.add(DbType.none.name());
    }

    /**
     * Closes and releases all registered connections and connection pool
     */
    public void close() {
	logger.info("DBAdmin closing");
	DbAdmin.closeAllConnection();
	try {
	    ds.close();
	} catch (SQLException e) {
	    logger.debug("Cannot properly close the database connection pool", e);
	}
    }

    /**
     * Commit on connection (since in autocommit, should not be used)
     * 
     * @throws WaarpDatabaseNoConnectionException
     * @throws WaarpDatabaseSqlException
     *
     */
    public void commit() throws WaarpDatabaseSqlException,
            WaarpDatabaseNoConnectionException {
        if (getSession() != null) {
            getSession().commit();
        }
    }

    /**
     * @return the JDBC connection url property
     */
    public String getServer() {
        return server;
    }

    /**
     * @return the JDBC connection username property
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the JDBC connection password property
     *
     * @deprecated Use {@link #getPassword()} instead.
     */
    @Deprecated
    public String getPasswd() {
        return password;
    }

    /**
     * @return the JDBC connection password property
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the associated dbModel
     */
    public DbModel getDbModel() {
        return dbModel;
    }

    /**
     * @return the typeDriver
     */
    public DbType getTypeDriver() {
        return typeDriver;
    }

    @Override
    public String toString() {
        return "Admin: " + typeDriver.name() + ":" + server + ":" + user + ":" + (password.length());
    }

    /**
     * List all Connection to enable the close call on them
     */
    private static ConcurrentHashMap<UUID, Connection> connections = new ConcurrentHashMap<UUID, Connection>();

    /**
     * Increment nb of Http Connection
     */
    public static void incHttpSession() {
        nbHttpSession++;
    }
    /**
     * Decrement nb of Http Connection
     */
    public static void decHttpSession() {
        nbHttpSession--;
    }
    /**
     * @return the nb of Http Connection
     */
    public static int getHttpSession() {
        return nbHttpSession;
    }

    /**
     * Add a Connection into the list
     * 
     * @param id
     * @param session
     * @deprecated This will be removed in a future version of WaarpCommon. 
     *
     * use {@link #addConnection(UUID id, Connection con)} instead.
     */
    @Deprecated
    public static void addConnection(UUID id, DbSession session) {
	DbAdmin.addConnection(id, session.getConn());
    }

    /**
     * Add a Connection into the list
     * 
     * @param id
     * @param connection
     */
    public static void addConnection(UUID id, Connection con) {
        connections.put(id, con);
    }

    /**
     * Remove a Connection from the list
     * 
     * @param id Id of the connection
     */
    public static void removeConnection(UUID id) {
	logger.info("Remove connection " + id);
	Connection con = connections.get(id);
	try {
	    con.close();
	} catch (SQLException e) {
	    logger.debug("Cannot properly close database connection: " + id, e);
	}
	connections.remove(id);
    }

    /**
     * @return the number of connection (so number of network channels)
     */
    public static int getNbConnection() {
        return connections.size() - 1;
    }

    /**
     * Close all database connections
     *
     * @deprecated This will be removed in a future version of WaarpCommon. 
     * Use {@link #close()} instead.
     */
    @Deprecated
    public static void closeAllConnection() {
        for (UUID id : connections.keySet()) {
            removeConnection(id);
	}
	connections.clear();
	for (DbModel dbModel : DbModelFactory.dbModels) {
            if (dbModel != null) {
                dbModel.releaseResources();
            }
        }
        dbSessionTimer.stop();
    }

    /**
     * Check all database connections and try to reopen them if disActive
     *
     * @deprecated This will be removed in a future version of WaarpCommon. 
     * Use {@link #validateConnection()} instead.
     */
    // TODO 4.0 remove
    @Deprecated
    public static void checkAllConnections() {
    	//Do Nothing
    }

    /**
     * @return True if this driver allows Thread Shared Connexion (concurrency usage)
     */
    public boolean isCompatibleWithThreadSharedConnexion() {
        return typeDriver != DbType.MariaDB && 
		typeDriver != DbType.MySQL && 
		typeDriver != DbType.Oracle && 
		typeDriver != DbType.none;
    }
}
