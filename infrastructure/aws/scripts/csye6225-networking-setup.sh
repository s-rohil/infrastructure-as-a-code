#!/bin/bash
echo "This is my network building shell script"
#var=$(aws ec2 describe-vpcs --query "Vpcs[*]" --output text)
#echo $var
#$STACK_NAME_var="Hello World"
#echo $STACK_NAME_var


# you can just have one command for creation of the name
#creating vpc
STACK_NAME="$1"
if [ -z "$STACK_NAME" ];then
	echo "No parameters were given"
	exit 0
fi

instance_id=$(aws ec2 create-vpc --cidr-block 10.0.0.0/16 --query "Vpc.VpcId" --output text)
echo $instance_id

#polling until vpc is available
aws ec2 wait vpc-available --vpc-ids $instance_id

#creating the name for vpc
aws ec2 create-tags --resources $instance_id --tags 'Key=Name,Value='$STACK_NAME'-csye6225-vpc'
vpc_name=$(aws ec2 describe-vpcs --vpc-ids $instance_id --query "Vpcs[0].Tags[0].Value" --output text)
echo $vpc_name

#creating the internet gateway and getting its ID
gateway_id=$(aws ec2 create-internet-gateway --query "InternetGateway.InternetGatewayId" --output text)
echo $gateway_id

#creating the name for internet gateway
aws ec2 create-tags --resources $gateway_id --tags 'Key=Name,Value='$STACK_NAME'-csye6225-InternetGateway'
gateway_name=$(aws ec2 describe-internet-gateways --internet-gateway-ids $gateway_id --query "InternetGateways[0].Tags[0].Value" --output text)
echo $gateway_name

#attaching the internet gateway to our target VPC
aws ec2 attach-internet-gateway --internet-gateway-id $gateway_id --vpc-id $instance_id

#creating route table
route_table_id=$(aws ec2 create-route-table --vpc-id $instance_id --query "RouteTable.RouteTableId" --output text)
echo $route_table_id

#naming the route table
aws ec2 create-tags --resources $route_table_id --tags 'Key=Name,Value='$STACK_NAME'-csye6225-public-route-table'
route_table_name=$(aws ec2 describe-route-tables --route-table-ids $route_table_id --query "RouteTables[0].Tags[0].Value" --output text)
echo $route_table_name

#readonly STACK_NAME=assignment3
#aws ec2 create-tags --resources $instance_id $gateway_id $route_table_id --tags 'Key=Name,Value=$STACK_NAME-csye6225-vpc' 'Key=Name,Value=$STACK_NAME-csye6225-InternetGateway' 'Key=Name,Value=$STACK_NAME-csye6225-public-route-table'

#create route
route_status=$(aws ec2 create-route --route-table-id $route_table_id --destination-cidr-block 0.0.0.0/0 --gateway-id $gateway_id)
echo $route_status

