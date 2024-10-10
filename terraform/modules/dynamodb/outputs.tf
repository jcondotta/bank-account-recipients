output "recipients_table_arn" {
  description = "The ARN of the DynamoDB recipients table created"
  value       = aws_dynamodb_table.recipients.arn
}

output "recipients_table_name" {
  description = "The name of the DynamoDB recipients table created"
  value       = aws_dynamodb_table.recipients.name
}