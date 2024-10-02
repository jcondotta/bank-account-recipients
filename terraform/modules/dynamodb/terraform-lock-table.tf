# DynamoDB table used for locking Terraform state to prevent concurrent operations
resource "aws_dynamodb_table" "terraform_lock_table" {
  name         = var.terraform_lock_table_name
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = var.tags
}