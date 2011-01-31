/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2009/04/04
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
package org.jiemamy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.jiemamy.composer.importer.DefaultDatabaseImportConfig;
import org.jiemamy.test.MySqlDatabaseTest;
import org.jiemamy.utils.sql.SqlExecutor;

/**
 * {@link DatabaseCleaner}のテストクラス。
 * 
 * @author daisuke
 */
public class DatabaseCleanerMySqlTest extends MySqlDatabaseTest {
	
	private DatabaseCleaner databaseCleaner;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		databaseCleaner = new DatabaseCleaner();
	}
	
	/**
	 * テストの情報を破棄する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@After
	public void tearDown() throws Exception {
		databaseCleaner = null;
	}
	
	/**
	 * データベースをcleanしてみる。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	@Ignore("まだv0.3向けに書き換えていない")
	public void test01_データベースをcleanしてみる() throws Exception {
		DefaultDatabaseImportConfig config = new DefaultDatabaseImportConfig();
//		config.setDialect(new GenericDialect());
		config.setDriverClassName("org.postgresql.Driver");
		config.setDriverJarPaths(new URL[] {
			new File("./src/test/resources/postgresql-8.3-603.jdbc3.jar").toURI().toURL()
		});
		config.setUsername("postgres");
		config.setPassword("postgres");
		config.setUri("jdbc:postgresql://localhost:5432/jiemamy");
		
		Connection mockConnection = mock(Connection.class);
		SqlExecutor mockExecutor = spy(new SqlExecutor(mockConnection));
		
		DatabaseCleaner mockCleaner = spy(databaseCleaner);
		doReturn(mockConnection).when(mockCleaner).connect(eq(config), any(Properties.class), any(URL[].class),
				anyString());
//		when(mockCleaner.connect(eq(config), any(Properties.class), any(URL[].class), anyString())).thenReturn(
//				mockConnection);
		doReturn(mockExecutor).when(mockCleaner).getExecutor(eq(mockConnection));
//		when(mockCleaner.getExecutor(eq(mockConnection))).thenReturn(mockExecutor);
		
		mockCleaner.clean(config);
		
		verify(mockConnection).close();
	}
}
