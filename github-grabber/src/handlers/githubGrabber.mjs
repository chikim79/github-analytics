import axios from 'axios';
import {
    S3Client,
    CreateBucketCommand,
    PutObjectCommand,
    ListObjectsCommand,
    CopyObjectCommand,
    GetObjectCommand,
    DeleteObjectsCommand,
    DeleteBucketCommand,
} from "@aws-sdk/client-s3";

const bucketName = 'gh-analytics-csca5028-build'

const s3 = new S3Client({ region: 'us-east-1' });

const processRepo = async (repo) => {
    const { id, full_name, html_url, description, forks, default_branch } = repo;

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

    return null;
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
            const { processed, repoData } = processRepo(item);
        }

    } catch (error) {
        // Error handling
        console.error('Error fetching repositories:', error);
    }

}

