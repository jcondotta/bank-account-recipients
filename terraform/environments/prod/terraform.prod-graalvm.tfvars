aws_region           = "us-east-1"
environment          = "prod"
aws_profile          = "jcondotta"

lambda_runtime = "provided.al2023"
lambda_handler = "io.micronaut.function.aws.proxy.MicronautLambdaHandler"
lambda_file =    "/../target/function.zip"