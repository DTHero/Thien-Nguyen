# website-react

Website: react, scaffold, basic

**Demonstrate:**
- **Create a new React App** - Create a react website scaffold, run it 
  locally, and build it to create deployment static files in the `build`
  folder
- Manually - deploy react app to aws s3 for website hosting

## Prerequisite

- Node.js v14.x or later
- npm v6.14.4 or later

## Create a new React App

- Tested versions
```
$ node --version
v18.12.0
$ npm --version
8.19.2
$ npx --version
8.19.2
```
- This repo already contains the `aws-website-react`. To create the react app scaffold
```
$ npx create-react-app aws-website-react
```
- List initial files
```
$ cd aws-website-react
$ ls
README.md  node_modules  package-lock.json  package.json  public  src
```
- Run
```
$ npm start
Compiled successfully!

You can now view aws-website-react in the browser.

  Local:            http://localhost:3000
  On Your Network:  http://172.19.183.38:3000

Note that the development build is not optimized.
To create a production build, use npm run build.

webpack compiled successfully
```
- Build
```
$ npm run build

> aws-website-react@0.1.0 build
> react-scripts build

Creating an optimized production build...
Compiled successfully.

File sizes after gzip:

  46.63 kB  build/static/js/main.b1cf6321.js
  1.79 kB   build/static/js/787.aa1b8d16.chunk.js
  541 B     build/static/css/main.073c9b0a.css

The project was built assuming it is hosted at /.
You can control this with the homepage field in your package.json.

The build folder is ready to be deployed.
You may serve it with a static server:

  npm install -g serve
  serve -s build

Find out more about deployment here:

  https://cra.link/deployment
```
- List build files
```
$ cd aws-website-react
$ ls
README.md  build  node_modules  package-lock.json  package.json  public  src
$ ls build
asset-manifest.json  favicon.ico  index.html  logo192.png  logo512.png  manifest.json  robots.txt  static
$ ls build/static
css  js  media
```

## Manually - deploy react app to aws s3 for website hosting

Use Amazon S3 to host a static website; and Cloudfront for the Content
Distribution Network (CDN).

**Source:**
- [Tutorial: Configuring a static website on Amazon S3](https://docs.aws.amazon.com/AmazonS3/latest/userguide/HostingWebsiteOnS3Setup.html)

### SETUP s3 - Step by Step instructions:

- If not already, create the react app, and copy the `error.html` file
  to the public folder:
```
$ npx create-react-app aws-website-react
$ cp error.html aws-website-react/public
$ cd aws-website-react
$ ls
README.md  node_modules  package-lock.json  package.json  public  src
```
- If not already, build. The `build` folder contains the website static
  files that will be uploaded to s3 bucket
```
$ npm run build
$ ls
README.md  build  node_modules  package-lock.json  package.json  public  src
```
- PERFORM the following from the AWS CONSOLE
- create a bucket: 
  - Bucket name: `aws-website-react`
    - NOTE: for production website with a custom domain, we should name
      the s3 bucket using the target domain, for example, `example.com`
      or `www.example.com`
  - AWS Region: `us-west-2`
  - Object Ownership: `ACLs disabled (recommended)` by default
  - In the "Block Public Access settings for bucket" section, 
    - Clear the check box for "Block all public access"
    - NOTE: you must allow public read access to the bucket and files so
      that CloudFront URLs can serve content from the bucket.
    - NOTE: for production deployment, we can restrict access to s3
      from CloudFront only using CloudFront origin access control (OAC).
      See [How do I use my CloudFront distribution to restrict access to an Amazon S3 bucket?](https://repost.aws/knowledge-center/cloudfront-access-to-amazon-s3)
  - Bucket Versioning: `disabled` by default
    - NOTE: consider using versioning to enable deployment rollback;
    - See discussion - https://stackoverflow.com/questions/53140211/static-web-app-versioning-with-s3-and-cloudfront
  - Accept the default settings, click `Create`
- After the bucket is created, upload the content of the `build` folder
  to the s3 bucket, `aws-website-react`
  - Using the s3 console, the upload needs to be done in two steps; due
    the limitation of the s3 console
  - first, upload the files in the `build` folder
  - then, upload the folders in the `builder` folder
  - *OPTIONAL* - use the `aws-cli` to upload the `build` folder. Make 
    sure the `aws cli` has been configured. 
```
$ cd aws-website-react
$ aws s3 cp ./build s3://aws-website-react/ --recursive
```
- Next, enabling the website hosting
  - Sources:
    - https://docs.aws.amazon.com/AmazonS3/latest/userguide/EnableWebsiteHosting.html
    - [Deploy a React-based single-page application to Amazon S3 and CloudFront](https://docs.aws.amazon.com/prescriptive-guidance/latest/patterns/deploy-a-react-based-single-page-application-to-amazon-s3-and-cloudfront.html)
  - go to the Properties tab, scroll all the way
  - Under Static website hosting, choose Edit
  - Static website hosting - choose `Enable`
  - Hosting type: choose `Host a static website`
  - In Index document, enter the file name of the index document, 
    in this case `index.html`. The index document name is case sensitive
    and must exactly match the file name of the HTML index document that
    was uploaded to the S3 bucket. Amazon S3 returns this index document
    when requests are made to the root domain or any of the subfolders.
    For more information, see [Configuring an index document](https://docs.aws.amazon.com/AmazonS3/latest/userguide/IndexDocumentSupport.html)
  - To provide your own custom error document for 4XX class errors, in 
    Error document, enter the custom error document file name.
    If you don't specify a custom error document and an error occurs, 
    Amazon S3 returns a default HTML error document. For more information,
    see [Configuring a custom error document](https://docs.aws.amazon.com/AmazonS3/latest/userguide/CustomErrorDocSupport.html)
  - (Optional) specify advanced redirection rules, in Redirection rules,
    enter JSON to describe the rules. For example, you can conditionally
    route requests according to specific object key names or prefixes in
    the request. For more information, see 
    [Configure redirection rules to use advanced conditional redirects](https://docs.aws.amazon.com/AmazonS3/latest/userguide/how-to-page-redirect.html#advanced-conditional-redirects)
  - Choose Save changes.
- Add a bucket policy that makes your bucket content publicly available
  - Go to the Permissions tab, under Bucket Policy, choose Edit.
  - To grant public read access for your website, copy the following 
    bucket policy, and paste it in the Bucket policy editor:
  ```
  {
      "Version": "2012-10-17",
      "Statement": [
          {
              "Sid": "PublicReadGetObject",
              "Effect": "Allow",
              "Principal": "*",
              "Action": [
                  "s3:GetObject"
              ],
              "Resource": [
                  "arn:aws:s3:::aws-website-react/*"
              ]
          }
      ]
  }  
  ```
- Amazon S3 enables static website hosting for your bucket. At the bottom
  of the page, under Static website hosting, you see the website endpoint
  for your bucket.
 
### SETUP CloudFront - Step by Step instructions:

![Architecture](./images/cf-secure-static-site-architecture.png)

The above diagram shows an overview of how the solution works:
- The viewer requests the website from CloudFront Distribution domain name,
  for example, `https://d2nd9ww29jffec.cloudfront.net`
- If the requested object is cached, CloudFront returns the object from 
  its cache to the viewer.
- If the object is not in CloudFront’s cache, CloudFront requests the 
  object from the origin (an S3 bucket).
- S3 returns the object to CloudFront
- CloudFront caches the object.
- The object is returned to the viewer. Subsequent responses for the object are served from the CloudFront cache.

**Source:**
- [Getting started with a simple CloudFront distribution - Step 2: Create a CloudFront distribution](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/GettingStarted.SimpleDistribution.html)

**Other useful references:**
- [How do I use CloudFront to serve a static website hosted on Amazon S3?](https://repost.aws/knowledge-center/cloudfront-serve-static-website)
- [Github: Amazon CloudFront Secure Static Website](https://github.com/aws-samples/amazon-cloudfront-secure-static-site#user-content-amazon-cloudfront-secure-static-website)

- PERFORM the following from the AWS CONSOLE
- Go to Cloudfront; and choose Create distribution.
- Under Origin, for "Origin domain", if you click the textbox the list
  of the Amazon S3 buckets will be shown. **However**, for S3 bucket 
  that is used for static web hosting we need to use the S3 website 
  endpoint rather than the bucket endpoint. 
  - The s3 endpoint can be found from s3 bucket page - Properties tab -
    Static website hosting section - Bucket website endpoint
  - **DO NOT** include the `http://`
- For the other settings under Origin:
  - Origin path - optional is <empty>; by default
    - Note: If you want CloudFront to always request content from a 
      particular directory in the origin, enter the directory path, 
      beginning with a forward slash (/). Do not add a slash (/) at the
      end of the path. CloudFront appends the directory path to the origin
      domain name.
  - Name is auto-populated with the same text as "Origin domain"
  - Add custom headers; Note: CloudFront includes this header in all 
    requests that it sends to your origin.
    - The CloudFront Response Header Policy adds security headers to every
      response served by CloudFront
    - The security headers can help mitigate some attacks, as explained 
      in the [Amazon CloudFront - Understanding response header policies documentation](https://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/understanding-response-headers-policies.html#understanding-response-headers-policies-security).
    - Security headers are a group of headers in the web server response
      that tell web browsers to take extra security precautions.
    - For more information, see [Mozilla’s web security guidelines](https://infosec.mozilla.org/guidelines/web_security). 
    - Adds the following headers to each response:
    ```
    Strict-Transport-Security: max-age=31536000
    Content-Security-Policy: default-src 'none'; img-src 'self'; script-src 'self'; style-src 'self'; object-src 'none'
    X-Content-Type-Options: nosniff
    X-Frame-Options: DENY
    X-XSS-Protection: 1; mode=block
    Referrer-Policy: same-origin
    ```
      - See [Amazon CloudFront Secure Static Website](https://github.com/aws-samples/amazon-cloudfront-secure-static-site)
      - For the values, see https://repost.aws/knowledge-center/cloudfront-http-security-headers
      - **NEED TO BE TESTED!!**
    - Enable Origin Shield: `No` by default
      - Origin Shield is an additional caching layer that can help reduce
        the load on your origin and help protect its availability.
      - **Incur additional charges**
    - Connection attempts: `3` by default
    - Connection timeout: `10` by default
    - Response timeout: `30`; only applicable to custom origins
    - Keep-alive timeout: `5`; only applicable to custom origins
- For Default cache behavior, use the default values, EXCEPT:
  - Viewer - Viewer protocol policy: `Redirect HTTP to HTTPS`
- For the Function associations, no associations
- For Settings, use the default values
  - Price class: `Use only North  America and Europe`
  - Alternate domain name (CNAME) - optional
    - None for this demo. Defer to other demo;
  - Custom SSL certificate - optional; the SSL certificate will be created
    and used automatically;
    - Use the certificate created earlier
    - Important: If you entered Alternate domain names (CNAMEs) for the
      distribution, then the CNAMEs must match the SSL certificate that 
      you select. To troubleshoot issues with your SSL certificate, see 
      [How can I troubleshoot issues with using a custom SSL certificate for my CloudFront distribution?](https://repost.aws/knowledge-center/custom-ssl-certificate-cloudfront)
  - Default root object:
    - so that when typing the url on browser, no need to include the
      `index.html` all the time
- Click "Create distribution"; After CloudFront creates your distribution,
  the value of the Status column for your distribution changes from 
  In Progress to Deployed. This typically takes a few minutes.
- Open browser and points to the following. Yes, it's `https`.
```
https://d1mouw3fzm7gpx.cloudfront.net/index.html
```


## CLOUDFORMATION - deploy react app to aws s3 for website hosting  

TBD

## References

Other useful resources:

- [Tutorial: Configuring a static website using a custom domain registered with Route 53](https://docs.aws.amazon.com/AmazonS3/latest/userguide/website-hosting-custom-domain-walkthrough.html)
