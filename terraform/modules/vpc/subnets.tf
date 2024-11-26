resource "aws_subnet" "public_subnet_a" {
  vpc_id            = aws_vpc.recipients_vpc.id
  cidr_block        = "10.0.0.0/26"
  map_public_ip_on_launch = true
  availability_zone = "us-east-1a"

  tags = merge({ Name = "recipients-public-subnet-a"},
    var.tags
  )
}

resource "aws_subnet" "public_subnet_b" {
  vpc_id            = aws_vpc.recipients_vpc.id
  cidr_block        = "10.0.0.64/26"
  map_public_ip_on_launch = true
  availability_zone = "us-east-1b"

  tags = merge({ Name = "recipients-public-subnet-b"},
    var.tags
  )
}

resource "aws_subnet" "private_subnet_a" {
  vpc_id            = aws_vpc.recipients_vpc.id
  cidr_block        = "10.0.0.128/26"
  availability_zone = "us-east-1a"

  tags = merge({ Name = "recipients-private-subnet-a"},
    var.tags
  )
}

resource "aws_subnet" "private_subnet_b" {
  vpc_id            = aws_vpc.recipients_vpc.id
  cidr_block        = "10.0.0.192/26"
  availability_zone = "us-east-1b"

  tags = merge({ Name = "recipients-private-subnet-b"},
    var.tags
  )
}