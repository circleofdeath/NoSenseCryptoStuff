package deltaalpha.crypto.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class LaunchPoolDTO {
    private Long id;
    private String exchange;
    private String launchPool;
    private Map<String, String> pools;
    private String period;
    private String status;
}