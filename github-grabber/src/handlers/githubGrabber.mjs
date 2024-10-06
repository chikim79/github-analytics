import axios from 'axios';
import {
    S3Client,
    PutObjectCommand,
} from "@aws-sdk/client-s3";
import { SQSClient, SendMessageCommand } from "@aws-sdk/client-sqs";

const bucketName = 'gh-analytics-csca5028-build'
const sqsQueueUrl = 'https://sqs.us-east-1.amazonaws.com/096792111890/gh-analytics';

const s3 = new S3Client({ region: 'us-east-1' });
const sqs = new SQSClient({ region: 'us-east-1' });

const uploadBuildFileToS3 = async (repo) => {
    const { id, full_name, html_url, description, forks_count, default_branch, stargazers_count, watchers_count } = repo;

    let pomFile = `https://raw.githubusercontent.com/${full_name}/refs/heads/${default_branch}/pom.xml`;

    let gradleFile = `https://raw.githubusercontent.com/${full_name}/refs/heads/${default_branch}/build.gradle`;

    let buildFile = null;
    let builder = null;
    try {
        const response = await axios.get(pomFile);
        // Check if the response is OK (status 200)
        if (response.status === 200) {
            buildFile = response.data;
            builder = 'maven';
        }
    } catch (error) {
        // Handle the 404 or other errors gracefully
        try {
            const response = await axios.get(gradleFile);
            // Check if the response is OK (status 200)
            if (response.status === 200) {
                buildFile = response.data;
                builder = 'gradle';
            }
        } catch (error) {
            return null;
        }
    }

    const key = `${id}/buildfile`;

    await s3.send(new PutObjectCommand({
        Bucket: bucketName,
        Key: key,
        Body: buildFile,
    }));

    return { id, full_name, html_url, description, forks_count, stargazers_count, watchers_count, builder, filePath: key };
}

const sendToSqs = async (data) => {

    await sqs.send(new SendMessageCommand({
        QueueUrl: sqsQueueUrl,
        MessageBody: JSON.stringify(data),
    }));
}

export const handler = async (event, context) => {
    // All log statements are written to CloudWatch by default. For more information, see
    // https://docs.aws.amazon.com/lambda/latest/dg/nodejs-prog-model-logging.html
    console.info(JSON.stringify(event));
    const url = 'https://api.github.com/search/repositories';
    const params = {
        q: 'language:java',  // Query for Java repositories
        page: 1,             // Pagination: page number 1
        per_page: 30         // Return 30 repositories per page
    };

    try {
        // Making the GET request to the GitHub API
        const response = await axios.get(url, { params });

        for (const item of response.data.items) {
            const fileData = await uploadBuildFileToS3(item);
            if (fileData) { // fileData can be null, if unable to download builder

                sendToSqs(fileData);

            }
        }

    } catch (error) {
        // Error handling
        console.error('Error fetching repositories:', error);
    }

}

