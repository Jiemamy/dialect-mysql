/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
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

import java.util.HashMap;
import java.util.List;

import com.google.common.collect.Lists;

import org.jiemamy.dialect.AbstractDialect;
import org.jiemamy.dialect.DatabaseMetadataParser;
import org.jiemamy.dialect.Necessity;
import org.jiemamy.dialect.SqlEmitter;
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
@SuppressWarnings("serial")
public class MySqlDialect extends AbstractDialect {
	
	private static List<Entry> typeEntries = Lists.newArrayList();
	
	static {
		// FORMAT-OFF
		// CHECKSTYLE:OFF
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.OPTIONAL);
						put(TypeParameterKey.SERIAL, Necessity.OPTIONAL);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER, "MEDIUMINT"),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.OPTIONAL);
						put(TypeParameterKey.SERIAL, Necessity.OPTIONAL);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(INTEGER, "BIGINT"),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.OPTIONAL);
						put(TypeParameterKey.SERIAL, Necessity.OPTIONAL);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(SMALLINT),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.OPTIONAL);
						put(TypeParameterKey.SERIAL, Necessity.OPTIONAL);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(SMALLINT, "TINYINT"),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.OPTIONAL);
						put(TypeParameterKey.SERIAL, Necessity.OPTIONAL);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(NUMERIC),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.PRECISION, Necessity.REQUIRED);
						put(TypeParameterKey.SCALE, Necessity.REQUIRED);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(DECIMAL),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.PRECISION, Necessity.REQUIRED);
						put(TypeParameterKey.SCALE, Necessity.REQUIRED);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(FLOAT),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.PRECISION, Necessity.REQUIRED);
						put(TypeParameterKey.SCALE, Necessity.REQUIRED);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(REAL),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.PRECISION, Necessity.REQUIRED);
						put(TypeParameterKey.SCALE, Necessity.REQUIRED);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(DOUBLE),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.PRECISION, Necessity.REQUIRED);
						put(TypeParameterKey.SCALE, Necessity.REQUIRED);
						put(MySqlParameterKeys.UNSIGNED, Necessity.OPTIONAL);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(BIT),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.REQUIRED);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(CHARACTER),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.REQUIRED);
				}}));
		typeEntries.add(new Entry(new SimpleRawTypeDescriptor(VARCHAR),
				new HashMap<TypeParameterKey<?>, Necessity>() {{
						put(TypeParameterKey.SIZE, Necessity.REQUIRED);
				}}));
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
		// CHECKSTYLE:ON
		// FORMAT-ON
	}
	
	
	/**
	 * インスタンスを生成する。
	 */
	public MySqlDialect() {
		super("jdbc:mysql://localhost:3306/", typeEntries);
	}
	
	public DatabaseMetadataParser getDatabaseMetadataParser() {
		return new MySqlDatabaseMetadataParser(this);
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
