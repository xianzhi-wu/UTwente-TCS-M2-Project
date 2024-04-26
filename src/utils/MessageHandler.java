package utils;

public class MessageHandler {

    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static void handleError(String message)  {
        System.err.println(message);
    }
    
    public static void handleError(Exception e) {
        System.err.println(e.getMessage());
        e.printStackTrace();
    }
    
    public static void handleError(Exception e, String message) {
        System.err.println(message);
        System.err.println(e.getMessage());
        e.printStackTrace();
    }

}
