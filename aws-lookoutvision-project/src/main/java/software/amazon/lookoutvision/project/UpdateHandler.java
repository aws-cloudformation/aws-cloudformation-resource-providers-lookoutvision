package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.LookoutVisionClient;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ProxyClient;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

public class UpdateHandler extends BaseHandlerStd {

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final ProxyClient<LookoutVisionClient> proxyClient,
        final Logger logger) {

        // The AWS::LookoutVision::Project resource only has `createOnly` attributes, so there are currently no supported updates
        return new ReadHandler().handleRequest(proxy, request, callbackContext, proxyClient, logger);
    }
}
