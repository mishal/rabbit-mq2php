package com.happyr.java.deferredEventWorker;

import com.happyr.java.deferredEventWorker.executors.ExecutorInterface;
import com.happyr.java.deferredEventWorker.queue.QueueInterface;

import java.text.SimpleDateFormat;

/**
 * com.happyr.java.deferredEventWorker
 *
 * @author Tobias Nyholm
 */
public class Worker extends Thread {

    private QueueInterface mq;
    private ExecutorInterface client;

    public Worker(QueueInterface mq, ExecutorInterface client) {
        this.mq = mq;
        this.client = client;
    }

    @Override
    public void run() {

        String error;
        Message message;
        while (true) {
            message = new Message(mq.receive());
            error = client.execute(message);

            //if there was any error
            if (error != null) {
                //add timestamp
                error = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + ": " + error;

                System.err.println(error);
                message.addHeader("error", error);
                mq.reportError(message.getFormattedMessage());
            }
        }
    }
}
