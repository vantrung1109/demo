package digi.kitplay.data.model.api.response;

import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CheckUpdateResponse extends BaseResponse{
    @SerializedName("URL")
    private String url;
    private String version;
    @SerializedName("this_version")
    private long currentVersion;
}
