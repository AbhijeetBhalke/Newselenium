#!groovy
//Jenkinsfile (Declarative Pipeline)

// -- Suite Name
#def SUITE_NAME

// -- Keep only 15 builds
echo 'Discard old build'
properties([buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', 
    daysToKeepStr: '', numToKeepStr: "15"))])

pipeline {
    agent any
    // -- Display a timestamp on the log.
    options{timestamps()}
    stages {
    
    // Parameters needed: JOB_GIT_BRANCH, JOB_GIT_URL, GIT_CREDENTIAL
    // --------------------------------
    // -- STAGE: Download GIT Code
    // --------------------------------
    stage("Download GIT Code") {
        steps {
            script {
                try {
                   // -- Download GIT Code 
                    git clone "https://github.com/AbhijeetBhalke/Newselenium.git"

                } catch (err) { 
                    echo "The Download GIT Code Stage failed"                                          
                }
            }   
        }
    }

    // Parameters needed: JOB_OS, JOB_BROWSER, SUITE_NAME 
    // --------------------------------
    // -- STAGE: Selenium Test
    // --------------------------------
    stage("Selenium Test") {
        steps {
            script {
                // -- Script to launch Appium Test
                script {
                    try {
                        sh "mvn clean test"
                        echo "Publishing Junit Results"
                        junit "**/target/surefire-reports/junitreports/*.xml"

                    } catch (err) { 
                        echo "Archiving Screenshot of the Failed Tests"
                        archiveArtifacts "**/screenshot/*.png"
                        echo "Publishing Junit Results"
                        junit "**/target/surefire-reports/junitreports/*.xml"
                    }
                }   
            }
        }
    }

    } // -- End stage
    // ----------------------------------------------
    // -- STAGE: Post Build actions
    // ----------------------------------------------
    post ("Post-Build Actions"){
        always {
            //-- Delete Directory
            echo "Delete Directory"
            deleteDir() 
        }
        success ("JOB SUCCESS"){
            echo "Success Job"
        }

        failure ("JOB FAILURE"){
            echo "Failure Job"
        }

        unstable ("JOB UNSTABLE") {
            echo "Unstable Job"
        }

        aborted ("JOB ABORTED") {
            echo "Aborted Job"
        }
    }
}
