#resource "aws_iam_role" "recipients_ecs_task_execution_exec" {
#  name = "bank-account-recipients-ecs-task-execution-role"
#  assume_role_policy = jsonencode(
#    {
#      Version = "2012-10-17",
#      Statement = [
#        {
#          Action = "sts:AssumeRole",
#          Effect = "Allow",
#          Principal = {
#            Service = "ecs-tasks.amazonaws.com"
#          }
#        }
#      ]
#    }
#  )
#}
#
#resource "aws_iam_role_policy_attachment" "ecs_task_execution_role_policy" {
#  role       = aws_iam_role.recipients_ecs_task_execution_exec.name
#  policy_arn = "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy"
#}