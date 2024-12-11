package digi.kitplay.data.socket.dto;

import digi.kitplay.constant.Constants;
import lombok.Data;

@Data
public class Message {
    private Integer platform = Constants.PLATFORM;
    private String clientVersion = Constants.VERSION;
    private String lang = Constants.LANG;
    private String app = Constants.APP;
    private String cmd;
    private String token;
    private Object data;
    private Integer responseCode;
    private String msg;
    private String md5;
    private String subCmd;
//    private String screen;
//    private String requestId;
}
