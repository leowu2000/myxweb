package com.mmstart.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class DBUpgrade {
	private PlatformTransactionManager transactionManager;
	private JdbcTemplate jdbcTemplate;
	private String tablename = "db_version";

	/**
     *执行scriptDir目录下的脚本对数据库进行更新
     *@param scriptDir 脚本存放路径
     */
	public void upgrade(final String scriptDir) {
		//获取文件夹下所有文件名
		final String[] scripts = new File(scriptDir).list();
		//对文件名数组进行排序
		Arrays.sort(scripts);

		//调用getDBVersion获取当前数据库版本号
		final int dbVersion = getDBVersion(jdbcTemplate);
		//用事务管理器实例化事务模版
		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		//执行事务模版的execute方法,执行脚本
		tt.execute(new TransactionCallbackWithoutResult() {
			//实例化TransactionCallbackWithoutResult,实现doInTransactionWithoutResult方法,如果执行没有结果则回滚
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				//循环得到文件夹下脚本文件名称
				for (String script : scripts) {
					//获取此脚本文件的版本号
					int version = Integer.parseInt(script.substring(0, 3));
					//判断脚本文件的版本是否大于数据库当前版本
					if (version > dbVersion)
						try {
							//控制台输出log
							System.out.println("正在执行脚本：" + script);
							//执行脚本文件
							executeSQLScript(new FileInputStream(new File(scriptDir + script)));
							//将数据库版本设置为执行的脚本文件版本
							executeSQLScript("UPDATE " + tablename + " SET version=" + version);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (SQLException e) {
							e.printStackTrace();
						}
				}
			}
		});
	}

	/**
     *获取当前数据库版本号
     *@param jdbcTemplate jdbc模版
     *@return int dbVersion 数据库版本号
     */
	public int getDBVersion(JdbcTemplate jdbcTemplate) {
		int dbVersion = 0;
		try {
			//通过jdbc模版的queryForInt方法执行sql语句获取数据库版本号
			dbVersion = jdbcTemplate.queryForInt("SELECT max(version) FROM " + tablename);
		} catch (Exception e) {
			//出现异常则创建新的version表
			jdbcTemplate.execute("create table " + tablename + " (version int NOT NULL)");
			//插入值设置当前数据库版本为0
			jdbcTemplate.execute("insert into " + tablename + " values(0)");
		}
		return dbVersion;
	}

	/**
     *执行sql脚本
     *@param resource 脚本文件输入流
     */
	private void executeSQLScript(InputStream resource) throws IOException,
			SQLException {
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(resource, "UTF-8"));
			boolean done = false;
			while (!done) {
				StringBuilder command = new StringBuilder();
				while (true) {
					String line = in.readLine();
					if (line == null) {
						done = true;
						break;
					}
					// Ignore comments and blank lines.
					if (isSQLCommandPart(line)) {
						command.append(" ").append(line.trim());
					}
					if (line.trim().endsWith(";")) {
						break;
					}
				}
				if (!done && !command.toString().equals("")) {
					// Remove last semicolon when using Oracle or DB2 to prevent
					// "invalid character error"
					// if (DbConnectionManager.getDatabaseType() ==
					// DbConnectionManager.DatabaseType.oracle
					// || DbConnectionManager.getDatabaseType() ==
					// DbConnectionManager.DatabaseType.db2) {
					// command.deleteCharAt(command.length() - 1);
					// }
					command.deleteCharAt(command.length() - 1);
					jdbcTemplate.execute(command.toString());
				}
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void executeSQLScript(String sql) {
		jdbcTemplate.execute(sql);
	}

	private static boolean isSQLCommandPart(String line) {
		line = line.trim();
		if (line.equals("")) {
			return false;
		}
		// Check to see if the line is a comment. Valid comment types:
		// "//" is HSQLDB
		// "--" is DB2 and Postgres
		// "#" is MySQL
		// "REM" is Oracle
		// "/*" is SQLServer
		return !(line.startsWith("//") || line.startsWith("--") || line.startsWith("#") || line.startsWith("REM ")
				|| line.startsWith("/*") || line.startsWith("*"));
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
