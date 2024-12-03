variable "allow_all_cidr" {
  description = "CIDR block for open egress traffic"
  default     = ["0.0.0.0/0"]
}

resource "aws_security_group" "recipients_lambda_security_group" {
  name   = "recipients_lambda_sg"
  vpc_id = aws_vpc.this.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    description = "Restrict outbound traffic to VPC"
    cidr_blocks = [
      aws_vpc.this.cidr_block
    ]
  }

  tags = merge( { Name = "recipients_lambda_security_group" }, var.tags)
}

resource "aws_security_group" "ssm_vpce_security_group" {
  name        = "ssm_vpce_security_group"
  description = "Allow Lambda access to SSM VPC Endpoint"
  vpc_id      = aws_vpc.this.id

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    security_groups = [
      aws_security_group.recipients_lambda_security_group.id
    ]
    description = "Allow HTTPS traffic from Lambda security group"
  }

  egress {
    description = "Allow all outbound traffic"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = var.allow_all_cidr
  }

  tags = merge( { Name = "ssm_vpce_security_group" }, var.tags)
}

resource "aws_security_group" "dynamodb_vpce_security_group" {
  name        = "dynamodb-vpce-security_group"
  description = "Allow Lambda access to DynamoDB VPC Endpoint"
  vpc_id      = aws_vpc.this.id

  ingress {
    description     = "Allow HTTPS traffic from Lambda security group"
    from_port       = 443
    to_port         = 443
    protocol        = "tcp"
    security_groups = [
      aws_security_group.recipients_lambda_security_group.id
    ]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = var.allow_all_cidr
  }

  tags = merge( { Name = "dynamodb-vpce-security_group" }, var.tags)
}
#
#
#resource "aws_security_group" "recipients_redis_security_group" {
#  name   = "recipients_redis_security_group"
#  vpc_id = aws_vpc.this.id
#
#  ingress {
#    description     = "Allow Redis traffic from Lambda security group"
#    from_port       = 6379
#    to_port         = 6380
#    protocol        = "tcp"
#    security_groups = [
#      aws_security_group.recipients_lambda_security_group.id
#    ]
#  }
#
#  egress {
#    from_port   = 0
#    to_port     = 0
#    protocol    = "-1"
#    cidr_blocks = [aws_vpc.this.cidr_block]
#    description = "Allow outbound traffic within VPC"
#  }
#
#  tags = merge(
#    { Name = "recipients_redis_security_group" },
#    var.tags
#  )
#}
