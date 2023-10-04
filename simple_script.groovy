pipeline{
    agent {
        label 'cpu1'
    }
    
    stages{
        stage("Cleanup") {
            steps {
                cleanWs()
            }
        }
        stage('Setup parameters'){
            steps{
                script{
                    properties(
                        [[$class: 'JiraProjectProperty'], 
                        [$class: 'RebuildSettings', autoRebuild: false, rebuildDisabled: false], 
                        parameters([
                            string(defaultValue: '3414', description: '', name: 'RUN_ID', trim: false), 
                            extendedChoice(defaultValue: 'Default,Billing,Storage', description: 'Select which type of scenario to test(default,billing,storage). (Multi Select is allowed)', multiSelectDelimiter: ',', name: 'SELECT_MODE', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_MULTI_SELECT', value: 'Default,Billing,Storage', visibleItemCount: 3),
                            // choice(choices: ['false', 'true'], description: '''This must be true inorder to run aut using singlesignup''', name: 'SINGLESIGNUP'), 
                            choice(choices: ['false', 'true'], description: '''Mark \'true\' if want to run sso master''', name: 'SSO'), 
                            choice(choices: ['true', 'false'], description: '''Mark \'true\' if want to create a copy of above testrun id''', name: 'RERUN'), 
                            choice(choices: ['true', 'false', 'existing'], description: '''Mark \'false\' if you run the zalenium on default''', name: 'ZALENIUM'), 
                            choice(choices: ['default', 'azure', 'S3', 'seagate'], description: 'Select the Storage type', name: 'STORAGE_TYPE'),
                            choice(choices: ['true', 'false'], description: '''Mark \'false\' if you want to create tenant through api''', name: 'UITENANT'), 
                            string(defaultValue: '', description: '', name: 'NEW_TESTRUN_TITLE', trim: false), 
                            string(defaultValue: 'http://app.aks-cicd-12650.cicd.cnvrg.me', description: '''Enter environment url to run automation on''', name: 'DEFAULT_URL', trim: true), 
                            string(defaultValue: '60', description: '''Number of threads on which automation will run''', name: 'PARALLEL_SPLIT_TEST_PROCESSES', trim: true), 
                            choice(choices: ['true', 'false'], description: '', name: 'RETEST'), 
                            string(defaultValue: 'master', description: '', name: 'AUT_BRANCH', trim: true), 
                            [$class: 'ChoiceParameter', name: 'Existing_tenant', choiceType: 'PT_CHECKBOX', description: 'Select when you run on existing tenant', script: [$class: 'GroovyScript', script: [classpath: [], sandbox: true, script: "return ['true']"], fallbackScript: [classpath: [], sandbox: true, script: 'return[\'error\']']]],
                            [$class: 'DynamicReferenceParameter', name: 'TENANT_URL', choiceType: 'ET_FORMATTED_HTML', omitValueField: true, referencedParameters: 'Existing_tenant', description: '', script: [$class: 'GroovyScript', script: [classpath: [], sandbox: true, script: ''' if (Existing_tenant.equals("true")) { return '<input name="value" class="setting-input" type="text" value="">'} '''], fallbackScript: [classpath: [], sandbox: true, script: ''' return """ """ ''']]],
                            [$class: 'DynamicReferenceParameter', name: 'TENANT_EMAIL', choiceType: 'ET_FORMATTED_HTML', omitValueField: true, referencedParameters: 'Existing_tenant', description: '', script: [$class: 'GroovyScript', script: [classpath: [], sandbox: true, script: ''' if (Existing_tenant.equals("true")) { return '<input name="value" class="setting-input" type="text" value="">'} '''], fallbackScript: [classpath: [], sandbox: true, script: ''' return """ """ ''']]],
                            [$class: 'DynamicReferenceParameter', name: 'TENANT_ORG_NAME', choiceType: 'ET_FORMATTED_HTML', omitValueField: true, referencedParameters: 'Existing_tenant', description: '', script: [$class: 'GroovyScript', script: [classpath: [], sandbox: true, script: ''' if (Existing_tenant.equals("true")) { return '<input name="value" class="setting-input" type="text" value="">'} '''], fallbackScript: [classpath: [], sandbox: true, script: ''' return """ """ ''']]],
                            [$class: 'DynamicReferenceParameter', name: 'TENANT_USER_NAME', choiceType: 'ET_FORMATTED_HTML', omitValueField: true, referencedParameters: 'Existing_tenant', description: '', script: [$class: 'GroovyScript', script: [classpath: [], sandbox: true, script: ''' if (Existing_tenant.equals("true")) { return '<input name="value" class="setting-input" type="text" value="">'} '''], fallbackScript: [classpath: [], sandbox: true, script: ''' return """ """ ''']]],
                            string(defaultValue: 'http://testgrid.cnvrg.io/wd/hub', description: '', name: 'REMOTE_URL', trim: true), 
                            string(defaultValue: '5', description: '', name: 'PARALLEL_SPLIT_TEST_PROCESSES_CUSTOM_RESOURCE', trim: true), 
                            string(defaultValue: '', description: 'Set the custom resource name in order to run aut other than default', name: 'CUSTOM_RESOURCE_NAME', trim: true),
                            [$class: 'WHideParameterDefinition', defaultValue: 'us-east-2', description: '', name: 'AWS_REGION'], 
                            string(defaultValue: '', description: '', name: 'TENANT_IMAGE', trim: true), 
                            choice(choices: ['false', 'true'], description: '', name: 'TAG_TENANT_SAAS'), 
                            [$class: 'WHideParameterDefinition', defaultValue: 'false', description: '', name: 'STORAGE_NEW_URL'], 
                            choice(choices: ['chrome', 'firefox'], description: '', name: 'BROWSER'), 
                            [$class: 'WHideParameterDefinition', defaultValue: '{"not_on_aks": ["23182"], "not_on_eks": [], "not_on_gke": [] ,"only_master":["29736"]}', description: 'Cases to exclude on different environments.', name: 'EXCLUDE_CASES'], 
                            [$class: 'WHideParameterDefinition', defaultValue: '', description: 'This parameter will be contain remote build no only when this job is called through QA-Automation.', name: 'BUILD_NUMBER_DELETE'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'true', description: '', name: 'DELETE_GRID'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'xoxb-6133864085-3090963159154-ew13uExAKgfaZHJ6jZEHIySU', description: '', name: 'SLACK_TOKEN'], 
                            [$class: 'RandomStringParameterDefinition', description: '', failedValidationMessage: '', name: 'RANDOM'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'false', description: '', name: 'ONE_CLICK'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'false', description: '''true/false to send an email for the testrun report''', name: 'SEND_TESTRAIL_EMAIL_REPORT'], 
                            [$class: 'WHideParameterDefinition', defaultValue: '4', description: 'which testrail report template to hit', name: 'TEST_RAIL_REPORT_ID'], 
                            extendedChoice(description: 'Select which type of scenario to test. Default it will run all the scenarios present in testrun. (Multi Select is allowed)', multiSelectDelimiter: ',', name: 'RUN_STATUS', quoteValue: false, saveJSONParameterToFile: false, type: 'PT_MULTI_SELECT', value: 'Passed, Blocked, Failed, Untested, Retest, Warning', visibleItemCount: 6), 
                            [$class: 'WHideParameterDefinition', defaultValue: 'false', description: '', name: 'USE_CUSTOM_CLUSTER'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'false', description: '', name: 'SCALE_DOWN'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'true', description: '', name: 'SELENOID'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'true', description: '', name: 'TAKE_SCREENSHOT'], 
                            [$class: 'WHideParameterDefinition', defaultValue: 'true', description: '', name: 'POPULATE_TEST_RAIL']
                        ]), 
                        [$class: 'JobLocalConfiguration', changeReasonComment: '']]
                    )
                }
            }
        }

//         stage('Fetch Git Project') {
//             steps {
//                 git branch: '${AUT_BRANCH}', credentialsId: '9e673d23-974c-460c-ba67-1188333cf4b4', poll: false, url: 'https://github.com/AccessibleAI/cnvrg_aut.git'
//             }
//         }
//         stage('Add Zalenium to existing cluster') {
//             when{
//                 allOf{
//                     environment name: 'ZALENIUM', value: 'existing'
//                     environment name: 'REMOTE_URL', value: 'http://testgrid.cnvrg.io/wd/hub'
//                 }
//             }
//             steps {
//                 script {
//                     env.cluster = env.DEFAULT_URL.split("app.")[1].split(".cicd.cnvrg")[0]
//                     // env.ONE_CLICK = 'true'
//                     env.build_no = cluster.split("-cicd-")[1]
//                     env.desired_containers = (env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger()).toInteger()
//                     env.max_containers = (env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger() * 1.5).toInteger()
//                     env.REMOTE_URL = "http://testgrid.${cluster}.cicd.cnvrg.me/wd/hub"
//                     def res = build job: 'Get Kube CICD Clusters', parameters: [
//                         string(name: 'CLUSTER_NAME', value: cluster)
//                         ], wait: true, propagate: false
//                     env.kubeconfig = res.getBuildVariables().Kubeconfig_File_QA
//                     env.kubeconfig = env.kubeconfig.toString().trim()
//                     // def tag = build job: 'Tag CICD Clusters', parameters: [
//                     //     string(name: 'CLUSTER_NAME', value: cluster)
//                     //     ], wait: true, propagate: false
//                     build job: 'Scale CICD Cluster', parameters: [
//                         string(name: 'CLUSTER_NAME', value: cluster),
//                         string(name: 'Scale', value: 'up'),
//                         string(name: 'Num_Nodes', value: '10')
//                         ], wait: true, propagate: false
//                     echo "${WORKSPACE}"
//                     sh """cat <<-EOF>\${WORKSPACE}/kubeconfig
//                     $kubeconfig"""
//                     sh """sed "1 s/^[ \t]*//" -i kubeconfig"""
//                     echo "******** Adding zalenium *************"
//                     sh '''
//                         export KUBECONFIG=kubeconfig
//                         helm repo add zalenium-github https://raw.githubusercontent.com/zalando/zalenium/master/charts/zalenium
//                         helm repo update
//                         helm upgrade -i zalenium-$build_no -n cnvrg --create-namespace zalenium-github/zalenium --set hub.serviceType=ClusterIP,hub.desiredContainers=$desired_containers,hub.maxDockerSeleniumContainers=$max_containers,hub.videoRecordingEnabled=false,hub.keepOnlyFailedTests=true,hub.retentionPeriod=3,hub.maxTestSessions=1,hub.screenWidth=1380,hub.screenHeight=800 --set hub.resources.requests.cpu=6,hub.resources.requests.memory=23000Mi --set persistence.video.enabled=false,persistence.video.useExisting=true,persistence.video.name=selenium-video --set persistence.data.enabled=false,persistence.data.useExisting=true,persistence.data.name=selenium-data --set serviceAccount.create=false --set nameOverride=zalenium-$build_no
//                     '''
//                     sleep 220
//                     sh """cat <<-EOF>> \${WORKSPACE}/selenium_vs.yaml
// ---
// apiVersion: networking.istio.io/v1beta1
// kind: VirtualService
// metadata:
//   name: testgrid
//   namespace: cnvrg
// spec:
//   gateways:
//     - istio-gw-cnvrg
//   hosts:
//     - testgrid.${cluster}.cicd.cnvrg.me
//   http:
//   - retries:
//       attempts: 5
//       perTryTimeout: 180s
//     route:
//     - destination:
//         host: zalenium-${build_no}.cnvrg.svc.cluster.local
//         port:
//           number: 80
//     timeout: 18000s
// EOF"""
//                     sh '''
//                         export KUBECONFIG=kubeconfig
//                         kubectl apply -f selenium_vs.yaml
//                     ''' 
//                     sleep 10
//                 }
//             }
//         }
//         stage("Fetch/Recreate Test Run") {
//             when{
//                 environment name: 'ONE_CLICK', value: 'false'
//             }
//             steps {
//                 script {
//                     if ((env.DEFAULT_URL == "https://metacloud.aut.staging-cloud.cnvrg.io" || env.DEFAULT_URL == "https://metacloud.staging-cloud.cnvrg.io" || env.DEFAULT_URL == "https://metacloud.qa.staging-cloud.cnvrg.io")|| env.TENANT_URL){
//                         env.SINGLESIGNUP = "true"

//                         if (env.SELECT_MODE.contains('Default')){
//                             env.DEFAULT = 'true'
//                             println("Present Default Section")
//                         }
//                         if (env.SELECT_MODE.contains('Billing')){
//                             env.BILLING = 'true'
//                             println("Present Billing Section")
//                         }
//                         if (env.SELECT_MODE.contains('Storage')){
//                             env.STORAGE = 'true'
//                             println("Present Storage Section")
//                         }
//                     } else {
//                         env.SINGLESIGNUP = "false"
//                     }

//                     env.RANDOMF = params.RANDOM.toLowerCase() + "mkl"
//                     sh '''docker build -f DockerFetchCases --build-arg SELECT_MODE=$SELECT_MODE --build-arg RUN_STATUS=$RUN_STATUS --build-arg NEW_TESTRUN_TITLE=$NEW_TESTRUN_TITLE --build-arg RERUN=$RERUN --build-arg RUN_ID=$RUN_ID -t $RANDOMF .'''
//                     sh '''
//                         cont_id=$(docker create $RANDOMF)
//                         docker cp $cont_id:/cnvrg_aut/environment_var.sh $(pwd)/environment_var.sh
//                         docker cp $cont_id:/cnvrg_aut/total_test_cases_in_testrun.json $(pwd)/total_test_cases_in_testrun.json
//                         docker cp $cont_id:/cnvrg_aut/environment_var_1.sh $(pwd)/environment_var_1.sh
//                         ls
//                     '''
//                     def new_run_id = sh(script: "sh environment_var.sh", returnStdout: true).trim()
//                     env.RUN_ID = new_run_id
//                     sh 'echo $RUN_ID'
//                     env.RERUN = 'false'
//                     def temp = sh(script: "sh environment_var_1.sh", returnStdout: true).trim()
//                     sh 'echo $temp'
//                     env.NEW_TENANT = temp
//                     sh 'echo $NEW_TENANT'
//                     sh 'printenv'
//                     if (temp.contains('Billing')){
//                         env.BILLING_NEW_TENANT = 'true'
//                     } else{
//                         env.BILLING_NEW_TENANT = 'false'
//                     }
//                     if (temp.contains('StorageAndQueues')){
//                         env.QUEUES_STORAGE_NEW_TENANT = 'true'
//                     } else{
//                         env.QUEUES_STORAGE_NEW_TENANT = 'false'
//                     }

//                     if(env.CUSTOM_RESOURCE_NAME != ''){
//                         env.QUEUES_STORAGE_NEW_TENANT = 'false'
//                         env.BILLING_NEW_TENANT = 'false'
//                     }
//                 }
//             }
//         }
//         stage("Run CICD Aut"){
//             when{
//                 environment name: 'SINGLESIGNUP', value: 'false'
//             }
//             parallel{
//                 stage("Update Testrail"){
//                     steps{
//                         script {
//                             echo "${DEFAULT_URL}"
//                             def cluster = env.DEFAULT_URL.split("app.")[1].split(".cicd.cnvrg")[0]
//                             echo "${cluster}"
//                             // sh 'ls'
//                             env.z_value = env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger()*3
//                             sh "echo $RANDOM"
//                             env.RANDOM = params.RANDOM.toLowerCase()
//                             sh "echo $RANDOM"
//                             if(env.ONE_CLICK == "false") {
//                                 def res = build job: 'Get Kube CICD Clusters', parameters: [
//                                     string(name: 'CLUSTER_NAME', value: cluster)
//                                     ], wait: true, propagate: false
//                                 env.kubeconfig = res.getBuildVariables().Kubeconfig_File_QA
//                                 env.kubeconfig = env.kubeconfig.toString().trim()
//                                 sh """cat <<-EOF>\${WORKSPACE}/kubeconfig
//                                 $kubeconfig"""
//                                 sh """sed "1 s/^[ \t]*//" -i kubeconfig"""
//                                 echo "******** Adding CICD Details *************"  
//                                 sh '''
//                                     export KUBECONFIG=kubeconfig
//                                     app_name="$(kubectl -n cnvrg get pods | grep 'app' | awk '{print $1}' | head -n 1)"
//                                     operator_name="$(kubectl -n cnvrg get pods | grep 'cnvrg-operator' | awk '{print $1}' | head -n 1)"
//                                     app_image="$(kubectl -n cnvrg get pod "${app_name}" -o=jsonpath='{.spec.containers[0].image}')"
//                                     operator_version="$(kubectl -n cnvrg get pod "${operator_name}" -o=jsonpath='{.spec.containers[?(@.name=="cnvrg-operator")].image}' | awk -F':' '{print $0}')"
//                                     kubectl_version="$(kubectl version --short | awk -F- '/Server Version: /{print $1}')"
//                                     body_payload="(APP_IMAGE:$app_image) --- (OPERATOR_VERSION:$operator_version) --- (kUBECTL_VERSION:$kubectl_version)"
//                                     echo "$body_payload"
//                                     curl -H "Content-Type: application/json" -u "pandey.rishav@cnvrg.io:MUmmy@papa123" -d '{"description": "'"${body_payload}"'"}' "https://cnvrg.testrail.io/index.php?/api/v2/update_run/${RUN_ID}"
//                                 '''
//                             }
//                         }
//                     }
//                 }
//                 stage("CICD Aut"){
//                     steps{
//                         script {
//                             echo "${DEFAULT_URL}"
//                             def cluster = env.DEFAULT_URL.split("app.")[1].split(".cicd.cnvrg")[0]
//                             echo "${cluster}"
//                             // sh 'ls'
//                             env.z_value = env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger()*3
//                             sh "echo $RANDOM"
//                             env.RANDOM = params.RANDOM.toLowerCase()
//                             sh "echo $RANDOM"
//                             if(env.ONE_CLICK == "false") {
//                             def res = build job: 'Get Kube CICD Clusters', parameters: [
//                                 string(name: 'CLUSTER_NAME', value: cluster)
//                                 ], wait: true, propagate: false
//                             env.kubeconfig = res.getBuildVariables().Kubeconfig_File_QA
//                             env.kubeconfig = env.kubeconfig.toString().trim()
//                             sh """cat <<-EOF>\${WORKSPACE}/kubeconfig
//                             $kubeconfig"""
//                             sh """sed "1 s/^[ \t]*//" -i kubeconfig"""  
//                             }
//                             // def tag = build job: 'Tag CICD Clusters', parameters: [
//                             //     string(name: 'CLUSTER_NAME', value: cluster)
//                             //     ], wait: true, propagate: false
//                             echo "${WORKSPACE}"
//                             if(env.REMOTE_URL == 'http://testgrid.cnvrg.io/wd/hub' && env.ZALENIUM == 'true'){ 
//                                 def Responseqa = build job: 'QA-Zalenium', parameters: [string(name: 'PARALLEL_QA_NUMBER', value: env.z_value)], wait: true, propagate: true 
//                                 env.REMOTE_URL = Responseqa.getBuildVariables().Env_Remote_url  
//                                 env.BUILD_NUMBER_DELETE = Responseqa.getBuildVariables().Env_Build_Number   
//                                 sleep 300   
//                             }
//                             // if (env.REMOTE_URL == 'http://testgrid.cnvrg.io/wd/hub' && env.ONE_CLICK == 'false') {
//                             //     def res = build job: 'Get Kube CICD Clusters', parameters: [
//                             //         string(name: 'CLUSTER_NAME', value: cluster)
//                             //         ], wait: true, propagate: false
//                             //     env.kubeconfig = res.getBuildVariables().Kubeconfig_File_QA
//                             //     env.kubeconfig = env.kubeconfig.toString().trim()
//                             //     def tag = build job: 'Tag CICD Clusters', parameters: [
//                             //         string(name: 'CLUSTER_NAME', value: cluster)
//                             //         ], wait: true, propagate: false
//                             //     echo "${WORKSPACE}"
//                             //     sh """cat <<-EOF>\${WORKSPACE}/kubeconfig
//                             //     $kubeconfig"""
//                             //     sh """sed "1 s/^[ \t]*//" -i kubeconfig"""
//                             //}
//                             def USER_ID ="${currentBuild.getBuildCauses()[0].userId}"
//                             env.UserID = USER_ID
//                             withCredentials([usernamePassword(credentialsId: 'QA_AWS', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
//                                 sh '''docker build --build-arg RUN_ID=$RUN_ID --build-arg SSO=$SSO --build-arg JENKINS_USER_ID=$UserID --build-arg RUN_STATUS=$RUN_STATUS --build-arg BUILD_URL=$BUILD_URL --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES="${PARALLEL_SPLIT_TEST_PROCESSES}" -t $RANDOM .'''
//                                 sh 'docker ps'
//                                 sh '''
//                                     cont_id=$(docker create $RANDOM)
//                                     echo $cont_id
//                                     ls
//                                     sleep 10
//                                 '''

//                                 sh '''
//                                     docker run -e PARALLEL_SPLIT_TEST_PROCESSES=1 -e REMOTE_URL=${REMOTE_URL} -e PREEMPTION=true -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false  ${RANDOM}:latest
//                                 '''
                        
//                                 if (env.RETEST=='true'){
//                                     def reteststatus = 'Retest,Failed,Warning,Untested,Blocked'
//                                     sh '''
//                                         docker run -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e REMOTE_URL=${REMOTE_URL} -e PARALLEL_SPLIT_TEST_PROCESSES=${PARALLEL_SPLIT_TEST_PROCESSES} -e PREEMPTION=false -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false -e PARALLELFILE=cicdfile ${RANDOM}:latest
//                                         docker run -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e REMOTE_URL=${REMOTE_URL} -e PARALLEL_SPLIT_TEST_PROCESSES=1 -e PREEMPTION=true -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false -e PARALLELFILE=cicdfile ${RANDOM}:latest
//                                         docker run -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e REMOTE_URL=${REMOTE_URL} -e PARALLEL_SPLIT_TEST_PROCESSES=${PARALLEL_SPLIT_TEST_PROCESSES} -e PREEMPTION=false -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false -e PARALLELFILE=cicdfile ${RANDOM}:latest
//                                         docker run -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e REMOTE_URL=${REMOTE_URL} -e PARALLEL_SPLIT_TEST_PROCESSES=1 -e PREEMPTION=true -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false -e PARALLELFILE=cicdfile ${RANDOM}:latest
//                                         docker run -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e REMOTE_URL=${REMOTE_URL} -e PARALLEL_SPLIT_TEST_PROCESSES=${PARALLEL_SPLIT_TEST_PROCESSES} -e PREEMPTION=false -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false -e PARALLELFILE=cicdfile ${RANDOM}:latest
//                                         docker run -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e REMOTE_URL=${REMOTE_URL} -e PARALLEL_SPLIT_TEST_PROCESSES=1 -e PREEMPTION=true -e RERUN=false -e RUN_ID=${RUN_ID} -e RETEST=false -e PARALLELFILE=cicdfile ${RANDOM}:latest
//                                     '''
//                                 }
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//         stage("Create Zalenium") {
//             when{
//                 allOf{
//                     environment name: 'SINGLESIGNUP', value: 'true'
//                     environment name: 'ZALENIUM', value: 'true'
//                 }
//             }
//             steps {
//                 script {
//                     env.z_value = env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger()*2
//                     def Responseqa = build job: 'QA-Zalenium', parameters: [string(name: 'PARALLEL_QA_NUMBER', value: env.z_value)], wait: true, propagate: true
//                     env.REMOTE_URL = Responseqa.getBuildVariables().Env_Remote_url
//                     env.BUILD_NUMBER_DELETE = Responseqa.getBuildVariables().Env_Build_Number
//                     sleep 180
//                 }
//             }
//         }

//         stage("Create Tenant for one click automation"){
//             when{
//                 allOf{
//                     environment name: 'SINGLESIGNUP', value: 'true'
//                     environment name: 'ONE_CLICK', value: 'true'
//                 }
//             }
//             steps {
//                 script {
//                     sh "echo $RANDOM"
//                     env.RANDOMPONE = params.RANDOM.toLowerCase() + 'pone'
//                     sh "echo $RANDOMPONE"
//                     def USER_ID ="${currentBuild.getBuildCauses()[0].userId}"
//                     env.UserID = USER_ID
//                     withCredentials([usernamePassword(credentialsId: 'QA_AWS', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
//                         sh '''docker build -f Dockerfiletenant --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES="${PARALLEL_SPLIT_TEST_PROCESSES}" -t $RANDOMPONE .'''
//                         sh 'docker ps'
//                         sh '''
//                             cont_id_one=$(docker create $RANDOMPONE)
//                             echo $cont_id_one
//                             docker cp $cont_id_one:/cnvrg_aut/environment_var_change_1.sh $(pwd)/environment_var_change_1.sh
//                             docker cp $cont_id_one:/cnvrg_aut/environment_var_change_2.sh $(pwd)/environment_var_change_2.sh
//                             docker cp $cont_id_one:/cnvrg_aut/environment_var_change_3.sh $(pwd)/environment_var_change_3.sh
//                             docker cp $cont_id_one:/cnvrg_aut/environment_var_change_4.sh $(pwd)/environment_var_change_4.sh
//                         '''
//                         script{
//                         def tenant = sh(script: "sh environment_var_change_1.sh", returnStdout: true).trim()
//                         env.DEFAULT_URL = tenant
//                         def resource = sh(script: "sh environment_var_change_2.sh", returnStdout: true).trim()
//                         env.USER = resource
//                         def org = sh(script: "sh environment_var_change_3.sh", returnStdout: true).trim()
//                         env.ORG = org
//                         def user_token = sh(script: "sh environment_var_change_4.sh", returnStdout: true).trim()
//                         env.USER_TOKEN = user_token
//                         }
//                     }
//                 }
//             }
//         }

//         stage("Aut on tenant"){
//             when{
//                 allOf{
//                     environment name: 'SINGLESIGNUP', value: 'true'
//                     environment name: 'ONE_CLICK', value: 'false'
//                 }
//             }
//             parallel {
//                 stage("Default Scenarios") {
//                     when{
//                         environment name: 'DEFAULT', value: 'true'
//                     }
//                     steps {
//                         script {
//                             sh "echo $RANDOM"
//                             env.RANDOMPONE = params.RANDOM.toLowerCase() + 'pone'
//                             sh "echo $RANDOMPONE"
//                             def USER_ID ="${currentBuild.getBuildCauses()[0].userId}"
//                             env.UserID = USER_ID
//                             withCredentials([usernamePassword(credentialsId: 'QA_AWS', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
//                                 sh '''docker build -f Dockerfiletenant --build-arg STORAGE_TYPE=$STORAGE_TYPE --build-arg CUSTOM_RESOURCE_NAME=$CUSTOM_RESOURCE_NAME --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES="${PARALLEL_SPLIT_TEST_PROCESSES}" -t $RANDOMPONE .'''
//                                 sh 'docker ps'
//                                 sh'''
//                                     cont_id_one=$(docker create $RANDOMPONE)
//                                     echo $cont_id_one
//                                     docker ps -a
//                                     docker cp $cont_id_one:/cnvrg_aut/recreate_environment.sh $(pwd)/recreate_environment.sh
//                                 '''
//                                 script{
//                                 def again = sh(script: "sh recreate_environment.sh", returnStdout: true).trim()
//                                 env.RUN_AGAIN = again
//                                 }
//                                 if (env.RUN_AGAIN=='false'){
//                                     sh '''docker build -f Dockerfiletenant --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES="${PARALLEL_SPLIT_TEST_PROCESSES}" -t $RANDOMPONE .'''
//                                     sh 'docker ps'
//                                 }
//                                 sh '''
//                                     cont_id_one=$(docker create $RANDOMPONE)
//                                     echo $cont_id_one
//                                     docker ps -a
//                                     docker cp $cont_id_one:/cnvrg_aut/environment_var_change_1.sh $(pwd)/environment_var_change_1.sh
//                                     docker cp $cont_id_one:/cnvrg_aut/environment_var_change_2.sh $(pwd)/environment_var_change_2.sh
//                                     docker cp $cont_id_one:/cnvrg_aut/environment_var_change_3.sh $(pwd)/environment_var_change_3.sh
//                                 '''
//                                 script{
//                                 def tenant = sh(script: "sh environment_var_change_1.sh", returnStdout: true).trim()
//                                 env.DEFAULT_URL_ONE = tenant
                                
//                                 def resource = sh(script: "sh environment_var_change_2.sh", returnStdout: true).trim()
//                                     env.USER_ONE = resource
                                
//                                 def org = sh(script: "sh environment_var_change_3.sh", returnStdout: true).trim()
//                                     env.ORG_ONE = org
//                                 }
//                                 sh '''
//                                 docker run -e DEFAULT_URL=${DEFAULT_URL_ONE} -e REMOTE_URL=${REMOTE_URL} -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=false -e USER=${USER_ONE} -e ORG=${ORG_ONE}  $RANDOMPONE:latest
//                                 '''
//                                 if (env.RETEST=='true'){
//                                     def reteststatus = 'Retest,Failed,Warning,Untested,Blocked'
//                                     sh '''
//                                     docker run -e DEFAULT_URL=${DEFAULT_URL_ONE} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=false -e USER=${USER_ONE} -e ORG=${ORG_ONE} -e PARALLELFILE=defaultfile $RANDOMPONE:latest
//                                     docker run -e DEFAULT_URL=${DEFAULT_URL_ONE} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=false -e USER=${USER_ONE} -e ORG=${ORG_ONE} -e PARALLELFILE=defaultfile $RANDOMPONE:latest
//                                     docker run -e DEFAULT_URL=${DEFAULT_URL_ONE} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=false -e USER=${USER_ONE} -e ORG=${ORG_ONE} -e PARALLELFILE=defaultfile $RANDOMPONE:latest
//                                     '''
//                                 }
//                             }
//                         }
//                     }
//                 }
//                 stage("Billing Scenarios") {
//                     when{
//                         allOf{
//                             environment name: 'BILLING', value: 'true'
//                             environment name: 'BILLING_NEW_TENANT', value: 'true'
//                         }
//                     }
//                     steps {
//                         script {
//                             env.z_value = env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger()*2
//                             // sh "echo $RANDOM"
//                             env.RANDOMPTWO = params.RANDOM.toLowerCase() + 'ptwo'
//                             sh "echo $RANDOMPTWO"
//                             def USER_ID ="${currentBuild.getBuildCauses()[0].userId}"
//                             env.UserID = USER_ID
//                             env.EMPTYVar = ''
//                             withCredentials([usernamePassword(credentialsId: 'QA_AWS', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
//                                 sh '''docker build -f Dockerfiletenant --build-arg SCENARIOS=billing --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES=3 -t $RANDOMPTWO .'''
//                                 sh 'docker ps'
//                                 sh'''
//                                     cont_id_two=$(docker create $RANDOMPTWO)
//                                     echo $cont_id_two
//                                     docker cp $cont_id_two:/cnvrg_aut/recreate_environment.sh $(pwd)/recreate_environment.sh
//                                 '''
//                                 script{
//                                 def again = sh(script: "sh recreate_environment.sh", returnStdout: true).trim()
//                                 env.RUN_AGAIN = again
//                                 }
//                                 if (env.RUN_AGAIN=='false'){
//                                     sh '''docker build -f Dockerfiletenant --build-arg SCENARIOS=billing --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES=3 -t $RANDOMPTWO .'''
//                                     sh 'docker ps'
//                                 }
//                                 sh '''
//                                     cont_id_two=$(docker create $RANDOMPTWO)
//                                     echo $cont_id_two
//                                     docker cp $cont_id_two:/cnvrg_aut/environment_var_change_1.sh $(pwd)/tenant_url_billing.sh
//                                     docker cp $cont_id_two:/cnvrg_aut/environment_var_change_2.sh $(pwd)/resource_billing_storage.sh
//                                     docker cp $cont_id_two:/cnvrg_aut/environment_var_change_3.sh $(pwd)/org_billing_storage.sh
//                                 '''
//                                 script{
//                                 def tenant = sh(script: "sh tenant_url_billing.sh", returnStdout: true).trim()
//                                 env.DEFAULT_URL_TWO = tenant
                                
//                                 def resource = sh(script: "sh resource_billing_storage.sh", returnStdout: true).trim()
//                                     env.USER_TWO = resource
                                
//                                 def org = sh(script: "sh org_billing_storage.sh", returnStdout: true).trim()
//                                     env.ORG_TWO = org
//                                 }
//                                 sh '''
//                                 echo $DEFAULT_URL_TWO
//                                 echo $USER_TWO
//                                 docker run -e DEFAULT_URL=${DEFAULT_URL_TWO} -e REMOTE_URL=${REMOTE_URL} -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=true -e COMMUNITY_STATUS=true -e USER=${USER_TWO} -e ORG=${ORG_TWO}  $RANDOMPTWO:latest
//                                 '''
//                                 if (env.RETEST=='true'){
//                                     def reteststatus = 'Retest,Failed,Warning,Untested,Blocked'
//                                     sh '''
//                                     docker run -e DEFAULT_URL=${DEFAULT_URL_TWO} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=true -e USER=${USER_TWO} -e ORG=${ORG_TWO} -e PARALLELFILE=billingfile $RANDOMPTWO:latest
//                                     docker run -e DEFAULT_URL=${DEFAULT_URL_TWO} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=true -e USER=${USER_TWO} -e ORG=${ORG_TWO} -e PARALLELFILE=billingfile $RANDOMPTWO:latest
//                                     docker run -e DEFAULT_URL=${DEFAULT_URL_TWO} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY=true -e USER=${USER_TWO} -e ORG=${ORG_TWO} -e PARALLELFILE=billingfile $RANDOMPTWO:latest
//                                     '''
//                                     if (env.CUSTOM_RESOURCE_NAME != ''){
//                                         sh '''
//                                             docker run -e DEFAULT_URL=${DEFAULT_URL_TWO} -e REMOTE_URL=${REMOTE_URL} -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY='storageandqueues' -e USER=${USER_TWO} -e ORG=${ORG_TWO}  $RANDOMPTWO:latest
//                                             docker run -e DEFAULT_URL=${DEFAULT_URL_TWO} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY='storageandqueues' -e USER=${USER_TWO} -e ORG=${ORG_TWO} -e PARALLELFILE=queuesStorage $RANDOMPTWO:latest
//                                         '''
//                                     }
//                                 }
//                             }
//                         }
//                     }
//                 }
//                 stage("Queues and Storage Scenarios") {
//                     when{
//                         allOf{
//                             environment name: 'STORAGE', value: 'true'
//                             environment name: 'QUEUES_STORAGE_NEW_TENANT', value: 'true'
//                         }
//                     }
//                     steps {
//                         script {
//                             env.z_value = env.PARALLEL_SPLIT_TEST_PROCESSES.toInteger()*2
//                             // sh "echo $RANDOM"
//                             env.RANDOMP3 = params.RANDOM.toLowerCase() + 'pthree'
//                             sh "echo $RANDOMP3"
//                             def USER_ID ="${currentBuild.getBuildCauses()[0].userId}"
//                             env.UserID = USER_ID
//                             env.EMPTYVar1 = ''
//                             withCredentials([usernamePassword(credentialsId: 'QA_AWS', passwordVariable: 'AWS_SECRET_ACCESS_KEY', usernameVariable: 'AWS_ACCESS_KEY_ID')]) {
//                                 sh '''docker build -f Dockerfiletenant --build-arg SCENARIOS=queues --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES=3 -t $RANDOMP3 .'''
//                                 sh 'docker ps'
//                                 sh'''
//                                     cont_id_three=$(docker create $RANDOMP3)
//                                     echo $cont_id_three
//                                     docker cp $cont_id_three:/cnvrg_aut/recreate_environment.sh $(pwd)/recreate_environment.sh
//                                 '''
//                                 script{
//                                 def again = sh(script: "sh recreate_environment.sh", returnStdout: true).trim()
//                                 env.RUN_AGAIN = again
//                                 }
//                                 if (env.RUN_AGAIN=='false'){
//                                     sh '''docker build -f Dockerfiletenant --build-arg SCENARIOS=queues --build-arg UITENANT=$UITENANT --build-arg ZALENIUM=$ZALENIUM --build-arg TENANT_IMAGE=$TENANT_IMAGE --build-arg TENANT_EMAIL=$TENANT_EMAIL --build-arg BUILD_URL=$BUILD_URL --build-arg TENANT_URL=$TENANT_URL --build-arg TENANT_ORG_NAME=$TENANT_ORG_NAME --build-arg TENANT_USER_NAME=$TENANT_USER_NAME --build-arg RUN_ID=$RUN_ID --build-arg RUN_STATUS=$RUN_STATUS --build-arg RERUN=$RERUN --build-arg AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY --build-arg AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID --build-arg REMOTE_URL=$REMOTE_URL --build-arg DEFAULT_URL=$DEFAULT_URL --build-arg NEW_TESTRUN_TITLE="${NEW_TESTRUN_TITLE}" --build-arg PARALLEL_SPLIT_TEST_PROCESSES=3 -t $RANDOMP3 .'''
//                                     sh 'docker ps'
//                                 }
//                                 sh '''
//                                     cont_id_three=$(docker create $RANDOMP3)
//                                     echo $cont_id_three
//                                     docker cp $cont_id_three:/cnvrg_aut/environment_var_change_1.sh $(pwd)/tenant_url_queues_storage.sh
//                                     docker cp $cont_id_three:/cnvrg_aut/environment_var_change_2.sh $(pwd)/resource_queues_storage.sh
//                                     docker cp $cont_id_three:/cnvrg_aut/environment_var_change_3.sh $(pwd)/org_queues_storage.sh
//                                 '''
//                                 script{
//                                 def tenant = sh(script: "sh tenant_url_queues_storage.sh", returnStdout: true).trim()
//                                 env.QUEUES_STORAGE_TENANT_URL = tenant

//                                 def resource = sh(script: "sh resource_queues_storage.sh", returnStdout: true).trim()
//                                     env.QUEUES_STORAGE_RESOURCE = resource
                                
//                                 def org = sh(script: "sh org_queues_storage.sh", returnStdout: true).trim()
//                                     env.QUEUES_STORAGE_ORG = org
//                                 }
//                                 sh '''
//                                 echo $QUEUES_STORAGE_TENANT_URL
//                                 echo $QUEUES_STORAGE_RESOURCE
//                                 echo $QUEUES_STORAGE_ORG
//                                 docker run -e DEFAULT_URL=${QUEUES_STORAGE_TENANT_URL} -e REMOTE_URL=${REMOTE_URL} -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY='storageandqueues' -e USER=${QUEUES_STORAGE_RESOURCE} -e ORG=${QUEUES_STORAGE_ORG}  $RANDOMP3:latest
//                                 '''
//                                 if (env.RETEST=='true'){
//                                     def reteststatus = 'Retest,Failed,Warning,Untested,Blocked'
//                                     sh '''
//                                     docker run -e DEFAULT_URL=${QUEUES_STORAGE_TENANT_URL} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY='storageandqueues' -e USER=${QUEUES_STORAGE_RESOURCE} -e ORG=${QUEUES_STORAGE_ORG} -e PARALLELFILE=queuesStorage $RANDOMP3:latest
//                                     docker run -e DEFAULT_URL=${QUEUES_STORAGE_TENANT_URL} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY='storageandqueues' -e USER=${QUEUES_STORAGE_RESOURCE} -e ORG=${QUEUES_STORAGE_ORG} -e PARALLELFILE=queuesStorage $RANDOMP3:latest
//                                     docker run -e DEFAULT_URL=${QUEUES_STORAGE_TENANT_URL} -e REMOTE_URL=${REMOTE_URL} -e RUN_STATUS='Retest,Failed,Warning,Untested,Blocked' -e RERUN=false -e RUN_ID=${RUN_ID} -e COMMUNITY='storageandqueues' -e USER=${QUEUES_STORAGE_RESOURCE} -e ORG=${QUEUES_STORAGE_ORG} -e PARALLELFILE=queuesStorage $RANDOMP3:latest
//                                     '''
//                                 }
//                             }
//                         }
//                     }
//                 }  
//             }
//         }
//     }
    // post {
    //   always {
    //     script {
    //         if (env.SINGLESIGNUP == 'false'){
    //             sh '''
    //             if [ -e result-json.sh ]; then
    //                 export KUBECONFIG=kubeconfig
    //                 bash result-json.sh $RUN_ID
    //                 kubectl cp automation-results.json sdk-aut:/cnvrg_sdk_aut/statusServer/results/automation-results$RANDOM.json -c python -n cnvrg
    //             fi
    //             '''
    //         }
    //         if (env.BUILD_NUMBER_DELETE != '') {
    //             sh 'echo always after test'
    //             def res = build job: 'QA-Zalenium', parameters: [
    //             string(name: 'Create_or_Delete', value: 'delete'),
    //             string(name: 'BUILD_NUMBER_DELETE', value: env.BUILD_NUMBER_DELETE)
    //             ], wait: true, propagate: false
    //             sh 'echo always after test2'
    //         }
    //         if (env.REMOTE_URL != 'http://testgrid.cnvrg.io/wd/hub' && env.ONE_CLICK == 'true') {
    //             sh '''
    //                 export KUBECONFIG=kubeconfig
    //                 kubectl -n cnvrg delete deployment zalenium-$build_no
    //             '''
    //         }

    //     }
    //   }
    // }
}
