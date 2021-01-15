package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.model.ConflictException;
import software.amazon.cloudformation.exceptions.ResourceAlreadyExistsException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

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
            proxy.injectCredentialsAndInvokeV2(
                Translator.translateToCreateRequest(model),
                ClientBuilder.getClient()::createProject);
        } catch (final ConflictException e) {
            if (e.getMessage().contains(String.format("Project %s already exists", model.getProjectName()))) {
                final ResourceAlreadyExistsException resourceAlreadyExistsException =
                    new ResourceAlreadyExistsException(ResourceModel.TYPE_NAME, model.getArn());

                logger.log(resourceAlreadyExistsException.getMessage());
                throw resourceAlreadyExistsException;
            }

            throw e;
        }
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
