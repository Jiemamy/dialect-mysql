/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2008/07/12
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

import static org.jiemamy.model.datatype.RawTypeCategory.BIT;
import static org.jiemamy.model.datatype.RawTypeCategory.BLOB;
import static org.jiemamy.model.datatype.RawTypeCategory.CHARACTER;
import static org.jiemamy.model.datatype.RawTypeCategory.CLOB;
import static org.jiemamy.model.datatype.RawTypeCategory.DATE;
import static org.jiemamy.model.datatype.RawTypeCategory.DECIMAL;
import static org.jiemamy.model.datatype.RawTypeCategory.DOUBLE;
import static org.jiemamy.model.datatype.RawTypeCategory.FLOAT;
import static org.jiemamy.model.datatype.RawTypeCategory.INTEGER;
import static org.jiemamy.model.datatype.RawTypeCategory.NUMERIC;
import static org.jiemamy.model.datatype.RawTypeCategory.OTHER;
import static org.jiemamy.model.datatype.RawTypeCategory.REAL;
import static org.jiemamy.model.datatype.RawTypeCategory.SMALLINT;
import static org.jiemamy.model.datatype.RawTypeCategory.TIME;
import static org.jiemamy.model.datatype.RawTypeCategory.TIMESTAMP;
import static org.jiemamy.model.datatype.RawTypeCategory.VARCHAR;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.jiemamy.dialect.AbstractDialect;
import org.jiemamy.dialect.DatabaseMetadataParser;
import org.jiemamy.dialect.DefaultDatabaseMetadataParser;
import org.jiemamy.dialect.SqlEmitter;
import org.jiemamy.dialect.TypeParameterSpec;
import org.jiemamy.dialect.TypeParameterSpec.Necessity;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.model.datatype.SimpleRawTypeDescriptor;
import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.validator.CompositeValidator;
import org.jiemamy.validator.Validator;

/**
 * MySQLに対するSQL方言実装クラス。
 * 
 * @author daisuke
 */
public class MySqlDialect extends AbstractDialect {
	
	private static List<Entry> typeEntries = Lists.newArrayList();
	
	static {
		// FORMAT-OFF
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER, "MEDIUMINT"), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER, "BIGINT"), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(SMALLINT), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(SMALLINT, "TINYINT"), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(NUMERIC), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(DECIMAL), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(FLOAT), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(REAL), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(DOUBLE), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(BIT), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.REQUIRED)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(CHARACTER), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.REQUIRED)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(VARCHAR), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.REQUIRED)
		)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(CLOB, "TEXT")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(CLOB, "TINYTEXT")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(CLOB, "MEDIUMTEXT")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(CLOB, "LONGTEXT")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(BLOB)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(BLOB, "TINYBLOB")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(BLOB, "MEDIUMBLOB")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(BLOB, "LONGBLOB")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(DATE)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(TIME)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(TIMESTAMP)));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(TIMESTAMP, "DATETIME")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER, "YEAR")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(OTHER, "BINARY")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(OTHER, "VARBINARY")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(OTHER, "ENUM")));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(OTHER, "SET")));
		// FORMAT-ON
	}
	

	/**
	 * インスタンスを生成する。
	 */
	public MySqlDialect() {
		super("jdbc:mysql://localhost:3306/", typeEntries);
	}
	
	public DatabaseMetadataParser getDatabaseMetadataParser() {
		// TODO カスタマイズ for [DMYS-2]
		return new DefaultDatabaseMetadataParser(this);
	}
	
	public String getName() {
		return "MySQL 5.0";
	}
	
	public SqlEmitter getSqlEmitter() {
		return new MySqlEmitter(this);
	}
	
	@Override
	public Validator getValidator() {
		CompositeValidator validator = (CompositeValidator) super.getValidator();
		validator.getValidators().add(new MySqlIdentifierValidator());
		return validator;
	}
}
