package life.majiang.community.community.provider;

import org.springframework.beans.factory.annotation.Value;


public class UcloudProvider {
    @Value("${Ucloud.ufile.public-key}")
    private String publicKey;
    @Value("${Ucloud.ufile.public-key}")
    private String privateKey;
}
