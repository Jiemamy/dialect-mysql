/*
 * Copyright 2007-2011 Jiemamy Project and the Others.
 * Created on 2011/01/31
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
package org.jiemamy.test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.jiemamy.composer.importer.SimpleDbImportConfig;
import org.jiemamy.dialect.mysql.MySqlDialect;

/**
 * TODO for daisuke
 * 
 * @version $Id$
 * @author daisuke
 */
public abstract class MySqlDatabaseTest extends AbstractDatabaseTest {
	
	@Override
	protected String getPropertiesFilePath(String hostName) {
		if (hostName.equals("griffon.jiemamy.org")) {
			return "/mysql_griffon.properties";
		}
		return "/mysql_local.properties";
	}
	
	protected SimpleDbImportConfig newImportConfig() {
		SimpleDbImportConfig config = new SimpleDbImportConfig();
		config.setDialect(new MySqlDialect());
		try {
			config.setDriverJarPaths(new URL[] {
				new File(getJarPath()).toURL()
			});
		} catch (MalformedURLException e) {
			throw new Error(e);
		}
		config.setDriverClassName(getDriverClassName());
		config.setUri(getConnectionUri());
		config.setUsername(getUsername());
		config.setPassword(getPassword());
		return config;
	}
}
