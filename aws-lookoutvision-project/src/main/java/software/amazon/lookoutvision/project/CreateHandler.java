package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.model.ConflictException;
import software.amazon.awssdk.services.lookoutvision.model.CreateProjectResponse;
import software.amazon.cloudformation.exceptions.CfnInvalidRequestException;
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

        // Make sure the user isn't trying to assign values to readOnly properties
        if (hasReadOnlyProperties(model)) {
            throw new CfnInvalidRequestException("Attempting to set a ReadOnly Property.");
        }

        CreateProjectResponse createProjectResponse = null;
        try {
            createProjectResponse = proxy.injectCredentialsAndInvokeV2(
                Translator.translateToCreateRequest(model),
                ClientBuilder.getClient()::createProject);
        } catch (final ConflictException e) {
            if (e.getMessage().contains(String.format("Project %s already exists", model.getProjectName()))) {
                final ResourceAlreadyExistsException resourceAlreadyExistsException =
                    new ResourceAlreadyExistsException(ResourceModel.TYPE_NAME, model.getArn(), e);

                logger.log(resourceAlreadyExistsException.getMessage());
                throw resourceAlreadyExistsException;
            }

            throw e;
        }
        final String createMessage = String.format("%s successfully created.", ResourceModel.TYPE_NAME);
        logger.log(createMessage);

        final ResourceModel modelFromCreateResult = Translator.translateFromCreateResponse(createProjectResponse);

        return ProgressEvent.defaultSuccessHandler(modelFromCreateResult);
    }

    /**
     * This function checks that the model provided by CloudFormation does not contain any readOnly properties (i.e Arn).
     *
     * @param model the ResourceModel for the given CreateHandler invocation
     * @return a boolean indicating if the ResourceModel contains readOnly properties
     */
    private boolean hasReadOnlyProperties(final ResourceModel model) {
        return model.getArn() != null;
    }
}
