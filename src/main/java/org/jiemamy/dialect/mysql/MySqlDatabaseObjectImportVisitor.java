/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2011/02/12
 *
 * This file is part of Jiemamy.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.jiemamy.dialect.mysql;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jiemamy.dialect.DefaultDatabaseObjectImportVisitor;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StandardEngine;
import org.jiemamy.model.DbObject;
import org.jiemamy.model.SimpleDbObject;
import org.jiemamy.utils.sql.metadata.TableMeta;
import org.jiemamy.utils.sql.metadata.TypeSafeDatabaseMetaData;

/**
 * TODO for daisuke
 * 
 * @version $Id$
 * @author daisuke
 */
public class MySqlDatabaseObjectImportVisitor extends DefaultDatabaseObjectImportVisitor {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param dialect {@link Dialect}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public MySqlDatabaseObjectImportVisitor(MySqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected DbObject createDbObject(TableMeta tableMeta) throws SQLException {
		SimpleDbObject dbObject = (SimpleDbObject) super.createDbObject(tableMeta);
		
		try {
			// TODO すまぬ、無茶しているｗ  TypeSafeDatabaseMetaData#getConnection():Connection があればよかった…。
			Field field = TypeSafeDatabaseMetaData.class.getDeclaredField("meta");
			field.setAccessible(true);
			DatabaseMetaData meta = (DatabaseMetaData) field.get(getMeta());
			Connection connection = meta.getConnection();
			String engineTypeString = getEngineType(connection, dbObject.getName());
			if (engineTypeString != null) {
				StandardEngine engineType = StandardEngine.valueOf(engineTypeString);
				dbObject.putParam(MySqlParameterKeys.STORAGE_ENGINE, engineType);
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbObject;
	}
	
	// http://jira.jiemamy.org/browse/DMYS-2
	String getEngineType(Connection conn, String tableName) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("SHOW TABLE STATUS");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				if (rs.getString(1).equals(tableName)) {
					return rs.getString(2);
				}
			}
			return null;
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}
}
