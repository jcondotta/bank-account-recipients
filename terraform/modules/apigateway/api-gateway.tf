# Define the API Gateway REST API for the recipients service
resource "aws_api_gateway_rest_api" "recipients_api" {
  name        = "recipients-api-${var.environment}"
  description = "API Gateway for recipients service in ${var.environment} environment"

  tags = var.tags
}