resource "aws_internet_gateway" "recipients_internet_gateway" {
  vpc_id = aws_vpc.recipients_vpc.id

  tags = merge({ Name = "recipients-internet-gateway"},
    var.tags
  )
}