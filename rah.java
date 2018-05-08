public class rah {
    public static Message messageBuilder (int type, Object message) {
        Message nMessage = new Message();

        nMessage.type = type;
        nMessage.message = message;

        return nMessage;
    }
}
