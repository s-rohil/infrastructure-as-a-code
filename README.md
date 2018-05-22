# Infrastructure as a Code

This repo is part of my spring 2018 course where I worked on developing a demo web application and successfully deploying it using a CI/CD pipeline in AWS

## Technology used

* AWS CloudFormation
* AWS CLI
* Springboot
* AWS Lambda - for implementing password reset functionality
* Travis CI
* VirtualBox with Ubuntu16.04


### Prerequisites

** The parameters for CloudFormation stacks are present under .gitignore so the stack won't execute successfully.


## Workflow of the pipeline

* Created an AWS Account
* Ran the scripts under infrastructure/aws/cloudformation to set up the infrastructure
* Built application using Travis CI and uploaded to S3
* Deployed application to Auto Scaling group using AWS CodeDeploy



### Information about the application

* Implemented Lambda function for password reset functionality
* Utilized AWS SNS, AWS SES and AWS Lambda, DynamoDB for achieving the above functionality


## Deployment




## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Network Structures course
* Our course instructor
