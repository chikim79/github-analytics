import https from 'https';
import {
    S3Client,
    PutObjectCommand,
} from "@aws-sdk/client-s3";
import { SQSClient, SendMessageCommand } from "@aws-sdk/client-sqs";

const bucketName = 'gh-analytics-csca5028-build'
const sqsQueueUrl = 'https://sqs.us-east-1.amazonaws.com/096792111890/gh-analytics';

const s3 = new S3Client({ region: 'us-east-1' });
const sqs = new SQSClient({ region: 'us-east-1' });


const fetchFile = (url) => {
    return new Promise((resolve, reject) => {
        https.get(url, { headers: { 'User-Agent': 'Node.js' } }, (res) => {
            let data = '';
            res.on('data', (chunk) => {
                data += chunk;
            });
            res.on('end', () => {
                if (res.statusCode === 200) {
                    resolve(data);
                } else {
                    reject(`Request failed. Status code: ${res.statusCode}`);
                }
            });
        }).on('error', (err) => {
            reject(err.message);
        });
    });
};


const uploadBuildFileToS3 = async (repo) => {
    const { id, full_name, html_url, description, forks_count, default_branch, stargazers_count, watchers_count } = repo;

    let pomFile = `https://raw.githubusercontent.com/${full_name}/refs/heads/${default_branch}/pom.xml`;

    let gradleFile = `https://raw.githubusercontent.com/${full_name}/refs/heads/${default_branch}/build.gradle`;

    let buildFile = null;
    let builder = null;
    try {
        const pomData = await fetchFile(pomFile);
        buildFile = pomData;
        builder = 'maven';
    } catch (error) {
        try {
            const gradleData = await fetchFile(gradleFile);
            buildFile = gradleData;
            builder = 'gradle';
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
    const url = 'https://api.github.com/search/repositories?q=language:java&page=1&per_page=30';

    try {
        // Making the GET request to the GitHub API
        const response = await fetchFile(url);
        const responseData = JSON.parse(response);

        for (const item of responseData.items) {
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

