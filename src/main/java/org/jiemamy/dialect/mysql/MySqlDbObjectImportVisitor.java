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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jiemamy.dialect.DbObjectImportVisitor;
import org.jiemamy.dialect.DefaultDbObjectImportVisitor;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StandardEngine;
import org.jiemamy.dialect.mysql.parameter.StorageEngineType;
import org.jiemamy.model.DbObject;
import org.jiemamy.model.SimpleDbObject;
import org.jiemamy.model.view.SimpleJmView;
import org.jiemamy.utils.sql.metadata.TableMeta;

/**
 * MySQL用{@link DbObjectImportVisitor}実装クラス。
 * 
 * @version $Id$
 * @author daisuke
 */
public class MySqlDbObjectImportVisitor extends DefaultDbObjectImportVisitor {
	
	private static Logger logger = LoggerFactory.getLogger(MySqlDbObjectImportVisitor.class);
	

	/**
	 * インスタンスを生成する。
	 * 
	 * @param dialect {@link Dialect}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public MySqlDbObjectImportVisitor(MySqlDialect dialect) {
		super(dialect);
	}
	
	@Override
	protected DbObject createDbObject(TableMeta tableMeta) throws SQLException {
		SimpleDbObject dbObject = (SimpleDbObject) super.createDbObject(tableMeta);
		
		try {
			Connection connection = getMeta().getMetaData().getConnection();
			final String engineTypeString = getEngineType(connection, dbObject.getName());
			if (engineTypeString != null) {
				StorageEngineType engineType;
				try {
					engineType = StandardEngine.valueOf(engineTypeString);
				} catch (IllegalArgumentException e) {
					// 一応、無理矢理未知のエンジンタイプに対応しておく
					engineType = new StorageEngineType() {
						
						@Override
						public String toString() {
							return engineTypeString;
						}
					};
				}
				dbObject.putParam(MySqlParameterKeys.STORAGE_ENGINE, engineType);
			}
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			logger.error("exception is thrown", e);
		}
		return dbObject;
	}
	
	@Override
	protected SimpleJmView createView(String viewName) throws SQLException {
		Validate.notNull(viewName);
		
		SimpleJmView view = new SimpleJmView();
		view.setName(viewName);
		
		try {
			Connection connection = getMeta().getMetaData().getConnection();
			String definition = getViewDefinition(connection, viewName);
			view.setDefinition(definition);
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			logger.error("exception is thrown", e);
		}
		
		return view;
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
	
	String getViewDefinition(Connection conn, String viewName) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement("select * from information_schema.VIEWS where table_name = ?;");
			ps.setString(1, viewName);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getString("VIEW_DEFINITION");
			}
			return null;
		} finally {
			if (ps != null) {
				ps.close();
			}
		}
	}
}
