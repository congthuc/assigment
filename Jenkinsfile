pipeline {
    agent any
    
    environment {
        // Define environment variables
        DOCKER_REGISTRY = 'your-docker-registry'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/insurance-app:${env.BUILD_NUMBER}"
        
        // LaunchDarkly SDK Key - Should be stored in Jenkins credentials
        LAUNCHDARKLY_SDK_KEY = credentials('launchdarkly-sdk-key')
        
        // Database configuration
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://your-db-host:5432/ifsw-db'
        SPRING_DATASOURCE_USERNAME = 'ifsw-user'
        
        // Get branch name (for conditional deployments)
        BRANCH_NAME = "${env.BRANCH_NAME ?: env.CHANGE_BRANCH ?: 'main'}"
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Set up JDK') {
            steps {
                script {
                    // Use Java 21 as specified in README
                    jdk = tool name: 'jdk-21', type: 'jdk'
                    env.JAVA_HOME = "${jdk}"
                    env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
                    sh 'java -version'
                }
            }
        }
        
        stage('Build') {
            steps {
                sh './gradlew clean build -x test'
            }
        }
        
        stage('Unit Tests') {
            steps {
                // Run unit tests (excludes blackbox tests)
                sh './gradlew test'
            }
            post {
                always {
                    // Publish JUnit test results
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }
        
        stage('Integration Tests') {
            when {
                // Only run integration tests on main branch or pull requests
                anyOf {
                    branch 'main'
                    changeRequest()
                }
            }
            steps {
                // Run blackbox tests in a containerized environment
                sh './gradlew blackboxTest'
            }
            post {
                always {
                    // Publish blackbox test results
                    junit '**/build/test-results/blackboxTest/*.xml'
                }
            }
        }
        
        stage('Build Docker Image') {
            when {
                // Only build Docker image on main branch or tags
                anyOf {
                    branch 'main'
                    tag pattern: 'v.*', comparator: 'REGEXP'
                }
            }
            steps {
                script {
                    // Build and tag the Docker image
                    sh """
                    docker build \
                        --build-arg LAUNCHDARKLY_SDK_KEY=${LAUNCHDARKLY_SDK_KEY} \
                        -t ${DOCKER_IMAGE} .
                    """
                }
            }
        }
        
        stage('Push to Registry') {
            when {
                // Only push to registry on main branch or tags
                anyOf {
                    branch 'main'
                    tag pattern: 'v.*', comparator: 'REGEXP'
                }
            }
            steps {
                script {
                    // Login to Docker registry (configure credentials in Jenkins)
                    withCredentials([
                        usernamePassword(
                            credentialsId: 'docker-registry-credentials',
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )
                    ]) {
                        sh """
                        echo "${DOCKER_PASS}" | docker login ${DOCKER_REGISTRY} -u "${DOCKER_USER}" --password-stdin
                        docker push ${DOCKER_IMAGE}
                        """
                    }
                }
            }
        }
        
        stage('Deploy to Staging') {
            when {
                // Only deploy to staging from main branch
                branch 'main'
            }
            steps {
                script {
                    // Deploy to staging environment
                    // This would typically use your orchestration tool (e.g., Kubernetes, ECS, etc.)
                    echo 'Deploying to staging environment...'
                    // Example: kubectl apply -f k8s/staging-deployment.yaml
                }
            }
        }
        
        stage('Deploy to Production') {
            when {
                // Only deploy to production on version tags
                tag pattern: 'v.*', comparator: 'REGEXP'
            }
            steps {
                script {
                    // Deploy to production environment
                    echo 'Deploying to production environment...'
                    // Example: kubectl apply -f k8s/production-deployment.yaml
                }
            }
        }
    }
    
    post {
        always {
            // Clean up workspace
            cleanWs()
        }
        success {
            // Send success notification
            echo 'Pipeline completed successfully!'
        }
        failure {
            // Send failure notification
            echo 'Pipeline failed!'
        }
    }
}
