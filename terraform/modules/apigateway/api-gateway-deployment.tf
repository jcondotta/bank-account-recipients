# Deploy the API Gateway (ensure all methods are deployed)
resource "aws_api_gateway_deployment" "recipients_api_deployment" {
  rest_api_id = aws_api_gateway_rest_api.this.id

  depends_on = [
    aws_api_gateway_integration.post_login_integration,
    aws_api_gateway_integration.post_recipients_integration,
    aws_api_gateway_integration.get_bank_account_recipients_integration,
    aws_api_gateway_integration.delete_recipients_integration
  ]
}

# Define the API Gateway stage
resource "aws_api_gateway_stage" "recipients_api_stage" {
  deployment_id = aws_api_gateway_deployment.recipients_api_deployment.id
  rest_api_id   = aws_api_gateway_rest_api.this.id
  stage_name    = "${var.environment}-stage"

  tags = {
    Name = "${var.environment}-stage"
  }
}