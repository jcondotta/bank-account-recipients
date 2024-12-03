resource "aws_elasticache_subnet_group" "this" {
  name        = "recipients-redis-serverless-subnet-group-${var.environment}"
  subnet_ids  = var.subnet_ids
  description = "Subnet group for Redis in private subnets"

  tags = merge({ Name = "recipients-serverless-subnet-group" }, var.tags)
}

resource "aws_elasticache_cluster" "recipients_redis" {
  cluster_id           = "recipients-redis-${var.environment}"
  engine               = "redis"
  node_type            = "cache.t4g.micro" # Smallest instance for testing
  num_cache_nodes      = 1                 # Single-node setup
  subnet_group_name    = aws_elasticache_subnet_group.this.name
  security_group_ids   = [var.redis_security_group_id]
  engine_version       = "6.2"              # Specify the Redis version
  parameter_group_name = "default.redis6.x" # Use default parameter group

  tags = merge( { Name = "recipients-redis-instance" }, var.tags)
}