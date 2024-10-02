# S3 Bucket for storing the Terraform state
resource "aws_s3_bucket" "terraform_state_bucket" {
  bucket = var.terraform_state_bucket_name

  tags = var.tags
}

# aws_s3_bucket_public_access_block for managing all public access at the bucket level
resource "aws_s3_bucket_public_access_block" "terraform_state_bucket_access_block" {
  bucket = aws_s3_bucket.terraform_state_bucket.id

  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# aws_s3_bucket_versioning for managing versioning settings
resource "aws_s3_bucket_versioning" "terraform_state_bucket_versioning" {
  bucket = aws_s3_bucket.terraform_state_bucket.id

  versioning_configuration {
    status = "Enabled"
  }
}

# add server-side encryption
resource "aws_s3_bucket_server_side_encryption_configuration" "terraform_state_bucket_encryption" {
  bucket = aws_s3_bucket.terraform_state_bucket.id

  rule {
    apply_server_side_encryption_by_default {
      sse_algorithm = "AES256"
    }
  }
}