#data "aws_vpc" "default" {
#  default = true
#}
#
#data "aws_subnets" "default" {
#  filter {
#    name   = "vpc-id"
#    values = [data.aws_vpc.default.id]
#  }
#}
#
#resource "aws_security_group" "recipients_security_group" {
#  name        = "bank-account-recipients_security-group"
#  description = "Allow HTTP traffic"
#  vpc_id      = data.aws_vpc.default.id
#
#  ingress {
#    from_port   = 8080
#    to_port     = 8080
#    protocol    = "tcp"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#
#  egress {
#    from_port   = 0
#    to_port     = 0
#    protocol    = "-1"
#    cidr_blocks = ["0.0.0.0/0"]
#  }
#}