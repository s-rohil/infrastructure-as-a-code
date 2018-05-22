#!/bin/bash
Stack_Name="$1"

#
# #validations for script inputs
# if [[ ( -z $ref_stack_name && -z $Stack_Name ) ]];then
# 	echo "no parameters were provided"
# 	exit 0
# elif [[ ( -z $ref_stack_name || -z $Stack_Name ) ]]; then
# 	echo "Insufficient parameters provided"
# 	exit 0
# else
# 	#check if networking stack exists
# 	net_name=$(aws cloudformation wait stack-exists --stack-name $ref_stack_name 2>&1)
# 	if [[ -z $net_name ]]; then
# 		#check if the new stack to be added already exists
# 		name=$(aws cloudformation wait stack-exists --stack-name $Stack_Name 2>&1)
# 		if [[ -z $name ]]; then
# 			echo "The stack already exists. Please select a different name"
# 			exit 0
# 		fi
# 	else
# 		echo "No such networking stack exists"
# 		exit 0
# 	fi
# fi


aws cloudformation create-stack --template-body file://csye6225-cf-application.json --stack-name $Stack_Name --parameters file://parameters-application.json --capabilities CAPABILITY_NAMED_IAM

aws cloudformation wait stack-create-complete --stack-name $Stack_Name

aws cloudformation describe-stacks --stack-name $Stack_Name --query "Stacks[*].StackStatus" --output text

exit 0
