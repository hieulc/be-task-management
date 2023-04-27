package com.tms.config;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public interface DataSourceConfig {
	
	public DataSource getDataSource() throws NamingException;
	public InitialContext getInitialContext() throws NamingException;

}
