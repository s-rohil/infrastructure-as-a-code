#!/bin/bash -e
echo "Hello World"
echo "First argument: $1"
STACK_NAME="$1"
echo $STACK_NAME
if [ -z "$STACK_NAME" ];then
	echo "No parameters were given"
	exit 0
fi

vpc_id=$(aws ec2 describe-vpcs --filters "Name=tag-value,Values="$STACK_NAME"-csye6225-vpc" --query "Vpcs[*].VpcId" --output text)
echo $vpc_id

if [ -z "$vpc_id" ];then
	echo "No VPCs found"
	exit 0
fi

#check if vpc exists. write code for if not exists
#aws ec2 wait vpc-exists --vpc-ids $vpc_id

#delete route table
route_table_id=$(aws ec2 describe-route-tables --filters "Name=tag-value,Values="$STACK_NAME"-csye6225-public-route-table" --query "RouteTables[*].RouteTableId" --output text)
echo $route_table_id

aws ec2 delete-route-table --route-table-id $route_table_id

#detach the intenet gateway




# delete internet gateway
gateway_id=$(aws ec2 describe-internet-gateways --filters "Name=tag-value,Values="$STACK_NAME"-csye6225-InternetGateway" --query "InternetGateways[*].InternetGatewayId" --output text)
echo $gateway_id

aws ec2 detach-internet-gateway --internet-gateway-id $gateway_id --vpc-id $vpc_id

aws ec2 delete-internet-gateway --internet-gateway-id $gateway_id


aws ec2 delete-vpc --vpc-id $vpc_id

exit 0
