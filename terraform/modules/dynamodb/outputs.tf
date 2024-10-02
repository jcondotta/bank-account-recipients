output "recipients_table_arn" {
  description = "The ARN of the DynamoDB recipients table created"
  value       = aws_dynamodb_table.recipients.arn
}