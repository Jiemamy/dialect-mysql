/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/02/24
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
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jiemamy.JiemamyContext;
import org.jiemamy.SqlFacet;
import org.jiemamy.composer.exporter.DefaultSqlExportConfig;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StandardEngine;
import org.jiemamy.model.column.Column;
import org.jiemamy.model.datatype.DataTypeCategory;
import org.jiemamy.model.datatype.DefaultTypeReference;
import org.jiemamy.model.datatype.DefaultTypeVariant;
import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.model.datatype.TypeReference;
import org.jiemamy.model.sql.SqlStatement;
import org.jiemamy.model.table.DefaultTableModel;
import org.jiemamy.model.table.Table;

/**
 * {@link MySqlEmitter}のテストクラス。
 * 
 * @author daisuke
 */
public class MySqlEmitterTest {
	
	private static Logger logger = LoggerFactory.getLogger(MySqlEmitterTest.class);
	
	private static final TypeReference INTEGER = new DefaultTypeReference(DataTypeCategory.INTEGER, "INTEGER", "int4");
	
	private static final TypeReference VARCHAR = new DefaultTypeReference(DataTypeCategory.VARCHAR);
	
	private MySqlEmitter emitter;
	
	private DefaultSqlExportConfig config;
	
	private JiemamyContext context;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	public void setUp() throws Exception {
		emitter = new MySqlEmitter();
		
		config = new DefaultSqlExportConfig();
		config.setDataSetIndex(-1);
		config.setEmitCreateSchema(true);
		config.setEmitDropStatements(true);
		
		context = new JiemamyContext(SqlFacet.PROVIDER);
	}
	
	/**
	 * 空のcontextをemitしても文は生成されない。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test01_空のcontextをemitしても文は生成されない() throws Exception {
		List<SqlStatement> statements = emitter.emit(context, config);
		assertThat(statements.size(), is(0));
	}
	
	/**
	 * 単純なテーブルを1つemitして確認。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test02_単純なテーブルを1つemitして確認() throws Exception {
		DefaultTypeVariant varchar32 = new DefaultTypeVariant(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		DefaultTypeVariant aiInteger = new DefaultTypeVariant(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		// FORMAT-OFF
		DefaultTableModel table = new Table("T_FOO")
				.with(new Column("ID").whoseTypeIs(aiInteger).build())
				.with(new Column("NAME").whoseTypeIs(varchar32).build())
				.with(new Column("HOGE").whoseTypeIs(new DefaultTypeVariant(INTEGER)).build())
				.build();
		// FORMAT-ON
		context.store(table);
		
		List<SqlStatement> statements = emitter.emit(context, config);
		assertThat(statements.size(), is(2));
		assertThat(statements.get(0).toString(), is("DROP TABLE IF EXISTS `T_FOO`;"));
		assertThat(statements.get(1).toString(),
				is("CREATE TABLE `T_FOO`(`ID` INTEGER AUTO_INCREMENT, `NAME` VARCHAR(32), `HOGE` INTEGER);"));
	}
	
	/**
	 * engine指定付の単純なテーブルを1つemitして確認。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Test
	public void test03_engine指定付の単純なテーブルを1つemitして確認() throws Exception {
		DefaultTypeVariant varchar32 = new DefaultTypeVariant(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		DefaultTypeVariant aiInteger = new DefaultTypeVariant(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		// FORMAT-OFF
		DefaultTableModel table = new Table("T_FOO")
				.with(new Column("ID").whoseTypeIs(aiInteger).build())
				.with(new Column("NAME").whoseTypeIs(varchar32).build())
				.with(new Column("HOGE").whoseTypeIs(new DefaultTypeVariant(INTEGER)).build())
				.build();
		// FORMAT-ON
		table.putParam(MySqlParameterKeys.STORAGE_ENGINE, StandardEngine.InnoDB);
		context.store(table);
		
		List<SqlStatement> statements = emitter.emit(context, config);
		assertThat(statements.size(), is(2));
		assertThat(statements.get(0).toString(), is("DROP TABLE IF EXISTS `T_FOO`;"));
		assertThat(
				statements.get(1).toString(),
				is("CREATE TABLE `T_FOO`(`ID` INTEGER AUTO_INCREMENT, `NAME` VARCHAR(32), `HOGE` INTEGER)ENGINE=InnoDB;"));
	}
}
