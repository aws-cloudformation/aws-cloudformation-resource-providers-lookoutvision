package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.model.ListProjectsResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ListHandler extends BaseHandler<CallbackContext> {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        final ListProjectsResponse response =
            proxy.injectCredentialsAndInvokeV2(Translator.translateToListRequest(request.getNextToken()),
                ClientBuilder.getClient()::listProjects);

        return ProgressEvent.<ResourceModel, CallbackContext>builder()
            .status(OperationStatus.SUCCESS)
            .resourceModels(Translator.translateFromListRequest(response))
            .nextToken(response.nextToken())
            .build();
    }
}
