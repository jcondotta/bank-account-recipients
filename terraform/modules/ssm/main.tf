resource "aws_ssm_parameter" "jwt_signature_secret" {
  name  = var.jwt_signature_secret_name
  type  = "SecureString"
  value = var.jwt_signature_secret_value
  description = var.jwt_signature_secret_description

  lifecycle {
    create_before_destroy = true
  }
}