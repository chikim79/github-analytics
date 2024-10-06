# CSCA 5028

## Background
This Project is intended to analyze public github repositories in Java languagea and provide statistics depending on various metrics.  Github search API provides various metadata about the each repository but does not provide any data on what the java project is made of.  This project is about using the power of Large Language Model to process either the Maven or Gradle build file for each project to understand a little more about each project.  It is meant to be used by data analysts for further processing of the data.

## Architecture

The system is made up of following major components.

- Github Search API Grabber
- Backend Processor
- Frontend

### Other AWS Services

- AWS SQS : Used for asynchronous processing between API Grabber and Backend
- AWS S3: Used to store build file and for static hosting
- AWS Bedrock: To invoke Claude LLM
- AWS Elastic Beanstalk: To host Spring Boot app.
- AWS EventBridge: For Scheduled Lambda Invocation

## CI/CD

Each component is deployed using `Github action`.  Each component can be automatically deployed using respective mechanism when code change is pushed to github. (Automatic deployment is commented out currently)

## Components

### Github Search API Grabber

Platform: AWS Lambda (Javascript)
Deployment: AWS SAM
CI/CD: Github Action
Invocation: Scheduled Cron job using AWS EventBridge (Commented out)

Githb Search API Grabber's job is to make Github search API about java application and pass on the data for downstream processing.
First it makes the API call to Github using `https://api.github.com/search/repositories?q=language:java&page=1&per_page=30`
Second, it parses important metadata from the response, like id, description, number of forks, and number of stars.
Then, it tries to donwload either `build.gradle` or `pom.xml` if such file exists.  If none exists, it skips that repository.
After download the build file, it stores them into S3 bucket for later processing.
Finally, it emits SQS message for Backend Component to process.

#### Reason for Asynchronouse Processing

Search API is scheduled to be invoked on schedule.  It's possible for this schedule to be frequent at times, and slow at another time, it provides enough buffer on the backend platform to process the payload whenever possible if processing potentially takes a long time.

### Backend Component

Platform: Spring Boot (Java)
Deployment: AWS Elastic Beanstalk
CI/CD: Github Action
Automated Testing: JUnit

Backend component processes the SQS message, then downloads the build file from S3.  
Then it submits the buildfile into Claude Large Language Model via AWS Bedrock with prompt asking to respond with json.
The JSON file from LLM is expected to have some analytical data such as `Top Dependency` and `Build tool version`
After appending the extra analysis using LLM, it store the data into local MySQL using JPA.

### Frontend Component

Platform: React (Javascript)
Deployment: S3 Static Hosting
CI/CD: Github Action
Automated Testing: Jest

Frontend Component  is a simple React app that displays the JSON response from Backend into a HTML table.
