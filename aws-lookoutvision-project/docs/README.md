# AWS::LookoutVision::Project

The AWS::LookoutVision::Project type creates an Amazon Lookout for Vision project. A project is a grouping of the resources needed to create and manage a Lookout for Vision model.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::LookoutVision::Project",
    "Properties" : {
        "<a href="#projectname" title="ProjectName">ProjectName</a>" : <i>String</i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::LookoutVision::Project
Properties:
    <a href="#projectname" title="ProjectName">ProjectName</a>: <i>String</i>
</pre>

## Properties

#### ProjectName

The name of the Amazon Lookout for Vision project

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>255</code>

_Pattern_: <code>[a-zA-Z0-9][a-zA-Z0-9_\-]*</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ProjectName.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### Arn

Returns the <code>Arn</code> value.

