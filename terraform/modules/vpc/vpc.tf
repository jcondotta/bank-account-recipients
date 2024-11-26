resource "aws_vpc" "recipients_vpc" {
  cidr_block           = "10.0.0.0/24"
  enable_dns_support   = true
  enable_dns_hostnames = true
  instance_tenancy     = "default"

  tags = merge({ Name = "recipients-vpc"},
    var.tags
  )
}