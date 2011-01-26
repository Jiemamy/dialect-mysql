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

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.commons.lang.StringUtils;

import org.jiemamy.JiemamyContext;
import org.jiemamy.dialect.DefaultSqlEmitter;
import org.jiemamy.dialect.SqlEmitter;
import org.jiemamy.dialect.TokenResolver;
import org.jiemamy.dialect.mysql.internal.MySqlIdentifier;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.dialect.mysql.parameter.StorageEngineType;
import org.jiemamy.model.DatabaseObjectModel;
import org.jiemamy.model.sql.DefaultSqlStatement;
import org.jiemamy.model.sql.Identifier;
import org.jiemamy.model.sql.Keyword;
import org.jiemamy.model.sql.Separator;
import org.jiemamy.model.sql.SqlStatement;
import org.jiemamy.model.sql.Token;
import org.jiemamy.model.table.TableModel;

/**
 * MySQL用の{@link SqlEmitter}実装クラス。
 * 
 * @author daisuke
 */
public class MySqlEmitter extends DefaultSqlEmitter {
	
	/**
	 * インスタンスを生成する。
	 */
	public MySqlEmitter() {
		this(new MySqlTokenResolver());
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param tokenResolver
	 */
	protected MySqlEmitter(TokenResolver tokenResolver) {
		super(tokenResolver);
	}
	
	@Override
	protected SqlStatement emitCreateStatement(JiemamyContext context, DatabaseObjectModel dom) {
		DefaultSqlStatement statement = (DefaultSqlStatement) super.emitCreateStatement(context, dom);
		
		List<Token> tokens = statement.toTokens();
		tokens = convertIdentifierToMySqlIdentifier(tokens);
		
		if (dom instanceof TableModel) {
			TableModel tableModel = (TableModel) dom;
			StorageEngineType engineType = tableModel.getParam(MySqlParameterKeys.STORAGE_ENGINE);
			if (engineType != null && StringUtils.isEmpty(engineType.toString()) == false) {
				String engineName = engineType.toString();
				tokens.add(Keyword.of("ENGINE"));
				tokens.add(Separator.EQUAL);
				tokens.add(Keyword.of(engineName));
			}
		}
		
		return new DefaultSqlStatement(tokens);
	}
	
	@Override
	protected SqlStatement emitDropEntityStatement(DatabaseObjectModel dom) {
		DefaultSqlStatement stmt = (DefaultSqlStatement) super.emitDropEntityStatement(dom);
		List<Token> tokens = stmt.toTokens();
		tokens = convertIdentifierToMySqlIdentifier(tokens);
		tokens.addAll(2, Arrays.asList((Token) Keyword.of("IF"), Keyword.of("EXISTS")));
		return new DefaultSqlStatement(tokens);
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
