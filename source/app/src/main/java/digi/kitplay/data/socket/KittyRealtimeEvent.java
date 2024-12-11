package digi.kitplay.data.socket;

import digi.kitplay.data.socket.dto.Message;

public interface KittyRealtimeEvent {
    void onMessageReceived(Message message);
    void onConnectionClosed();
    void onConnectionClosing();
    void onConnectionFailed();
    void onConnectionOpened();
}
