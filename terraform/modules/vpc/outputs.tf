output "lambda_security_group_id" {
  description = "Security group ID for Lambda"
  value       = aws_security_group.recipients_lambda_security_group.id
}
#
#output "redis_security_group_id" {
#  description = "Security group ID for Redis"
#  value       = aws_security_group.recipients_redis_security_group.id
#}
#
#output "ssm_security_group_id" {
#  description = "Security group ID for SSM"
#  value       = aws_security_group.ssm_vpce_security_group.id
#}
#
#output "dynamodb_security_group_id" {
#  description = "Security group ID for DynamoDB"
#  value       = aws_security_group.dynamodb_vpce_security_group.id
#}
#
#output "private_subnet_ids" {
#  description = "List of private subnet IDs in the VPC"
#  value       = [
#    aws_subnet.private_subnet_a.id,
#    aws_subnet.private_subnet_b.id
#  ]
#}