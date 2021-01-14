package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.cloudformation.LambdaWrapper;

public class ClientBuilder {

  public static LookoutVisionClient getClient() {
    return LookoutVisionClient.builder()
        .httpClient(LambdaWrapper.HTTP_CLIENT)
        .build();
  }
}
