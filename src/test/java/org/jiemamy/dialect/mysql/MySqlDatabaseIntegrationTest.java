/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2011/01/29
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.util.Set;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jiemamy.JiemamyContext;
import org.jiemamy.composer.exporter.SimpleSqlExportConfig;
import org.jiemamy.composer.exporter.SqlExporter;
import org.jiemamy.composer.importer.DbImporter;
import org.jiemamy.model.DbObject;
import org.jiemamy.test.MySqlDatabaseTest;
import org.jiemamy.test.TestModelBuilders;
import org.jiemamy.utils.DbCleaner;
import org.jiemamy.utils.sql.SqlExecutor;

/**
 * TODO for daisuke
 * 
 * @version $Id$
 * @author daisuke
 */
public class MySqlDatabaseIntegrationTest extends MySqlDatabaseTest {
	
	private static Logger logger = LoggerFactory.getLogger(MySqlDatabaseIntegrationTest.class);
	

	/**
	 * TODO for daisuke
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test01_import() throws Exception {
		DbImporter importer = new DbImporter();
		JiemamyContext context = new JiemamyContext();
		boolean importModel = importer.importModel(context, newImportConfig());
		assertThat(importModel, is(true));
		
		Set<DbObject> dbObjects = context.getDbObjects();
		for (DbObject dbObject : dbObjects) {
			logger.info(dbObject.toString());
		}
	}
	
	/**
	 * TODO for daisuke
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test02_clean() throws Exception {
		// まず clean
		DbCleaner.clean(newImportConfig());
		
		// export
		File outFile = new File("target/testresult/PostgresqlDatabaseTest_test02.sql");
		
		SimpleSqlExportConfig config = new SimpleSqlExportConfig();
		config.setDataSetIndex(0);
		config.setEmitDropStatements(false);
		config.setOutputFile(outFile);
		config.setOverwrite(true);
		
		new SqlExporter().exportModel(TestModelBuilders.EMP_DEPT.getBuiltModel(MySqlDialect.class.getName()), config);
		
		// execute
		Connection connection = null;
		FileReader fileReader = null;
		try {
			connection = getConnection();
			SqlExecutor sqlExecutor = new SqlExecutor(connection);
			fileReader = new FileReader(outFile);
			sqlExecutor.execute(fileReader);
		} finally {
			IOUtils.closeQuietly(fileReader);
			DbUtils.closeQuietly(connection);
		}
		
		// assert not zero
		JiemamyContext context = new JiemamyContext();
		assertThat(new DbImporter().importModel(context, newImportConfig()), is(true));
		assertThat(context.getDbObjects().size(), is(not(0)));
		
		// clean
		DbCleaner.clean(newImportConfig());
		
		// assert zero
		JiemamyContext context2 = new JiemamyContext();
		assertThat(new DbImporter().importModel(context2, newImportConfig()), is(true));
		assertThat(context2.getDbObjects().size(), is(0));
	}
}
