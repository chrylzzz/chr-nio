package com.lnsoft.bio.threadPool;

/**
 * Created By Chr on 2019/3/1/0001.
 */
public class RequestHandler {
    public String handle(String request) {
        return "From NIOServer Hello : " + request + ".\n";
    }
}
