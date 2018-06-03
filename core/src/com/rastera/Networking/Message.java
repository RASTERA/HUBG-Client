package com.rastera.Networking;

import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 653214L;
    public int type;
    public Object message;
    public String name;
}
