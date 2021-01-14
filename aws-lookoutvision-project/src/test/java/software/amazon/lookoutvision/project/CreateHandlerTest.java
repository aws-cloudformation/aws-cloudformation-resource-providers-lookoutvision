package software.amazon.lookoutvision.project;

import org.mockito.ArgumentMatchers;
import software.amazon.awssdk.core.SdkClient;
import software.amazon.awssdk.services.lookoutvision.model.CreateProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.DescribeProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.ProjectDescription;
import software.amazon.awssdk.services.lookoutvision.model.ProjectMetadata;
import software.amazon.awssdk.services.lookoutvision.model.ResourceNotFoundException;
import software.amazon.cloudformation.exceptions.ResourceAlreadyExistsException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateHandlerTest {
    private CreateHandler handler;

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        handler = new CreateHandler();
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
    }

    @Test
    public void handleRequest_Success() {
        final String projectName = "projectName";
        final String projectArn = "arn:aws:lookoutvision:us-east-1:111111111111:project/projectName";

        final ResourceNotFoundException firstDescribeResponse = ResourceNotFoundException.builder().build();
        final CreateProjectResponse createProjectResponse = CreateProjectResponse.builder()
            .projectMetadata(ProjectMetadata.builder()
                .projectArn(projectArn)
                .projectName(projectName)
                .build())
            .build();
        final DescribeProjectResponse describeResponse = DescribeProjectResponse.builder()
            .projectDescription(ProjectDescription.builder()
                .projectName(projectName)
                .projectArn(projectArn)
                .build())
            .build();

        when(proxy.injectCredentialsAndInvokeV2(
            ArgumentMatchers.any(),
            ArgumentMatchers.any()
        ))
            .thenThrow(firstDescribeResponse)
            .thenReturn(createProjectResponse)
            .thenReturn(describeResponse);

        final ResourceModel model = ResourceModel.builder()
            .projectName(projectName)
            .arn(projectArn)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        final ProgressEvent<ResourceModel, CallbackContext> response = handler.handleRequest(proxy, request, null, logger);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getResourceModel()).isEqualToComparingFieldByField(model);
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequest_FailureAlreadyExists() {
        final String projectName = "projectName";
        final String projectArn = "arn:aws:lookoutvision:us-east-1:111111111111:project/projectName";
        final ProjectDescription projectDescription = ProjectDescription.builder()
            .projectName(projectName)
            .projectArn(projectArn)
            .build();

        final DescribeProjectResponse describeResponseInitial = DescribeProjectResponse.builder()
            .projectDescription(projectDescription)
            .build();

        doReturn(describeResponseInitial)
            .when(proxy)
            .injectCredentialsAndInvokeV2(
                ArgumentMatchers.any(),
                ArgumentMatchers.any()
            );

        final ResourceModel model = ResourceModel.builder()
            .projectName(projectName)
            .arn(projectArn)
            .build();

        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
            .desiredResourceState(model)
            .build();

        assertThrows(ResourceAlreadyExistsException.class,
            () -> handler.handleRequest(proxy, request, null, logger));
    }
}
