pipeline {
    agent any

    environment {
        IMAGE_NAME = "configuration"
        CONTAINER_NAME = "configurationservice"
        DOCKER_NETWORK = "updated_orgadmin_rmscadminnetwork"
        HOST_PORT = "8084"
        CONTAINER_PORT = "8084"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Docker Version') {
            steps {
                sh 'docker --version'
            }
        }

        stage('Clean Old Docker Image') {
            steps {
                echo "Removing old Docker image if it exists..."
                sh "docker rmi -f ${IMAGE_NAME}:latest || true"
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image..."
                sh "docker build -t ${IMAGE_NAME}:latest ."
            }
        }

        stage('Stop & Remove Old Container') {
            steps {
                sh """
                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                """
            }
        }

        stage('Run Docker Container') {
            steps {
                sh """
                    docker run -d --name ${CONTAINER_NAME} \\
                        -p ${HOST_PORT}:${CONTAINER_PORT} \\
                        --network ${DOCKER_NETWORK} \\
                        ${IMAGE_NAME}:latest
                """
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline execution completed.'
        }
    }
}
