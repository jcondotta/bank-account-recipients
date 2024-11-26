resource "aws_security_group" "recipients_lambda_security_group" {
  name        = "recipients_lambda_security_group"
  vpc_id = aws_vpc.recipients_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags
}

resource "aws_security_group" "recipients_redis_security_group" {
  name        = "recipients_redis_security_group"
  vpc_id = aws_vpc.recipients_vpc.id

  ingress {
    from_port       = 6379
    to_port         = 6379
    protocol        = "tcp"
    security_groups = [aws_security_group.recipients_lambda_security_group.id]
  }

  ingress {
    from_port       = 6380
    to_port         = 6380
    protocol        = "tcp"
    security_groups = [aws_security_group.recipients_lambda_security_group.id]
  }

  tags = var.tags
}