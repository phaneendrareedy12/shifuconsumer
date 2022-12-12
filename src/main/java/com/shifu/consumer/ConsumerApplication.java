package com.shifu.consumer;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

@SpringBootApplication
@Log4j2
public class ConsumerApplication {

	private static final String MONGO_DB_URI_PROPERTY = "spring.data.mongodb.uri";
	private static final String FILE_PATH = "D:\\mongo_db_connection_shifu.txt";

	public static void main(String[] args) {
		String mongoDbConnstr = getMongoDBconnectionString();
		System.setProperty(MONGO_DB_URI_PROPERTY, mongoDbConnstr);
		SpringApplication.run(ConsumerApplication.class, args);
	}

	public static String getMongoDBconnectionString() {
		String connetionString = "";
		try {
			File file = new File(FILE_PATH);
			BufferedReader br = new BufferedReader(new FileReader(file));
			connetionString = br.readLine();
		} catch (FileNotFoundException e) {
			log.error("Mongodb connection string file is not found : ", e.getMessage());
		} catch (IOException e) {
			log.error("There is an exception while reading mongodb connection string from database : ", e.getMessage());
		}
		return connetionString;
	}

}
