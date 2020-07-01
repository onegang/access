package org.onegang.access.email;

import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.annotation.PostConstruct;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

import org.onegang.access.email.dao.RequestService;
import org.onegang.access.email.dao.UserMapper;
import org.onegang.access.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IMAPStore;

/**
 * This acts on users replies.
 * 
 * @author TC
 *
 */
@Component
public class EmailBot {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailBot.class);

	@Value("${email-host}")
	private String emailHost;

	@Value("${email-port}")
	private int emailPort;

	@Value("${email-system}")
	private String systemEmail;

	@Value("${email-system-password}")
	private String systemPassword;

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private RequestService requestService;

	@PostConstruct
	public void listenIMAP() {
		LOGGER.info("Starting listener on IMAP: {}", systemEmail);

		Properties properties = new Properties();
		// properties.put("mail.debug", "true");
		properties.put("mail.store.protocol", "imaps");
		properties.put("mail.imaps.host", "imap.gmail.com");
		properties.put("mail.imaps.port", "993");
		properties.put("mail.imaps.timeout", "10000");

		Session session = Session.getInstance(properties);
		IMAPStore store = null;
		Folder inbox = null;

		try {
			store = (IMAPStore) session.getStore("imaps");
			store.connect(systemEmail, systemPassword);

			if (!store.hasCapability("IDLE")) {
				throw new RuntimeException("IDLE not supported");
			}

			inbox = (IMAPFolder) store.getFolder("INBOX");
			inbox.addMessageCountListener(new MessageCountAdapter() {

				@Override
				public void messagesAdded(MessageCountEvent event) {
					Message[] messages = event.getMessages();

					for (Message message : messages) {
						try {
							LOGGER.info("Mail Subject:- " + message.getSubject());
							if (isValidEmail(message)) {
								processEmail(message);
							}
						} catch (Exception e) {
							LOGGER.info("Error in reading email", e);
						}
					}
				}
			});

			IdleThread idleThread = new IdleThread(inbox);
			idleThread.setDaemon(true);
			idleThread.start();

//            idleThread.join();
			// idleThread.kill(); //to terminate from another thread

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(inbox);
			close(store);
		}
	}

	private boolean isValidEmail(Message message) throws MessagingException {
		return message.getSubject().contains(EmailConstants.EMAIL_PREFIX);
	}

	private void processEmail(Message message) throws MessagingException, IOException {
		String content = getContent(message).toUpperCase();
		LOGGER.debug("Parsing: {}", content);
		
		String email = getEmailAddress(message.getFrom()[0]);
		String username = getUsername(email);
		LOGGER.info("Getting the user: {}, {}", email, username);
		
		String id = message.getSubject();
		id = id.substring(id.indexOf(EmailConstants.EMAIL_PREFIX)+EmailConstants.EMAIL_PREFIX.length());
		id = id.substring("[SR-".length(), id.indexOf(']'));
		//find is the user among the request
		LOGGER.info("Getting request {}", id);
		
		if(content.startsWith("OK") || 
			content.startsWith("APPROVE") || 
			content.startsWith("DONE") || 
			content.startsWith("IMPLEMENTED")) {			
			requestService.doApprove(Integer.parseInt(id), username);
		} else if(content.startsWith("NOT OK") || 
				content.startsWith("REJECT")) {			
				requestService.doReject(Integer.parseInt(id), username);
		}
	}
	
	private String getUsername(String email) {
		User user = userMapper.selectUserByEmail(email);
		if(user==null) {
			return "Amina Burch, 19822"; //XXX For testing only
		}
		return user.getName();
	}
	
	private String getEmailAddress(Address address) {
		String email = address.toString();
		if(email.contains("<") && email.contains(">")) {
			return email.substring(email.indexOf("<")+1, email.length()-1);
		} else {
			return email;
		}
	}

	private String getContent(Message message) throws IOException, MessagingException {
		Multipart multipart = (Multipart) message.getContent();
		String content = "";
		for (int j = 0; j < multipart.getCount(); j++) {
			BodyPart bodyPart = multipart.getBodyPart(j);
			String disposition = bodyPart.getDisposition();
			if (disposition != null && (disposition.equalsIgnoreCase("ATTACHMENT"))) {
				DataHandler handler = bodyPart.getDataHandler();
				LOGGER.warn("Mail have some attachment: {}", handler.getName());
			} else {
				content += bodyPart.getContent().toString();
			}
		}
		return content;
	}

	private class IdleThread extends Thread {

		private final Folder folder;
		private volatile boolean running = true;

		public IdleThread(Folder folder) {
			super("IdleThread");
			this.folder = folder;
		}

//		public synchronized void kill() {
//			if (!running)
//				return;
//			this.running = false;
//		}

		@Override
		public void run() {
			while (running) {

				try {
					ensureOpen(folder);
					LOGGER.info("enter idle");
					((IMAPFolder) folder).idle();
				} catch (Exception e) {
					// something went wrong wait and try again
					LOGGER.error("Error in idle thread", e);
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						// ignore
					}
				}

			}
		}
	}

	public void close(final Folder folder) {
		try {
			if (folder != null && folder.isOpen()) {
				folder.close(false);
			}
		} catch (final Exception e) {
			// ignore
		}

	}

	public void close(final Store store) {
		try {
			if (store != null && store.isConnected()) {
				store.close();
			}
		} catch (final Exception e) {
			// ignore
		}

	}

	public void ensureOpen(final Folder folder) throws MessagingException {

		if (folder != null) {
			Store store = folder.getStore();
			if (store != null && !store.isConnected()) {
				store.connect(systemEmail, systemPassword);
			}
		} else {
			throw new MessagingException("Unable to open a null folder");
		}

		if (folder.exists() && !folder.isOpen() && (folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
			LOGGER.info("open folder {}", folder.getFullName());
			folder.open(Folder.READ_ONLY);
			if (!folder.isOpen())
				throw new MessagingException("Unable to open folder " + folder.getFullName());
		}

	}

}
