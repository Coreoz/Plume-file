package com.coreoz.plume.file.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.h2.jdbcx.JdbcDataSource;

import com.coreoz.plume.db.querydsl.generation.IdBeanSerializer;
import com.google.common.base.Throwables;
import com.querydsl.codegen.EntityType;
import com.querydsl.sql.Configuration;
import com.querydsl.sql.SQLTemplates;
import com.querydsl.sql.codegen.DefaultNamingStrategy;
import com.querydsl.sql.codegen.MetaDataExporter;
import com.querydsl.sql.types.JSR310LocalDateTimeType;
import com.querydsl.sql.types.JSR310LocalDateType;
import com.querydsl.sql.types.JSR310LocalTimeType;
import com.querydsl.sql.types.JSR310ZonedDateTimeType;
import com.querydsl.sql.types.Type;

public class QuerydslGenerator {

	private static Connection createDB() throws SQLException, IOException {
		
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:./test");
		ds.setUser("sa");
		ds.setPassword("sa");
		Connection conn = ds.getConnection();
		
		List<String> lines = Files.readAllLines(Paths.get("./sql/setup-mysql.sql"), StandardCharsets.UTF_8);
		
		for(String sql : Arrays.asList(String.join("", lines).split(";"))) {
			conn.createStatement().execute(sql);
		}
		
		return conn;
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		Configuration configuration = new Configuration(SQLTemplates.DEFAULT);
//		configuration.register(classType(JSR310InstantType.class));
		configuration.register(classType(JSR310LocalDateTimeType.class));
		configuration.register(classType(JSR310LocalDateType.class));
		configuration.register(classType(JSR310LocalTimeType.class));
//		configuration.register(classType(JSR310OffsetDateTimeType.class));
//		configuration.register(classType(JSR310OffsetTimeType.class));
		configuration.register(classType(JSR310ZonedDateTimeType.class));
		configuration.registerType("BLOB", byte[].class);
		
		MetaDataExporter exporter = new MetaDataExporter();
		exporter.setPackageName("com.coreoz.plume.file.db.generated");
		exporter.setTargetFolder(new File("src/main/java"));
		exporter.setBeanSerializer(new IdBeanSerializer());
		exporter.setColumnAnnotations(true);
		exporter.setConfiguration(configuration);
		exporter.setNamingStrategy(new DefaultNamingStrategy() {
			@Override
			public String getClassName(String tableName) {
				if(tableName.toLowerCase().startsWith("plm_")) {
					return super.getClassName(tableName.substring(4));
				}
				return tableName;
			}

			@Override
			public String getDefaultVariableName(EntityType entityType) {
				String variableName = getClassName(entityType.getData().get("table").toString());
				return variableName.substring(0, 1).toLowerCase() + variableName.substring(1);
			}
		});

		
		exporter.export(createDB().getMetaData());
		
	}

	static Type<?> classType(Class<?> classType) {
		try {
			return (Type<?>) classType.newInstance();
		} catch (Exception e) {
			throw Throwables.propagate(e);
		}
	}

}
