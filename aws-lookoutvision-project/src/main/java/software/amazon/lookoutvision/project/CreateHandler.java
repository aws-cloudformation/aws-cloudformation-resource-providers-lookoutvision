package software.amazon.lookoutvision.project;

import software.amazon.cloudformation.exceptions.ResourceAlreadyExistsException;
import software.amazon.cloudformation.exceptions.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import java.util.Objects;

public class CreateHandler extends BaseHandler<CallbackContext> {

    private AmazonWebServicesClientProxy proxy;
    private ResourceHandlerRequest<ResourceModel> request;
    private Logger logger;

    @Override
    public ProgressEvent<ResourceModel, CallbackContext> handleRequest(
        final AmazonWebServicesClientProxy proxy,
        final ResourceHandlerRequest<ResourceModel> request,
        final CallbackContext callbackContext,
        final Logger logger) {

        this.proxy = proxy;
        this.request = request;
        this.logger = logger;

        final ResourceModel model = request.getDesiredResourceState();

        try {
            new ReadHandler().handleRequest(proxy, request, callbackContext, logger);
            final ResourceAlreadyExistsException e = new ResourceAlreadyExistsException(ResourceModel.TYPE_NAME,
                model.getProjectName());
            logger.log(e.getMessage());
            throw e;
        } catch (final ResourceNotFoundException e) {
            // We want a ResourceNotFoundException because this means the Project doesn't already exist,
            // and we can go ahead and create one
        }

        proxy.injectCredentialsAndInvokeV2(
            Translator.translateToCreateRequest(model),
            ClientBuilder.getClient()::createProject);
        final String createMessage = String.format("%s successfully created.", ResourceModel.TYPE_NAME);
        logger.log(createMessage);

        final ResourceModel finalModel = new ReadHandler().handleRequest(proxy,
            request,
            callbackContext,
            logger)
            .getResourceModel();
        return ProgressEvent.defaultSuccessHandler(finalModel);
    }
}
