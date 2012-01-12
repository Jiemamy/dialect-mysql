/*
 * Copyright 2007-2012 Jiemamy Project and the Others.
 * Created on 2009/02/25
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
package org.jiemamy.dialect.mysql.parameter;

import org.jiemamy.model.datatype.TypeParameterKey;
import org.jiemamy.model.parameter.Converter;
import org.jiemamy.model.parameter.Converters;
import org.jiemamy.model.table.TableParameterKey;

/**
 * MySQL用の各パラメータキー。
 * 
 * @author daisuke
 */
public final class MySqlParameterKeys {
	
	/** UNSIGNEDパラメータ用のキー */
	public static final TypeParameterKey<Boolean> UNSIGNED = new TypeParameterKey<Boolean>(Converters.BOOLEAN,
			"org.jiemamy.dialect.mysql.unsigned");
	
	/** CHARSETパラメータ用のキー */
	public static final TypeParameterKey<String> CHARSET = new TypeParameterKey<String>(Converters.STRING,
			"org.jiemamy.dialect.mysql.charset");
	
	/** ストレージエンジンパラメータ用のキー */
	public static final TableParameterKey<StorageEngineType> STORAGE_ENGINE = new TableParameterKey<StorageEngineType>(
			new Converter<StorageEngineType>() {
				
				public String toString(StorageEngineType obj) {
					return obj.toString();
				}
				
				public StorageEngineType valueOf(final String str) {
					try {
						return StandardEngine.valueOf(str);
					} catch (IllegalArgumentException e) {
						return new StorageEngineType() {
							
							@Override
							public String toString() {
								return str;
							}
						};
					}
				}
			}, "org.jiemamy.dialect.mysql.storageEngine");
	
	
	private MySqlParameterKeys() {
	}
}
