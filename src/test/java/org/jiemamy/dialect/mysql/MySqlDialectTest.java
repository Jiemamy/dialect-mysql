/*
 * Copyright 2007-2009 Jiemamy Project and the Others.
 * Created on 2009/02/16
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

import javax.sql.DataSource;
import javax.sql.XADataSource;

import org.junit.Before;

import org.jiemamy.DefaultContextMetadata;
import org.jiemamy.JiemamyContext;
import org.jiemamy.dialect.AbstractDialectTest;
import org.jiemamy.dialect.Dialect;

/**
 * {@link MySqlDialect}のテストクラス。
 * 
 * @author daisuke
 */
public class MySqlDialectTest extends AbstractDialectTest {
	
	/** {@link XADataSource} */
	public XADataSource xaDataSource;
	
	/** {@link DataSource} */
	public DataSource dataSource;
	
	private Dialect dialect;
	
	private JiemamyContext context;
	

	/**
	 * テストを初期化する。
	 * 
	 * @throws Exception 例外が発生した場合
	 */
	@Before
	public void setUp() throws Exception {
		context = new JiemamyContext();
		DefaultContextMetadata meta = new DefaultContextMetadata();
		meta.setDialectClassName(MySqlDialect.class.getName());
		context.setMetadata(meta);
		dialect = context.findDialect();
	}
	
	@Override
	protected Dialect getInstance() {
		return dialect;
	}
}
