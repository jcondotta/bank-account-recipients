aws_region           = "us-east-1"
environment          = "localstack"
aws_profile          = "localstack"

lambda_environment_variables = {
  AWS_DYNAMODB_ENDPOINT = "http://host.docker.internal:4566"
  AWS_SSM_ENDPOINT = "http://host.docker.internal:4566"

}
