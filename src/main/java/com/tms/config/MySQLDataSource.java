package com.tms.config;

//import java.util.Hashtable;
//
//import javax.naming.Context;
//import javax.naming.InitialContext;
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;

//@Configuration
//public class MySQLDataSource implements DataSourceConfig {
//	private InitialContext ctx = null;
//	private DataSource mySQLDataSource = null;
//
//	@Bean(destroyMethod = "")
//	@Override
//	public DataSource getDataSource() throws NamingException {
//		try {
//			ctx = getInitialContext();
//			mySQLDataSource = (DataSource) ctx.lookup("jdbc/MySQLDB");;
//		} catch (NamingException e) {
//			e.printStackTrace();
//		} finally {
//			ctx.close();
//		}
//		return mySQLDataSource;
//	}
//
//	@Override
//	public InitialContext getInitialContext() throws NamingException {
//			if (ctx == null) {
//				Hashtable<String, String> env = new Hashtable<>();
//				env.put(Context.INITIAL_CONTEXT_FACTORY, "weblogic.jndi.WLInitialContextFactory");
//				env.put(Context.PROVIDER_URL, "t3://localhost:7001");
//				ctx = new InitialContext(env);
//			
//			} 
//			return ctx;
//	}
//
//}
