package digi.kitplay.data.download;

public interface DownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
