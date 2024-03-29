package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.awssdk.services.lookoutvision.model.DescribeProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class ReadHandler extends BaseHandlerStd {

    private AmazonWebServicesClientProxy proxy;
    private ResourceHandlerRequest<ResourceModel> request;
    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<LookoutVisionClient> proxyClient,
        final Logger logger) {

        this.proxy = proxy;
        this.request = request;
        this.logger = logger;

        return fetchProjectAndAssertExists(proxyClient);
    }

    private ProgressEvent<ResourceModel, CallbackContext> fetchProjectAndAssertExists(final ProxyClient<LookoutVisionClient> proxyClient) {
        final ResourceModel model = request.getDesiredResourceState();

        if (model == null || model.getProjectName() == null) {
            throwNotFoundException(model);
        }

        DescribeProjectResponse describeProjectResponse = null;
        try {
            describeProjectResponse = proxy.injectCredentialsAndInvokeV2(Translator.translateToReadRequest(model),
                proxyClient.client()::describeProject);
        } catch (final ResourceNotFoundException e) {
            throwNotFoundException(model);
        }

        final ResourceModel modelFromReadResult = Translator.translateFromReadResponse(describeProjectResponse);

        return ProgressEvent.defaultSuccessHandler(modelFromReadResult);
    }

    private void throwNotFoundException(final ResourceModel model) {
        final ResourceModel nullSafeModel = model == null ? ResourceModel.builder().build() : model;

        final software.amazon.cloudformation.exceptions.ResourceNotFoundException rpdkException =
            new software.amazon.cloudformation.exceptions.ResourceNotFoundException(ResourceModel.TYPE_NAME,
                nullSafeModel.getProjectName());

        logger.log(rpdkException.getMessage());
        throw rpdkException;
    }
}
