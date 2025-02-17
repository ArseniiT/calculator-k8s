pipeline {
   agent any
   stages {
 	stage("checkout"){
   	steps {
     	echo "récupération du projet"
     	git branch: 'main',
     	credentialsId: 'jenkinsgitlabssh',
     	url: 'git@gitlab.gretadevops.com:ars/calculator.git'
   	}
 	}
 	stage("permissions") {
       steps {
         echo "ajustement des permissions"
         sh 'chmod +x ./mvnw'
       }
     }
 	stage("compile"){
   	steps{
     	echo "compilation du projet"
     	sh './mvnw compile'
   	}
 	}
 	stage("tests"){
    	steps{
      	echo "test unitaire et test d'integration"
      	sh './mvnw test'
    	}
 	}
 	stage("package"){
   	steps{
     	echo "création du package de l'application"
     	sh './mvnw package'
   	}
 	}
 	stage("image docker"){
   	steps{
     	echo "création de l'image docker"
	sh 'docker build -t registry.gretadevops.com:5000/calculator .'
   	}
 	}
 	stage("push registry"){
   	steps{
     	echo "push de l'image sur le registry"
	sh 'docker push registry.gretadevops.com:5000/calculator'

   	}
 	}
 	stage("deploiement") {
    steps {
        script {
            echo "Deploying the application..."
            
            // Start the Docker container
            try {
                sh """
                docker run -d --name calculatortestcontainer -p 8080:8080 --net=gretadevops.com registry.gretadevops.com:5000/calculator
                """
                
                // Wait for the application to start
                sh "sleep 10"
                
                // Run automated tests
                echo "Running automated tests..."
                def testResult = sh(
                    script: "curl -s http://calculatortestcontainer:8080/sum?a=5\\&b=6 | grep -q 11",
                    returnStatus: true
                )
                
                if (testResult != 0) {
                    error("Tests failed! Pipeline interrupted.")
                }
                
                echo "Tests passed successfully!"
            } finally {
                // Clean up the container
                echo "Cleaning up the test container..."
                sh """
                docker stop calculatortestcontainer || true
                docker rm calculatortestcontainer || true
                """
            }
        }
    }
}


   }
}
