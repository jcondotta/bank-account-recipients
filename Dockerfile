# Use a base Ubuntu image
FROM ubuntu:latest

# Set environment variables for non-interactive installs
ENV DEBIAN_FRONTEND=noninteractive

# Install Homebrew (Linuxbrew)
RUN apt-get update && apt-get install -y \
    build-essential \
    curl \
    file \
    git

# Install Homebrew (Linuxbrew)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Add Homebrew to the PATH
ENV PATH="/home/linuxbrew/.linuxbrew/bin:/home/linuxbrew/.linuxbrew/sbin:$PATH"

# Update Homebrew and install necessary packages
RUN brew update && brew install \
    docker \
    docker-compose \
    maven \
    terraform \
    wget \
    python@3.9

# Install Amazon Corretto manually
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add - && \
    add-apt-repository 'deb https://apt.corretto.aws stable main' && \
    apt-get update && apt-get install -y java-17-amazon-corretto-jdk

# Verify Java installation
RUN java -version

# Set up Python virtual environment and install LocalStack and tflocal
RUN python3 -m venv /opt/venv && \
    /opt/venv/bin/pip install --upgrade pip && \
    /opt/venv/bin/pip install localstack terraform-local

# Set environment variable for the virtual environment
ENV PATH="/opt/venv/bin:/usr/local/bin:$PATH"

# Verify LocalStack and tflocal installations
RUN localstack --version && tflocal --version

# Clone the project repository into the container
RUN git clone https://github.com/jcondotta/bank-account-recipients.git /app

# Change working directory to the project directory
WORKDIR /app

# Build the project using Maven
RUN mvn clean package -DskipTests

# Clean up unnecessary files to keep the image size small
RUN brew cleanup && apt-get clean

# Expose necessary ports (if your application needs to expose ports)
EXPOSE 8080

# Command to run the application (modify according to how your project is run)
# CMD ["mvn", "spring-boot:run"]
