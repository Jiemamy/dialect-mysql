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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.commons.lang.StringUtils;

import org.jiemamy.JiemamyContext;
import org.jiemamy.dddbase.UUIDEntityRef;
import org.jiemamy.dialect.DefaultSqlEmitter;
import org.jiemamy.dialect.Dialect;
import org.jiemamy.dialect.SqlEmitter;
import org.jiemamy.dialect.TokenResolver;
import org.jiemamy.dialect.mysql.internal.MySqlIdentifier;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StorageEngineType;
import org.jiemamy.model.DbObject;
import org.jiemamy.model.column.JmColumn;
import org.jiemamy.model.constraint.JmNotNullConstraint;
import org.jiemamy.model.datatype.LiteralType;
import org.jiemamy.model.sql.Identifier;
import org.jiemamy.model.sql.Keyword;
import org.jiemamy.model.sql.Literal;
import org.jiemamy.model.sql.Separator;
import org.jiemamy.model.sql.SimpleSqlStatement;
import org.jiemamy.model.sql.SqlStatement;
import org.jiemamy.model.sql.Token;
import org.jiemamy.model.table.JmTable;

/**
 * MySQL用の{@link SqlEmitter}実装クラス。
 * 
 * @author daisuke
 */
public class MySqlEmitter extends DefaultSqlEmitter {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param dialect {@link Dialect}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public MySqlEmitter(Dialect dialect) {
		this(dialect, new MySqlTokenResolver());
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param dialect {@link Dialect}
	 * @param tokenResolver {@link TokenResolver}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	protected MySqlEmitter(Dialect dialect, TokenResolver tokenResolver) {
		super(dialect, tokenResolver);
	}
	
	@Override
	protected List<Token> emitColumn(JiemamyContext context, JmTable table, JmColumn column, TokenResolver tokenResolver) {
		List<Token> tokens = Lists.newArrayList();
		tokens.add(Identifier.of(column.getName()));
		tokens.addAll(tokenResolver.resolve(column.getDataType()));
		
		UUIDEntityRef<? extends JmColumn> ref = column.toReference();
		JmNotNullConstraint nn = table.getNotNullConstraintFor(ref);
		if (nn == null) {
			if (column.getDataType().getRawTypeDescriptor().getTypeName().equalsIgnoreCase("TIMESTAMP")) {
				tokens.add(Keyword.NULL);
			}
		} else {
			if (StringUtils.isEmpty(nn.getName()) == false) {
				tokens.add(Keyword.CONSTRAINT);
				tokens.add(Identifier.of(nn.getName()));
			}
			tokens.add(Keyword.NOT);
			tokens.add(Keyword.NULL);
		}
		
		if (StringUtils.isEmpty(column.getDefaultValue()) == false) {
			tokens.add(Keyword.DEFAULT);
			tokens.add(Literal.of(column.getDefaultValue(), LiteralType.FRAGMENT));
		}
		
		return tokens;
	}
	
	@Override
	protected SqlStatement emitCreateDbObjectStatement(JiemamyContext context, DbObject dbObject) {
		SimpleSqlStatement statement = (SimpleSqlStatement) super.emitCreateDbObjectStatement(context, dbObject);
		
		List<Token> tokens = statement.toTokens();
		tokens = convertIdentifierToMySqlIdentifier(tokens);
		
		if (dbObject instanceof JmTable) {
			JmTable table = (JmTable) dbObject;
			StorageEngineType engineType = table.getParam(MySqlParameterKeys.STORAGE_ENGINE);
			if (engineType != null && StringUtils.isEmpty(engineType.toString()) == false) {
				String engineName = engineType.toString();
				// FORMAT-OFF
				tokens.addAll(tokens.size() - 1, Arrays.asList(
						Keyword.of("ENGINE"),
						Separator.EQUAL,
						Keyword.of(engineName)
				));
				// FORMAT-ON
			}
		}
		
		return new SimpleSqlStatement(tokens);
	}
	
	@Override
	protected SqlStatement emitDropDbObjectStatement(DbObject dbObject) {
		SimpleSqlStatement stmt = (SimpleSqlStatement) super.emitDropDbObjectStatement(dbObject);
		List<Token> tokens = stmt.toTokens();
		tokens = convertIdentifierToMySqlIdentifier(tokens);
		tokens.addAll(2, Arrays.asList((Token) Keyword.of("IF"), Keyword.of("EXISTS")));
		return new SimpleSqlStatement(tokens);
	}
	
	private List<Token> convertIdentifierToMySqlIdentifier(List<Token> tokens) {
		List<Token> result = Lists.newArrayListWithCapacity(tokens.size());
		for (Token token : tokens) {
			if (token instanceof Identifier) {
				Identifier identifier = (Identifier) token;
				result.add(MySqlIdentifier.of(identifier.toString()));
			} else {
				result.add(token);
			}
		}
		return result;
	}
}
