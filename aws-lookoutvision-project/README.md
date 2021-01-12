# AWS::LookoutVision::Project

1. Write the JSON schema describing your resource, `aws-lookoutvision-project.json`
1. Implement your resource handlers.

This package has two main components:
1. The JSON schema describing an Amazon Lookout for Vision Project, `aws-lookoutvision-project.json`
1. The resource handlers that actually create, delete, update, read, and list Amazon Lookout for Vision Projects.

The RPDK will automatically generate the correct resource model from the schema whenever the project is built via Maven. You can also do this manually with the following command: `cfn generate`.

> Please don't modify files under `target/generated-sources/rpdk`, as they will be automatically overwritten.

The code uses [Lombok](https://projectlombok.org/), and [you may have to install IDE integrations](https://projectlombok.org/setup/overview) to enable auto-complete for Lombok-annotated classes.
