package server.endpoints.outputmodels;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatMessageOutputModel {
	
	private String message;
	private Long sender; //defined by id
	private Long receiver; //defined by id
	private String timestamp;
	
	public ChatMessageOutputModel() {}

	public String getMessage() {
		return message;
	}

	public Long getSender() {
		return sender;
	}

	public Long getReceiver() {
		return receiver;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSender(Long sender) {
		this.sender = sender;
	}

	public void setReceiver(Long receiver) {
		this.receiver = receiver;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimestamp(Date date) {
		if (date == null) {
			this.timestamp = "";
			return;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.timestamp = sdf.format(date);
	}
}
