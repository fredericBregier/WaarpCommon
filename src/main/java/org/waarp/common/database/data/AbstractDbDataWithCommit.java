/*
 * Copyright (c) 2019, to individual contributors by the @author tags.
 * See the COPYRIGHT.txt in the distribution for a full listing of individual contributors.
 *
 * This file is part of Waarp Project.
 *
 * All Waarp Project is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Waarp is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Waarp . If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.waarp.common.database.data;

import org.waarp.common.database.DbPreparedStatement;
import org.waarp.common.database.DbSession;
import org.waarp.common.database.exception.WaarpDatabaseException;
import org.waarp.common.database.exception.WaarpDatabaseNoDataException;

/**
 * Abstract database table implementation with explicit COMMIT.<br>
 * <br>
 * 
 * If the connection is in autocommit, this abstract should not be used.<br>
 * If the connection is not in autocommit, one could use this implementation to implicitly commit
 * when needed automatically as should do an autocommit connection.
 * 
 * @author Frederic Bregier
 * 
 */
public abstract class AbstractDbDataWithCommit extends AbstractDbData {
    /**
     * To be implemented
     */
    // public static String table;
    // public static final int NBPRKEY;
    // protected static String selectAllFields;
    // protected static String updateAllFields;
    // protected static String insertAllValues;
    // protected DbValue[] primaryKey;
    // protected DbValue[] otherFields;
    // protected DbValue[] allFields;

    /**
     * Abstract constructor to set the DbSession to use
     * 
     * @param dbSession
     */
    public AbstractDbDataWithCommit(DbSession dbSession) {
        super(dbSession);
        initObject();
    }

    /**
     * Insert object into table
     * 
     * @throws WaarpDatabaseException
     */
    public void insert() throws WaarpDatabaseException {
        if (isSaved) {
            return;
        }
        if (dbSession == null) {
            isSaved = true;
            return;
        }
        setToArray();
        DbPreparedStatement preparedStatement = new DbPreparedStatement(
                dbSession);
        try {
            preparedStatement.createPrepareStatement("INSERT INTO " + getTable() +
                    " (" + getSelectAllFields() + ") VALUES " + getInsertAllValues());
            setValues(preparedStatement, allFields);
            int count = preparedStatement.executeUpdate();
            if (count <= 0) {
                throw new WaarpDatabaseNoDataException("No row found");
            }
            dbSession.commit();
            isSaved = true;
        } finally {
            preparedStatement.realClose();
        }
    }

    /**
     * Update object to table
     * 
     * @throws WaarpDatabaseException
     */
    public void update() throws WaarpDatabaseException {
        if (isSaved) {
            return;
        }
        if (dbSession == null) {
            isSaved = true;
            return;
        }
        setToArray();
        DbPreparedStatement preparedStatement = new DbPreparedStatement(
                dbSession);
        try {
            preparedStatement.createPrepareStatement("UPDATE " + getTable() +
                    " SET " + getUpdateAllFields() + " WHERE " +
                    getWherePrimaryKey());
            setValues(preparedStatement, allFields);
            int count = preparedStatement.executeUpdate();
            if (count <= 0) {
                throw new WaarpDatabaseNoDataException("No row found");
            }
            dbSession.commit();
            isSaved = true;
        } finally {
            preparedStatement.realClose();
        }
    }

    /**
     * Delete object from table
     * 
     * @throws WaarpDatabaseException
     */
    public void delete() throws WaarpDatabaseException {
        if (dbSession == null) {
            return;
        }
        DbPreparedStatement preparedStatement = new DbPreparedStatement(
                dbSession);
        try {
            preparedStatement.createPrepareStatement("DELETE FROM " + getTable() +
                    " WHERE " + getWherePrimaryKey());
            setPrimaryKey();
            setValues(preparedStatement, primaryKey);
            int count = preparedStatement.executeUpdate();
            if (count <= 0) {
                throw new WaarpDatabaseNoDataException("No row found");
            }
            dbSession.commit();
            isSaved = false;
        } finally {
            preparedStatement.realClose();
        }
    }
}
