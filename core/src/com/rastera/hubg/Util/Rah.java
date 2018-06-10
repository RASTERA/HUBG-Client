package com.rastera.hubg.Util;

import com.rastera.Networking.Message;

public class Rah {
    public static Message messageBuilder(int type, Object message) {
        Message nMessage = new Message();

        nMessage.type = type;
        nMessage.message = message;

        return nMessage;
    }

    public static String stringMultiply(int times, String item){

        return new String(new char[times]).replace("\0", item);  // Creates a String using a string array and replace the blanks
    }
}
