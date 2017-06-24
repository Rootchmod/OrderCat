package com.myjo.ordercat.native_messaging.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.myjo.ordercat.config.OrderCatConfig;
import com.myjo.ordercat.handle.AccountCheck;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;


public class Main {


    //private static String TEMP_PATH="/Users/lee5hx/src/myjo/OrderCat/src/test/resources/test/";


	private static String TEMP_PATH="E:\\OrderCat\\temp\\";


	private static final Logger Logger = LogManager.getLogger(Main.class);


	public static void main(String[] args) throws Exception {

		// Read message
		String requestJson = readMessage(System.in);

		//Logger.error(requestJson);

		JSONObject object = JSON.parseObject(requestJson);

		File file = new File(TEMP_PATH+"ajax_guess_mail_no_request.json");

		FileUtils.writeStringToFile(file,requestJson,"UTF-8");



//		ObjectMapper mapper = new ObjectMapper();
//		NativeRequest request = mapper.readValue(requestJson, NativeRequest.class);
//
//
//		// Process request...
//		NativeResponse response = new NativeResponse();
//		response.setMessage("Hello, " + request.getMessage() + "!");
//
//		// Send response message back
//		String responseJson = mapper.writeValueAsString(response);

		sendMessage(requestJson);

		System.exit(0);
	}

	private static String readMessage(InputStream in) throws IOException {
		byte[] b = new byte[4];
		in.read(b); // Read the size of message

		int size = getInt(b);

		if (size == 0) {
			throw new InterruptedIOException("Blocked communication");
		}

		b = new byte[size];
		in.read(b);

		return new String(b, "UTF-8");
	}

	private static void sendMessage(String message) throws IOException {
		System.out.write(getBytes(message.length()));
		System.out.write(message.getBytes("UTF-8"));
		System.out.flush();
	}

	public static int getInt(byte[] bytes) {
		return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000 | (bytes[1] << 8) & 0x0000ff00
				| (bytes[0] << 0) & 0x000000ff;
	}

	public static byte[] getBytes(int length) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (length & 0xFF);
		bytes[1] = (byte) ((length >> 8) & 0xFF);
		bytes[2] = (byte) ((length >> 16) & 0xFF);
		bytes[3] = (byte) ((length >> 24) & 0xFF);
		return bytes;
	}
}
