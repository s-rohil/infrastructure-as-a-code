#!/bin/bash
var1="$1"
# validation code
# name=$(aws cloudformation wait stack-exists --stack-name $var1 2>&1)
#
#
# if [[ -z $name ]];then
# 	echo "the stack exists. please enter a different name"
# 	exit 0
# fi

aws cloudformation create-stack --stack-name $var1 --template-body file://csye6225-cf-ci-cd.json --parameters file://parameters-iam.json --capabilities CAPABILITY_NAMED_IAM

aws cloudformation wait stack-create-complete --stack-name $var1

aws cloudformation describe-stacks --stack-name $var1 --query "Stacks[*].StackStatus" --output text

exit 0
