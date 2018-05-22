#!/bin/bash
var1="$1"

#name=$(aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackName" --output text 2>&1)
#echo $?

# name=$(aws cloudformation wait stack-exists --stack-name $var1 2>&1)
# validation code
# if [[ -z $name ]];then
# 	echo "the stack exists. please enter a different name"
# 	exit 0
# fi

aws cloudformation create-stack --stack-name $var1 --parameters ParameterKey=vpcName,ParameterValue=${var1}-csye6225-vpc ParameterKey=gatewayName,ParameterValue=${var1}-csye6225-InternetGateway ParameterKey=publicroutetableName,ParameterValue=${var1}-csye6225-public-route-table ParameterKey=privateroutetableName,ParameterValue=${var1}-csye6225-private-route-table --template-body file://csye6225-cf-networking.json

aws cloudformation wait stack-create-complete --stack-name $var1

aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackStatus" --output text

exit 0
