package network;

import lombok.NonNull;
import lombok.Setter;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

import interfaces.IMessageHandler;
import interfaces.ISession;

public class QueueHandler implements Runnable {
    private ISession session;
    private BlockingDeque<Message> messages;
    @Setter
    private IMessageHandler messageHandler;
    public int MAX_SIZE_MESSAGE_IN_POOL = 1000;

    public int FPS_SERVER = 33;

    public QueueHandler(@NonNull ISession session) {
        try {
            this.session = session;
            this.messages = new LinkedBlockingDeque<>();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void run() {
        try {
            while (session.isConnected()) {
                while (!messages.isEmpty()) {
                    Message message = messages.poll(5, TimeUnit.SECONDS);
                    if (message != null) {
                        this.messageHandler.onMessage(this.session, message);
                        message.cleanup();
                    }
                }
                TimeUnit.MILLISECONDS.sleep(FPS_SERVER); //~30FPS
            }
        } catch (Exception ignored) {
        }
    }

    public void addMessage(Message msg) {
        try {
            if (session.isConnected() && messages.size() < MAX_SIZE_MESSAGE_IN_POOL) {
                messages.add(msg);
            }
        } catch (Exception ignored) {
        }
    }

    public void close() {
        if (messages != null) {
            messages.clear();
        }
    }

    public void dispose() {
        this.session = null;
        this.messages = null;
    }
}
