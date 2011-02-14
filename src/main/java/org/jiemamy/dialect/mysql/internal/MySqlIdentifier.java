/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2009/02/12
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

import org.jiemamy.model.sql.Identifier;

/**
 * MySQL用識別子クラス。
 * 
 * <p>このクラスはイミュータブルである。</p>
 * 
 * @author daisuke
 */
public class MySqlIdentifier extends Identifier {
	
	/**
	 * 識別子を返す。
	 * 
	 * @param string 識別子文字列
	 * @return　識別子
	 */
	public static MySqlIdentifier of(String string) {
		return new MySqlIdentifier(string);
	}
	
	/**
	 * インスタンスを生成する。
	 * 
	 * @param identifier 識別子文字列
	 */
	protected MySqlIdentifier(String identifier) {
		super(identifier);
	}
	
	@Override
	public String toString() {
		return "`" + super.toString() + "`";
	}
}
