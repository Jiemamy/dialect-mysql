/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
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
import org.jiemamy.SimpleJmMetadata;
import org.jiemamy.SqlFacet;
import org.jiemamy.composer.exporter.SimpleSqlExportConfig;
import org.jiemamy.composer.exporter.SqlExporter;
import org.jiemamy.composer.importer.DbImporter;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StandardEngine;
import org.jiemamy.dialect.mysql.parameter.StorageEngineType;
import org.jiemamy.model.DbObject;
import org.jiemamy.model.column.JmColumnBuilder;
import org.jiemamy.model.constraint.JmPrimaryKeyConstraint;
import org.jiemamy.model.datatype.RawTypeCategory;
import org.jiemamy.model.datatype.RawTypeDescriptor;
import org.jiemamy.model.datatype.SimpleDataType;
import org.jiemamy.model.datatype.SimpleRawTypeDescriptor;
import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.model.table.JmTable;
import org.jiemamy.model.table.JmTableBuilder;
import org.jiemamy.model.table.JmTable;
import org.jiemamy.model.view.JmView;
import org.jiemamy.test.MySqlDatabaseTest;
import org.jiemamy.test.TestModelBuilders;
import org.jiemamy.utils.DbCleaner;
import org.jiemamy.utils.sql.SqlExecutor;

/**
 * {@link MySqlDialect}と実DBの結合テスト。
 * 
 * @version $Id$
 * @author daisuke
 */
public class MySqlDatabaseIntegrationTest extends MySqlDatabaseTest {
	
	private static Logger logger = LoggerFactory.getLogger(MySqlDatabaseIntegrationTest.class);
	
	private static final RawTypeDescriptor INTEGER = new SimpleRawTypeDescriptor(RawTypeCategory.INTEGER, "INTEGER",
			"int4");
	
	private static final RawTypeDescriptor VARCHAR = new SimpleRawTypeDescriptor(RawTypeCategory.VARCHAR);
	
	// FORMAT-OFF
	private static final String VIEW_DEFINITION = "/* ALGORITHM=UNDEFINED */ "
			+ "select "
				+ "`T_FOO`.`ID` AS `ID`,"
				+ "`T_FOO`.`NAME` AS `NAME`,"
				+ "`T_FOO`.`HOGE` AS `HOGE` "
			+ "from `T_FOO` "
			+ "where (`T_FOO`.`ID` > 10)";
	// FORMAT-ON
	
	/**
	 * 実DBからインポートしてみる。
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
	 * 実DBをcleanしてみる。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test02_clean() throws Exception {
		// まず clean
		DbCleaner.clean(newImportConfig());
		
		// export
		File outFile = new File("target/testresult/MySqlDatabaseTest_test02.sql");
		
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
		logger.info("{} tables exists", context.getTables().size());
		
		// clean
		DbCleaner.clean(newImportConfig());
		
		// assert zero
		JiemamyContext context2 = new JiemamyContext();
		assertThat(new DbImporter().importModel(context2, newImportConfig()), is(true));
		assertThat(context2.getDbObjects().size(), is(0));
	}
	
	/**
	 * VIEWをインポートしてみる。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test03_view() throws Exception {
		DbCleaner.clean(newImportConfig());
		
		SimpleDataType varchar32 = new SimpleDataType(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		SimpleDataType aiInteger = new SimpleDataType(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		JiemamyContext context = new JiemamyContext(SqlFacet.PROVIDER);
		SimpleJmMetadata meta = new SimpleJmMetadata();
		meta.setDialectClassName(MySqlDialect.class.getName());
		context.setMetadata(meta);
		
		{
			// FORMAT-OFF
			JmTable table = new JmTableBuilder("T_FOO")
					.with(new JmColumnBuilder("ID").type(aiInteger).build())
					.with(new JmColumnBuilder("NAME").type(varchar32).build())
					.with(new JmColumnBuilder("HOGE").type(new SimpleDataType(INTEGER)).build())
					.build();
			// FORMAT-ON
			table.store(JmPrimaryKeyConstraint.of(table.getColumn("ID")));
			context.store(table);
			
			JmView view = new JmView();
			view.setName("V_BAR");
			view.setDefinition(VIEW_DEFINITION);
			context.store(view);
		}
		
		File outFile = new File("target/testresult/MySqlDatabaseTest_test04.sql");
		SimpleSqlExportConfig config = new SimpleSqlExportConfig();
		config.setDataSetIndex(-1);
		config.setEmitDropStatements(false);
		config.setOutputFile(outFile);
		config.setOverwrite(true);
		
		new SqlExporter().exportModel(context, config);
		
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
		
		connection = null;
		try {
			DbImporter importer = new DbImporter();
			JiemamyContext imported = new JiemamyContext();
			boolean importModel = importer.importModel(imported, newImportConfig());
			assertThat(importModel, is(true));
			JmTable importedTable = imported.getTable("T_FOO");
			assertThat(importedTable.getName(), is("T_FOO"));
			
			// [DMYS-3]
//			JmView importedView = Iterables.getOnlyElement(imported.getViews());
//			assertThat(importedView.getName(), is("V_BAR"));
//			assertThat(importedView.getDefinition(), is(VIEW_DEFINITION));
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
	
	/**
	 * DBエンジン名をインポートしてみる。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test04_engine() throws Exception {
		DbCleaner.clean(newImportConfig());
		
		SimpleDataType varchar32 = new SimpleDataType(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		SimpleDataType aiInteger = new SimpleDataType(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		JiemamyContext context = new JiemamyContext(SqlFacet.PROVIDER);
		SimpleJmMetadata meta = new SimpleJmMetadata();
		meta.setDialectClassName(MySqlDialect.class.getName());
		context.setMetadata(meta);
		
		// FORMAT-OFF
		JmTable foo = new JmTableBuilder("T_FOO")
				.with(new JmColumnBuilder("ID").type(aiInteger).build())
				.with(new JmColumnBuilder("NAME").type(varchar32).build())
				.with(new JmColumnBuilder("HOGE").type(new SimpleDataType(INTEGER)).build())
				.build();
		// FORMAT-ON
		foo.putParam(MySqlParameterKeys.STORAGE_ENGINE, StandardEngine.InnoDB);
		foo.store(JmPrimaryKeyConstraint.of(foo.getColumn("ID")));
		context.store(foo);
		
		// FORMAT-OFF
		JmTable bar = new JmTableBuilder("T_BAR")
				.with(new JmColumnBuilder("ID").type(aiInteger).build())
				.with(new JmColumnBuilder("NAME").type(varchar32).build())
				.with(new JmColumnBuilder("FUGA").type(new SimpleDataType(INTEGER)).build())
				.build();
		// FORMAT-ON
		bar.store(JmPrimaryKeyConstraint.of(bar.getColumn("ID")));
		bar.putParam(MySqlParameterKeys.STORAGE_ENGINE, StandardEngine.MyISAM);
		context.store(bar);
		
		File outFile = new File("target/testresult/MySqlDatabaseTest_test03.sql");
		SimpleSqlExportConfig config = new SimpleSqlExportConfig();
		config.setDataSetIndex(-1);
		config.setEmitDropStatements(false);
		config.setOutputFile(outFile);
		config.setOverwrite(true);
		
		new SqlExporter().exportModel(context, config);
		
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
		
		connection = null;
		try {
			DbImporter importer = new DbImporter();
			JiemamyContext imported = new JiemamyContext();
			boolean importModel = importer.importModel(imported, newImportConfig());
			assertThat(importModel, is(true));
			assertThat(imported.getTables().size(), is(2));
			StorageEngineType fooEngine = imported.getTable("T_FOO").getParam(MySqlParameterKeys.STORAGE_ENGINE);
			assertThat(fooEngine, is((StorageEngineType) StandardEngine.InnoDB));
			StorageEngineType barEngine = imported.getTable("T_BAR").getParam(MySqlParameterKeys.STORAGE_ENGINE);
			assertThat(barEngine, is((StorageEngineType) StandardEngine.MyISAM));
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}
}
