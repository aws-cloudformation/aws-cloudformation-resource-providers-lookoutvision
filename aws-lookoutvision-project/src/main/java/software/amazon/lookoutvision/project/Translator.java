package software.amazon.lookoutvision.project;

import software.amazon.awssdk.services.lookoutvision.model.CreateProjectRequest;
import software.amazon.awssdk.services.lookoutvision.model.DeleteProjectRequest;
import software.amazon.awssdk.services.lookoutvision.model.DescribeProjectRequest;
import software.amazon.awssdk.services.lookoutvision.model.CreateProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.DescribeProjectResponse;
import software.amazon.awssdk.services.lookoutvision.model.ListProjectsRequest;
import software.amazon.awssdk.services.lookoutvision.model.ListProjectsResponse;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is a centralized placeholder for
 *  - api request construction
 *  - object translation to/from aws sdk
 *  - resource model construction for read/list handlers
 */

public class Translator {

  /**
   * Request to create a resource
   * @param model resource model
   * @return createProjectRequest the aws service request to create a project
   */
  static CreateProjectRequest translateToCreateRequest(final ResourceModel model) {
    final CreateProjectRequest createProjectRequest = CreateProjectRequest.builder()
        .projectName(model.getProjectName())
        .build();

    return createProjectRequest;
  }

  /**
   * Translate resource object from sdk into a resource model
   * @param createProjectResponse the lookout vision create project response
   * @return project resource model
   */
  static ResourceModel translateFromCreateResponse(final CreateProjectResponse createProjectResponse) {
    return ResourceModel.builder()
        .projectName(createProjectResponse.projectMetadata().projectName())
        .arn(createProjectResponse.projectMetadata().projectArn())
        .build();
  }

  /**
   * Request to read a resource
   * @param model resource model
   * @return describeProjectRequest the aws service request to describe a project
   */
  static DescribeProjectRequest translateToReadRequest(final ResourceModel model) {
    final DescribeProjectRequest describeProjectRequest = DescribeProjectRequest.builder()
        .projectName(model.getProjectName())
        .build();
    return describeProjectRequest;
  }

  /**
   * Translates resource object from sdk into a resource model
   * @param describeProjectResponse the lookout vision describe project response
   * @return project resource model
   */
  static ResourceModel translateFromReadResponse(final DescribeProjectResponse describeProjectResponse) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L58-L73
    return ResourceModel.builder()
        .projectName(describeProjectResponse.projectDescription().projectName())
        .arn(describeProjectResponse.projectDescription().projectArn())
        .build();
  }

  /**
   * Request to delete a project
   * @param model resource model
   * @return deleteProjectRequest the aws service request to delete a project
   */
  static DeleteProjectRequest translateToDeleteRequest(final ResourceModel model) {
    return DeleteProjectRequest.builder()
        .projectName(model.getProjectName())
        .build();
  }

  /**
   * Request to list resources
   * @param nextToken token passed to the aws service list resources request
   * @return awsRequest the aws service request to list resources within aws account
   */
  static ListProjectsRequest translateToListRequest(final String nextToken) {
    return ListProjectsRequest.builder()
        .maxResults(50)
        .nextToken(nextToken)
        .build();
  }

  /**
   * Translates resource objects from sdk into a resource model (primary identifier only)
   * @param response the aws service describe resource response
   * @return list of resource models
   */
  static List<ResourceModel> translateFromListRequest(final ListProjectsResponse response) {
    // e.g. https://github.com/aws-cloudformation/aws-cloudformation-resource-providers-logs/blob/2077c92299aeb9a68ae8f4418b5e932b12a8b186/aws-logs-loggroup/src/main/java/com/aws/logs/loggroup/Translator.java#L75-L82
    return streamOfOrEmpty(response.projects())
        .map(project -> ResourceModel.builder()
            .projectName(project.projectName())
            .arn(project.projectArn())
            .build())
        .collect(Collectors.toList());
  }

  private static <T> Stream<T> streamOfOrEmpty(final Collection<T> collection) {
    return Optional.ofNullable(collection)
        .map(Collection::stream)
        .orElseGet(Stream::empty);
  }
}
