package com.automation.selenium.cucumber.core.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class DBUtil {

	private static HashMap<String, SqlSessionFactory> sessionMap;

	private static SqlSessionFactory getSessionFactory(String config) {
		if (sessionMap == null)
			sessionMap = new HashMap<String, SqlSessionFactory>();

		if (!sessionMap.containsKey(config)) {
			InputStream inputStream = null;
			try {
				inputStream = Resources.getResourceAsStream(config);
			} catch (IOException e) {
				e.printStackTrace();
			}
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder()
					.build(inputStream);
			sessionMap.put(config, sqlSessionFactory);
		}

		return sessionMap.get(config);
	}

	public static List<?> getItems(String config, String select) {

		SqlSession session = getSessionFactory(config).openSession();
		List<?> items = session.selectList(select);
		session.close();
         
		return items;
	}

	public static void deleteItems(String config, String select, int id) {

		SqlSession session = getSessionFactory(config).openSession();
		// Delete operation
		session.delete(select, id);
		session.commit();
		session.close();
		
	}

	@SuppressWarnings("unchecked")
	public static List<Integer> getInts(String config, String select) {
		return (List<Integer>) getItems(config, select);
	}

	@SuppressWarnings("unchecked")
	public static List<String> getStrings(String config, String select) {
		return (List<String>) getItems(config, select);
	}

	public static List<HashMap<String, Object>> getMaps(String config,
			String select) {
		return (List<HashMap<String, Object>>) getMaps(config, select);
	}
}
