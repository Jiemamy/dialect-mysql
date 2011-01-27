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

import static org.jiemamy.model.datatype.DataTypeCategory.BIT;
import static org.jiemamy.model.datatype.DataTypeCategory.BLOB;
import static org.jiemamy.model.datatype.DataTypeCategory.CHARACTER;
import static org.jiemamy.model.datatype.DataTypeCategory.CLOB;
import static org.jiemamy.model.datatype.DataTypeCategory.DATE;
import static org.jiemamy.model.datatype.DataTypeCategory.DECIMAL;
import static org.jiemamy.model.datatype.DataTypeCategory.DOUBLE;
import static org.jiemamy.model.datatype.DataTypeCategory.FLOAT;
import static org.jiemamy.model.datatype.DataTypeCategory.INTEGER;
import static org.jiemamy.model.datatype.DataTypeCategory.NUMERIC;
import static org.jiemamy.model.datatype.DataTypeCategory.OTHER;
import static org.jiemamy.model.datatype.DataTypeCategory.REAL;
import static org.jiemamy.model.datatype.DataTypeCategory.SMALLINT;
import static org.jiemamy.model.datatype.DataTypeCategory.TIME;
import static org.jiemamy.model.datatype.DataTypeCategory.TIMESTAMP;
import static org.jiemamy.model.datatype.DataTypeCategory.VARCHAR;

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
import org.jiemamy.model.datatype.DefaultTypeReference;
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
		typeEntries.add(new Entry(new DefaultTypeReference(INTEGER), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(INTEGER, "MEDIUMINT"), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(INTEGER, "BIGINT"), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(SMALLINT), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(SMALLINT, "TINYINT"), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.OPTIONAL),
				new TypeParameterSpec(TypeParameterKey.SERIAL, Necessity.OPTIONAL),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(NUMERIC), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(DECIMAL), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(FLOAT), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(REAL), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(DOUBLE), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.PRECISION, Necessity.REQUIRED),
				new TypeParameterSpec(TypeParameterKey.SCALE, Necessity.REQUIRED),
				new TypeParameterSpec(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(BIT), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.REQUIRED)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(CHARACTER), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.REQUIRED)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(VARCHAR), Arrays.asList(
				new TypeParameterSpec(TypeParameterKey.SIZE, Necessity.REQUIRED)
		)));
		typeEntries.add(new Entry(new DefaultTypeReference(CLOB, "TEXT")));
		typeEntries.add(new Entry(new DefaultTypeReference(CLOB, "TINYTEXT")));
		typeEntries.add(new Entry(new DefaultTypeReference(CLOB, "MEDIUMTEXT")));
		typeEntries.add(new Entry(new DefaultTypeReference(CLOB, "LONGTEXT")));
		typeEntries.add(new Entry(new DefaultTypeReference(BLOB)));
		typeEntries.add(new Entry(new DefaultTypeReference(BLOB, "TINYBLOB")));
		typeEntries.add(new Entry(new DefaultTypeReference(BLOB, "MEDIUMBLOB")));
		typeEntries.add(new Entry(new DefaultTypeReference(BLOB, "LONGBLOB")));
		typeEntries.add(new Entry(new DefaultTypeReference(DATE)));
		typeEntries.add(new Entry(new DefaultTypeReference(TIME)));
		typeEntries.add(new Entry(new DefaultTypeReference(TIMESTAMP)));
		typeEntries.add(new Entry(new DefaultTypeReference(TIMESTAMP, "DATETIME")));
		typeEntries.add(new Entry(new DefaultTypeReference(INTEGER, "YEAR")));
		typeEntries.add(new Entry(new DefaultTypeReference(OTHER, "BINARY")));
		typeEntries.add(new Entry(new DefaultTypeReference(OTHER, "VARBINARY")));
		typeEntries.add(new Entry(new DefaultTypeReference(OTHER, "ENUM")));
		typeEntries.add(new Entry(new DefaultTypeReference(OTHER, "SET")));
		// FORMAT-ON
	}
	

	/**
	 * インスタンスを生成する。
	 */
	public MySqlDialect() {
		super("jdbc:mysql://localhost:3306/", typeEntries);
	}
	
	public DatabaseMetadataParser getDatabaseMetadataParser() {
		// TODO カスタマイズ
		return new DefaultDatabaseMetadataParser();
	}
	
	public String getName() {
		return "MySQL 5.0";
	}
	
	public SqlEmitter getSqlEmitter() {
		return new MySqlEmitter(new MySqlTokenResolver());
	}
	
	@Override
	public Validator getValidator() {
		CompositeValidator validator = (CompositeValidator) super.getValidator();
		validator.getValidators().add(new MySqlIdentifierValidator());
		return validator;
	}
}
