/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
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
import org.jiemamy.composer.exporter.SimpleSqlExportConfig;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StandardEngine;
import org.jiemamy.model.column.JmColumnBuilder;
import org.jiemamy.model.column.JmColumn;
import org.jiemamy.model.constraint.JmPrimaryKeyConstraint;
import org.jiemamy.model.datatype.RawTypeCategory;
import org.jiemamy.model.datatype.RawTypeDescriptor;
import org.jiemamy.model.datatype.SimpleDataType;
import org.jiemamy.model.datatype.SimpleRawTypeDescriptor;
import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.model.sql.SqlStatement;
import org.jiemamy.model.table.JmTableBuilder;
import org.jiemamy.model.table.JmTable;

/**
 * {@link MySqlEmitter}のテストクラス。
 * 
 * @author daisuke
 */
public class MySqlEmitterTest {
	
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(MySqlEmitterTest.class);
	
	private static final RawTypeDescriptor INTEGER = new SimpleRawTypeDescriptor(RawTypeCategory.INTEGER, "INTEGER",
			"int4");
	
	private static final RawTypeDescriptor VARCHAR = new SimpleRawTypeDescriptor(RawTypeCategory.VARCHAR);
	
	private static final RawTypeDescriptor TIMESTAMP = new SimpleRawTypeDescriptor(RawTypeCategory.TIMESTAMP);
	
	private MySqlEmitter emitter;
	
	private SimpleSqlExportConfig config;
	
	private JiemamyContext context;
	
	
	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	public void setUp() throws Exception {
		emitter = new MySqlEmitter(new MySqlDialect());
		
		config = new SimpleSqlExportConfig();
		config.setDataSetIndex(-1);
		config.setEmitCreateSchema(true);
		config.setEmitDropStatements(true);
		
		context = new JiemamyContext(SqlFacet.PROVIDER);
	}
	
	@Test
	@SuppressWarnings("javadoc")
	public void test_DEFAULT句がNULL句の後() {
		SimpleDataType aiInteger = new SimpleDataType(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		SimpleDataType varchar32 = new SimpleDataType(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		SimpleDataType timestamp = new SimpleDataType(TIMESTAMP);
		
		JmColumn id = new JmColumnBuilder("ID").type(aiInteger).build();
		
		JmColumn hoge = new JmColumnBuilder("HOGE").type(timestamp).build();
		hoge.setDefaultValue("'2011-10-27 10:53:59'");
		
		JmPrimaryKeyConstraint pk = JmPrimaryKeyConstraint.of(id);
		
		// FORMAT-OFF
		JmTable table = new JmTableBuilder("T_FOO")
				.with(id)
				.with(new JmColumnBuilder("NAME").type(varchar32).build())
				.with(hoge)
				.with(pk)
				.build();
		// FORMAT-ON
		context.add(table);
		
		List<SqlStatement> statements = emitter.emit(context, config);
		assertThat(statements.size(), is(2));
		assertThat(statements.get(0).toString(), is("DROP TABLE IF EXISTS `T_FOO`;"));
		assertThat(
				statements.get(1).toString(),
				is("CREATE TABLE `T_FOO`(`ID` INTEGER AUTO_INCREMENT, `NAME` VARCHAR(32), `HOGE` TIMESTAMP NULL DEFAULT '2011-10-27 10:53:59', PRIMARY KEY(`ID`));"));
	}
	
	@Test
	@SuppressWarnings("javadoc")
	public void test_TIMESTAMPにはNULLをつける() {
		SimpleDataType aiInteger = new SimpleDataType(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		SimpleDataType varchar32 = new SimpleDataType(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		SimpleDataType timestamp = new SimpleDataType(TIMESTAMP);
		
		// FORMAT-OFF
		JmTable table = new JmTableBuilder("T_FOO")
				.with(new JmColumnBuilder("ID").type(aiInteger).build())
				.with(new JmColumnBuilder("NAME").type(varchar32).build())
				.with(new JmColumnBuilder("HOGE").type(timestamp).build())
				.build();
		// FORMAT-ON
		context.add(table);
		
		List<SqlStatement> statements = emitter.emit(context, config);
		assertThat(statements.size(), is(2));
		assertThat(statements.get(0).toString(), is("DROP TABLE IF EXISTS `T_FOO`;"));
		assertThat(statements.get(1).toString(),
				is("CREATE TABLE `T_FOO`(`ID` INTEGER AUTO_INCREMENT, `NAME` VARCHAR(32), `HOGE` TIMESTAMP NULL);"));
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
		SimpleDataType varchar32 = new SimpleDataType(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		SimpleDataType aiInteger = new SimpleDataType(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		// FORMAT-OFF
		JmTable table = new JmTableBuilder("T_FOO")
				.with(new JmColumnBuilder("ID").type(aiInteger).build())
				.with(new JmColumnBuilder("NAME").type(varchar32).build())
				.with(new JmColumnBuilder("HOGE").type(new SimpleDataType(INTEGER)).build())
				.build();
		// FORMAT-ON
		context.add(table);
		
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
		SimpleDataType varchar32 = new SimpleDataType(VARCHAR);
		varchar32.putParam(TypeParameterKey.SIZE, 32);
		
		SimpleDataType aiInteger = new SimpleDataType(INTEGER);
		aiInteger.putParam(TypeParameterKey.SERIAL, true);
		
		// FORMAT-OFF
		JmTable table = new JmTableBuilder("T_FOO")
				.with(new JmColumnBuilder("ID").type(aiInteger).build())
				.with(new JmColumnBuilder("NAME").type(varchar32).build())
				.with(new JmColumnBuilder("HOGE").type(new SimpleDataType(INTEGER)).build())
				.build();
		// FORMAT-ON
		table.putParam(MySqlParameterKeys.STORAGE_ENGINE, StandardEngine.InnoDB);
		context.add(table);
		
		List<SqlStatement> statements = emitter.emit(context, config);
		assertThat(statements.size(), is(2));
		assertThat(statements.get(0).toString(), is("DROP TABLE IF EXISTS `T_FOO`;"));
		assertThat(
				statements.get(1).toString(),
				is("CREATE TABLE `T_FOO`(`ID` INTEGER AUTO_INCREMENT, `NAME` VARCHAR(32), `HOGE` INTEGER)ENGINE=InnoDB;"));
	}
}
