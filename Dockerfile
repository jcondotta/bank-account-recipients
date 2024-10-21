# Use a base Ubuntu image
FROM ubuntu:24.10
ENV DEBIAN_FRONTEND=noninteractive

# Update package list and install necessary packages for Docker
RUN apt-get update && apt-get install -y \
    curl \
    wget \
    ca-certificates \
    gnupg \
    lsb-release \
    software-properties-common

# Add Docker's official GPG key
RUN curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

# Add Docker's stable repository
RUN echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] \
https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | \
tee /etc/apt/sources.list.d/docker.list > /dev/null

# Install Docker CLI
RUN apt-get update && apt-get install -y \
    docker-ce \
    docker-ce-cli \
    containerd.io

# Install Docker Compose (version 2.x which includes arm64 support)
RUN curl -SL https://github.com/docker/compose/releases/download/v2.0.1/docker-compose-linux-aarch64 -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose && \
    ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose && \
    docker-compose --version

# Add Amazon Corretto APT repository and install Java 17 (Amazon Corretto)
RUN wget -O- https://apt.corretto.aws/corretto.key | apt-key add - && \
    add-apt-repository 'deb https://apt.corretto.aws stable main' && \
    apt-get update && apt-get install -y java-17-amazon-corretto-jdk

# Verify Java installation
RUN java -version

# Install Maven 3.9.6
RUN wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
RUN tar -xvzf apache-maven-3.9.6-bin.tar.gz -C /opt
RUN ln -s /opt/apache-maven-3.9.6/bin/mvn /usr/bin/mvn
RUN mvn -v

# Clone the project repository into the container
RUN git clone https://github.com/jcondotta/bank-account-recipients.git /app

# Optional: Clean up unnecessary packages to reduce image size
RUN apt-get clean && rm -rf /var/lib/apt/lists/*
