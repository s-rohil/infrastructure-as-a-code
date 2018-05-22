# CSYE 6225 Infrastructure

This folder deals with the Infrastructure configs in AWS

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

You would require:

* VirtualBox or VMWare Fusion
* Ubuntu Linux VM
* Pip
* [AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/awscli-install-linux.html)

```
sudo apt-get install python-pip
```


## Running the tests

Make sure AWS CLI is configured with your access and secret keys. The below command will help you to provide the details for your aws setup.

```
aws configure
```


### Break down into end to end tests

```
Running the script

> ./csye6225-aws-networking-setup.sh stackname

```



```
aws ec2 describe vpcs

{
    "Vpcs": [
        {
            "VpcId": "vpc-xxxxxx", 
            "InstanceTenancy": "default", 
            "CidrBlockAssociationSet": [
                {
                    "AssociationId": "vpc-cidr-assoc-xxxxxx", 
                    "CidrBlock": "172.31.0.0/16", 
                    "CidrBlockState": {
                        "State": "associated"
                    }
                }
            ], 
            "State": "available", 
            "DhcpOptionsId": "dopt-fcb57e84", 
            "CidrBlock": "172.31.0.0/16", 
            "IsDefault": true
        }
    ]
}

```

