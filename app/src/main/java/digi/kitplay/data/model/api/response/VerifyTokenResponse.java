package digi.kitplay.data.model.api.response;
import lombok.Data;
@Data
public class VerifyTokenResponse {
    private String token;
    private String accessToken;
}
