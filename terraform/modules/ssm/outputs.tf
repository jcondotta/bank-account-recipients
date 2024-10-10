output "jwt_signature_secret_name" {
  value = aws_ssm_parameter.jwt_signature_secret.name
}

output "jwt_signature_secret_arn" {
  value = aws_ssm_parameter.jwt_signature_secret.arn
}