#!/bin/bash

# Set default values for AWS_PROFILE and AWS_REGION if not provided
AWS_PROFILE=${1:-jcondotta}
AWS_REGION=${2:-us-east-1}

# Display the values being used
echo "Using AWS profile: $AWS_PROFILE"
echo "Using AWS region: $AWS_REGION"

# Create S3 bucket if it doesn't exist
S3_BUCKET_NAME="terraform-recipients-state-bucket"
echo "Checking if S3 bucket exists: $S3_BUCKET_NAME"
if aws s3api head-bucket --bucket $S3_BUCKET_NAME --region $AWS_REGION --profile $AWS_PROFILE 2>/dev/null; then
  echo "Bucket $S3_BUCKET_NAME already exists."
else
  echo "Creating S3 bucket: $S3_BUCKET_NAME"
  aws s3api create-bucket --bucket $S3_BUCKET_NAME --region $AWS_REGION --profile $AWS_PROFILE
fi

# Create DynamoDB table
DYNAMODB_TABLE_NAME="terraform-recipients-lock-table"
echo "Creating DynamoDB table: $DYNAMODB_TABLE_NAME"
aws dynamodb create-table \
  --table-name $DYNAMODB_TABLE_NAME \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region $AWS_REGION \
  --profile $AWS_PROFILE

echo "Setup completed successfully."
