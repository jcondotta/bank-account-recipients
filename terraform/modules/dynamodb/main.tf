# DynamoDB Table for storing recipients
resource "aws_dynamodb_table" "recipients" {
  name           = var.recipients_table_name
  billing_mode   = var.recipients_billing_mode
  read_capacity  = var.recipients_read_capacity
  write_capacity = var.recipients_write_capacity

  hash_key  = "bankAccountId"
  range_key = "recipientName"

  attribute {
    name = "bankAccountId"
    type = "S"
  }

  attribute {
    name = "recipientName"
    type = "S"
  }

  tags = var.tags
}