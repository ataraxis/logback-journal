package org.gnieh.logback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

/**
 * An appender that send the events to systemd journal
 * 
 * @author Lucas Satabin
 * 
 */
public class SystemdJournalAppender extends AppenderBase<ILoggingEvent> {

	boolean logLocation = true;

	boolean logThreadName = true;

	@Override
	protected void append(ILoggingEvent event) {
		try {
			// get the message id if any
			Map<String, String> mdc = event.getMDCPropertyMap();

			List<Object> messages = new ArrayList<>();
			// the formatted human readable message
			messages.add(event.getFormattedMessage());

			// the log level
			messages.add("PRIORITY=%i");
			messages.add(levelToInt(event.getLevel()));

			// the location information if any is available and it is enabled
			if (logLocation && event.hasCallerData()) {
				StackTraceElement[] callerData = event.getCallerData();
				if (callerData.length > 0) {
					messages.add("CODE_FILE=%s");
					messages.add(callerData[0].getFileName());
					messages.add("CODE_LINE=%i");
					messages.add(callerData[0].getLineNumber());
					messages.add("CODE_FUNC=%s.%s");
					messages.add(callerData[0].getClassName());
					messages.add(callerData[0].getMethodName());
				}
			}

			// log thread name if enabled
			if (logThreadName) {
				messages.add("THREAD_NAME=%s");
				messages.add(event.getThreadName());
			}

			// add a message id field if any is defined for this logging event
			if (mdc.containsKey(SystemdJournal.MESSAGE_ID)) {
				messages.add("MESSAGE_ID=" + mdc.get(SystemdJournal.MESSAGE_ID));
			}
			// the vararg list is null terminated
			messages.add(null);

			System.out.println(messages);

			SystemdJournalLibrary journald = SystemdJournalLibrary.INSTANCE;

			journald.sd_journal_send("MESSAGE=%s", messages.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int levelToInt(Level l) {
		switch (l.toInt()) {
		case Level.TRACE_INT:
		case Level.DEBUG_INT:
			return 7;
		case Level.INFO_INT:
			return 6;
		case Level.WARN_INT:
			return 4;
		case Level.ERROR_INT:
			return 3;
		default:
			throw new IllegalArgumentException("Unknown level value: " + l);
		}
	}

	public boolean isLogLocation() {
		return logLocation;
	}

	public void setLogLocation(boolean logLocation) {
		this.logLocation = logLocation;
	}

	public boolean isLogThreadName() {
		return logThreadName;
	}

	public void setLogThreadName(boolean logThreadName) {
		this.logThreadName = logThreadName;
	}

}