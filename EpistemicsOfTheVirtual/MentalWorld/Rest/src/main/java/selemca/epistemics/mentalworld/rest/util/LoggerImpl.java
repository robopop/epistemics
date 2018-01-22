package selemca.epistemics.mentalworld.rest.util;

import selemca.epistemics.mentalworld.engine.MentalWorldEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LoggerImpl implements MentalWorldEngine.Logger, Iterable<LoggerImpl.Message> {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public Iterator<Message> iterator() {
        return messages.iterator();
    }

    @Override
    public void debug(String message) {
        addMessage(Level.DEBUG, message);
    }

    @Override
    public void info(String message) {
        addMessage(Level.INFO, message);
    }

    @Override
    public void warning(String message) {
        addMessage(Level.WARNING, message);
    }

    protected void addMessage(Level level, String message) {
        messages.add(new Message(level, message));
    }

    public enum Level {
        DEBUG,
        WARNING,
        INFO
    }

    protected static class Message {
        private final Level level;
        private final String message;
        protected Message(Level level, String message) {
            this.level = level;
            this.message = message;
        }

        public Level getLevel() {
            return level;
        }

        public String getMessage() {
            return message;
        }
    }
}
