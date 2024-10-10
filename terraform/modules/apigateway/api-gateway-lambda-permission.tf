# Lambda permission to allow API Gateway to invoke the function
resource "aws_lambda_permission" "allow_apigateway_invoke" {
  statement_id  = "AllowExecutionFromAPIGateway-${var.lambda_function_name}"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_name
  principal     = "apigateway.amazonaws.com"

  # Allow all API Gateway methods and stages to invoke the Lambda function
  source_arn = "${aws_api_gateway_rest_api.recipients_api.execution_arn}/*/*/*"
}
