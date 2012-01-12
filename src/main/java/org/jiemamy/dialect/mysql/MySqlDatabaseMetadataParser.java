/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
 * Created on 2011/02/12
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

import org.jiemamy.dialect.DatabaseMetadataParser;
import org.jiemamy.dialect.DefaultDatabaseMetadataParser;
import org.jiemamy.dialect.DefaultForeignKeyImportVisitor;
import org.jiemamy.dialect.Dialect;

/**
 * MySQL用 {@link DatabaseMetadataParser} 実装クラス。
 * 
 * @version $Id$
 * @author daisuke
 */
public class MySqlDatabaseMetadataParser extends DefaultDatabaseMetadataParser {
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param dialect {@link Dialect}
	 * @throws IllegalArgumentException 引数に{@code null}を与えた場合
	 */
	public MySqlDatabaseMetadataParser(MySqlDialect dialect) {
		super(new MySqlDbObjectImportVisitor(dialect), new DefaultForeignKeyImportVisitor(dialect));
	}
}
