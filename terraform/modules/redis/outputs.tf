output "redis_host" {
  description = "The Redis cluster endpoint"
  value       = aws_elasticache_cluster.recipients_redis.cache_nodes[0].address
}

output "redis_port" {
  description = "The Redis cluster port"
  value       = "6379" # Default Redis port
}