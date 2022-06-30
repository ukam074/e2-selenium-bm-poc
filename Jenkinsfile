def CURRENT_STAGE = ""

pipeline {
agent {
        kubernetes {
          cloud 'kubernetes-e2'
          // label establishes the name of the agent pod created.
          // When created, kubernetes will append random strings to the end for uniqueness.
          label 'e2-selenium-slave'

          // yaml is a description of the pod and it's containers that we need to build the job.
          // https://kubernetes.io/docs/tasks/configure-pod-container/
          // https://kubernetes.io/docs/reference/generated/kubernetes-api/v1.14/#podspec-v1-core
          yaml """
    apiVersion: v1
    kind: Pod
    spec:
      ssl.verification_mode: none
      ssl.certificate_authorities:
        - /etc/ssl/certs/ca-certificates.crt
      serviceAccountName: eit-user
      nodeSelector:
        kops.k8s.io/instancegroup: eitnodes
      volumes:
      - name: docker-socket
        hostPath:
          path: /var/run/docker.sock
      - name: jenkins-maven-storage-pvc
        persistentVolumeClaim:
          claimName: jenkins-maven-storage
      containers:
      - name: maven
        image: docker.ci1.carlsonwagonlit.com:18090/cwt/devtools:alpine
        command:
        - cat
        tty: true
        resources:
          requests:
            memory: "768Mi"
            cpu: "1250m"
          limits:
            memory: "1024Mi"
            cpu: "1750m"
      - name: docker
        image: docker:stable
        command:
        - cat
        tty: true
        volumeMounts:
        - mountPath: /var/run/docker.sock
          name: docker-socket
        resources:
          requests:
            memory: "32Mi"
            cpu: "75m"
          limits:
            memory: "64Mi"
            cpu: "125m"
      - name: kubectl
        image: dtzar/helm-kubectl:2.14.1
        command:
        - cat
        tty: true
        resources:
          requests:
            memory: "128Mi"
            cpu: "150m"
          limits:
            memory: "192Mi"
            cpu: "250m"
    """
        }
}
  
    // environment variables
    environment {
        RUN_ENV = "${TEST_ENVIRONMENT}"
        MVN_PROFILE = "${BROWSER},${TEST_ENVIRONMENT}${XTRA_PROFILES}"
        MVN_VERSION = 'M3'
    }

    // master build logic
    stages {
        stage ('Accounting Test') {
                when {
                    anyOf {
                        environment name: 'RUN_ENV', value: 'QA1';
                        environment name: 'RUN_ENV', value: 'QA3';
                    }
                }
                steps {
                    script {
                        CURRENT_STAGE = sh(returnStdout: true, script: 'echo Accounting Tests')
                    }
                    catchError{
                    withEnv( ["PATH+MAVEN=${tool MVN_VERSION}/bin"] ) {
                        sh 'mvn clean -P ${MVN_PROFILE} -Dit.test=AccountingTestSuite verify'
                      }
                    }
                }
                post {
                    always {
                        junit 'target/failsafe-reports/*.xml'
                    }
                    success {
                        sh 'echo "Completed successfully"'
                    }
                    failure {
                        sh 'echo "Failed to complete tests"'
                    }
                }
            }
//         stage ('Administration Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Administration Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=AdministrationTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Amendments Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Amendments Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=AmendmentsTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Cancel Delete Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Cancel Delete Tests Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=CancelDeleteTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('CBA Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo CBA Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=CBA* verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Dashboard Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Dashboard Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=DashboardTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Selenium Dos Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Selenium Dos Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=DosTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Email Information Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Email Information Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=EmailInformation verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Expenses Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Expenses Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ExpensesTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Extras Tests'){
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Extras Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ExtrasTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Foreign Currency Tests'){
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Foreign Currency Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ForeignCurrency verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//
//         stage ('Inbound GDXx Tests'){
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Inbound GDSx Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=InboundGDSx verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Payments Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Payments Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=Payments verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Profile Credit Card Info Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Profile Credit Card Info Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ProfileTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Reclaim Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Reclaim Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ReclaimTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Routing Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Routing Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ApproverUserType verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Routing Pool Maint. Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Routing Pool Maintenance Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=RoutingPoolMaintenanceSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('SelfApproval Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo SelfApproval Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=SelfApprovalSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//          stage ('Site Details Tests') {
//             when {
//                 anyOf {
//                             environment name: 'RUN_ENV', value: 'QA1';
//                             environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                             CURRENT_STAGE = sh(returnStdout: true, script: 'echo Site Details Tests')
//                 }
//                         catchError{
//                             sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=SiteDetailsSuite verify'
//                         }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                         }
//                     }
//                 }
//         stage ('Smoke Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Smoke Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=SmokeTest verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Trans And Other Expenses Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Trans And Other Expenses Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=TransAndOtherExpenses verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Travel Agent Assistance Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Travel Agent Assistance Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=TravelAgentAssistance verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Validator Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Validator Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=ValidatorTestSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Vouchers Suite Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Vouchers Suite Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=VouchersSuite verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Voucher Transaction Fee Tests') {
//             when {
//                 anyOf {
//                     environment name: 'RUN_ENV', value: 'QA1';
//                     environment name: 'RUN_ENV', value: 'QA3';
//                 }
//             }
//             steps {
//                 script {
//                     CURRENT_STAGE = sh(returnStdout: true, script: 'echo Voucher Transaction Fee Tests')
//                 }
//                 catchError{
//                     sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=VoucherTransactionFee verify'
//                 }
//             }
//             post {
//                 always {
//                     junit 'target/failsafe-reports/*.xml'
//                 }
//                 success {
//                     sh 'echo "Completed successfully"'
//                 }
//                 failure {
//                     sh 'echo "Failed to complete tests"'
//                 }
//             }
//         }
//         stage ('Worlflow Tests') {
//                     when {
//                         anyOf {
//                             environment name: 'RUN_ENV', value: 'QA1';
//                             environment name: 'RUN_ENV', value: 'QA3';
//                         }
//                     }
//                     steps {
//                         script {
//                             CURRENT_STAGE = sh(returnStdout: true, script: 'echo Workflow Tests')
//                         }
//                         catchError{
//                             sh '/usr/bin/mvn clean -P ${MVN_PROFILE} -Dit.test=WorkflowTest verify'
//                         }
//                     }
//                     post {
//                         always {
//                             junit 'target/failsafe-reports/*.xml'
//                         }
//                         success {
//                             sh 'echo "Completed successfully"'
//                         }
//                         failure {
//                             sh 'echo "Failed to complete tests"'
//                 }
//            }
//        }
    }
}
