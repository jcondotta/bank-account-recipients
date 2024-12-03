# Lambda permission to allow API Gateway to invoke the function
resource "aws_lambda_permission" "allow_apigateway_invoke_post" {
  statement_id  = "AllowExecutionFromAPIGateway-POST-${var.lambda_function_name}"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_name
  principal     = "apigateway.amazonaws.com"

  # Allow all API Gateway methods and stages to invoke the Lambda function
  source_arn = "${aws_api_gateway_rest_api.this.execution_arn}/*/POST/api/v1/recipients"
}

# Define the Lambda permission to allow API Gateway to invoke the Lambda function for GET
resource "aws_lambda_permission" "allow_apigateway_invoke_get" {
  statement_id  = "AllowExecutionFromAPIGateway-GET-${var.lambda_function_name}"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_api_gateway_rest_api.this.execution_arn}/*/GET/api/v1/recipients/bank-account-id/*"
}

# Define the Lambda permission to allow API Gateway to invoke the Lambda function for DELETE
resource "aws_lambda_permission" "allow_apigateway_invoke_delete" {
  statement_id  = "AllowExecutionFromAPIGateway-DELETE-${var.lambda_function_name}"
  action        = "lambda:InvokeFunction"
  function_name = var.lambda_function_name
  principal     = "apigateway.amazonaws.com"

  source_arn = "${aws_api_gateway_rest_api.this.execution_arn}/*/DELETE/api/v1/recipients/bank-account-id/*/recipient-name/*"
}