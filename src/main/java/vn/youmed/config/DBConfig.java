package vn.youmed.config;

import io.vertx.core.json.JsonObject;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.IOException;

public class DBConfig {

	private static final String FILENAME = "/config.xml";

	public static JsonObject jsonConfig;

	public static JsonObject dbConfig() throws SAXException, IOException {
		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(ReadConfigDB.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			ReadConfigDB read = (ReadConfigDB) jaxbUnmarshaller.unmarshal(new File(FILENAME));

			String uri = read.getName() + "://" + read.getHost() + ":" + read.getPort();
			String db_name = read.getDb_name();

			jsonConfig.put("connection_string", uri);
			jsonConfig.put("db_name", db_name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonConfig;
	}
}
