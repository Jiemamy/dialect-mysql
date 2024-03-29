/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
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
package org.jiemamy.dialect.mysql.internal;

import org.jiemamy.model.sql.Keyword;

/**
 * MySQL用の（トークンとしての）キーワード。
 * 
 * @version $Id$
 * @author daisuke
 */
public final class MySqlKeywords {
	
	/** UNSIGNED */
	public static final Keyword UNSIGNED = Keyword.of("UNSIGNED");
	
	/** AUTO_INCREMENT */
	public static final Keyword AUTO_INCREMENT = Keyword.of("AUTO_INCREMENT");
	
	
	private MySqlKeywords() {
	}
	
}
