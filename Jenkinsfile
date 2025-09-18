pipeline {
    agent any

    environment {
        COMPOSE_FILE = "docker-compose.yml"
        REGISTRY_CONTAINER_NAME = "adminserviceregistry"
        TARGET_SERVICE = "uploadingservice"
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
                sh 'docker compose version'
            }
        }

        stage('Ensure Registry is Running') {
            steps {
                script {
                    def isRunning = sh(
                        script: "docker ps -q -f name=${REGISTRY_CONTAINER_NAME}",
                        returnStdout: true
                    ).trim()

                    if (isRunning) {
                        echo "${REGISTRY_CONTAINER_NAME} is already running. Skipping start."
                    } else {
                        echo "${REGISTRY_CONTAINER_NAME} not running. Starting container..."
                        sh "docker compose -f ${COMPOSE_FILE} up -d ${REGISTRY_CONTAINER_NAME}"
                    }
                }
            }
        }

        stage('Build and Run Target Service') {
            steps {
                sh "docker compose -f ${COMPOSE_FILE} build ${TARGET_SERVICE}"
                sh "docker compose -f ${COMPOSE_FILE} up -d ${TARGET_SERVICE}"
            }
        }
    }

    post {
        always {
            echo '✅ Pipeline execution completed.'
        }
    }
}
