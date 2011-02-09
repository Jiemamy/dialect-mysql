/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2011/01/26
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

import java.util.Collections;
import java.util.List;

import org.jiemamy.dialect.DefaultTokenResolver;
import org.jiemamy.dialect.mysql.internal.MySqlKeywords;
import org.jiemamy.dialect.mysql.parameter.MySqlParameterKeys;
import org.jiemamy.model.constraint.JmDeferrability.InitiallyCheckTime;
import org.jiemamy.model.datatype.DataType;
import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.model.sql.Token;

/**
 * TODO for daisuke
 * 
 * @version $Id$
 * @author daisuke
 */
public class MySqlTokenResolver extends DefaultTokenResolver {
	
	@Override
	public List<Token> resolve(Object value) {
		if (value instanceof InitiallyCheckTime) {
			return Collections.emptyList();
		}
		
		return super.resolve(value);
	}
	
	@Override
	protected List<Token> resolveType(DataType type) {
		List<Token> resolved = super.resolveType(type);
		
		Boolean unsigned = type.getParam(MySqlParameterKeys.UNSIGNED);
		if (unsigned != null && unsigned) {
			resolved.add(0, MySqlKeywords.UNSIGNED);
		}
		
		Boolean serial = type.getParam(TypeParameterKey.SERIAL);
		if (serial != null && serial) {
			resolved.add(MySqlKeywords.AUTO_INCREMENT);
		}
		
		return resolved;
	}
	
}
