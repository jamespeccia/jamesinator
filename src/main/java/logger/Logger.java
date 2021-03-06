package logger;

public class Logger {

    public static void log(String message) {
        LogThread logThread = new LogThread(message);
        logThread.start();
    }

    public static void log(String message, String exception) {
        LogThread logThread = new LogThread(message + " (" + exception + ")");
        logThread.start();
    }

    static class LogThread extends Thread {
        private final String message;

        LogThread(String message) {
            this.message = message;
        }

        public void run() {
            System.out.println(this.message);
            Discord.send(this.message);
        }
    }
}
