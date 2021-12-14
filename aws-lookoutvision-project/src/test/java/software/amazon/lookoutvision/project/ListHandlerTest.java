package software.amazon.lookoutvision.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.ListProjectsResponse;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ListHandlerTest {
    ListHandler handler;

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private ProxyClient<LookoutVisionClient> proxyClient;

    @Mock
    private LookoutVisionClient lookoutVisionClient;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        handler = new ListHandler();
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_Success() {
        final ProjectMetadata project1 = ProjectMetadata.builder()
            .projectName("Project1")
            .projectArn("arn:aws:lookoutvision:us-east-1:111111111111:project/Project1")
            .build();

        final ProjectMetadata project2 = ProjectMetadata.builder()
            .projectName("Project2")
            .projectArn("arn:aws:lookoutvision:us-east-1:111111111111:project/Project2")
            .build();

        final ListProjectsResponse listResponse = ListProjectsResponse.builder()
            .projects(Arrays.asList(project1, project2))
            .nextToken("token2")
            .build();

        doReturn(listResponse)
            .when(proxy)
            .injectCredentialsAndInvokeV2(
                eq(Translator.translateToListRequest("token1")),
                ArgumentMatchers.any()
            );

	doReturn(lookoutVisionClient)
	    .when(proxyClient)
	    .client();

        final ResourceModel model1 = ResourceModel.builder()
            .projectName("Project1")
            .arn("arn:aws:lookoutvision:us-east-1:111111111111:project/Project1")
            .build();

        final ResourceModel model2 = ResourceModel.builder()
            .projectName("Project2")
            .arn("arn:aws:lookoutvision:us-east-1:111111111111:project/Project2")
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .nextToken("token1")
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response =
            handler.handleRequest(proxy, request, null, proxyClient, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).containsAll(Arrays.asList(model1, model2));
        assertThat(response.getNextToken()).isEqualTo("token2");
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }
}
