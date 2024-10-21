#variable "aws_region" {
#  description = "The AWS region where resources will be deployed."
#  type        = string
#}
#
#variable "environment" {
#  description = "The environment the Lambda function is deployed to (e.g., dev, localstack, prod or staging)"
#  type        = string
#}
#
#variable "ecs_cluster_name" {
#  description = "The name of the recipients ECS cluster"
#  type        = string
#}
#
#variable "tags" {
#  description = "Tags applied to all resources for organization and cost tracking across environments and projects."
#  type        = map(string)
#}