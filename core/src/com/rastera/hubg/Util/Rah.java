package com.rastera.hubg.Util;

import com.rastera.Networking.Message;

public class Rah {
    public static Message messageBuilder(int type, Object message) {
        Message nMessage = new Message();

        nMessage.type = type;
        nMessage.message = message;

        return nMessage;
    }
}
