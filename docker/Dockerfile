FROM --platform=linux/amd64 public.ecr.aws/lambda/nodejs:16 AS nodejs

COPY aws-website-react /web-app-test
WORKDIR /web-app-test

RUN npm install

COPY . .

CMD [ "app.handler"]

# FROM --platform=linux/amd64 public.ecr.aws/nginx/nginx:stable

# COPY --from=nodejs --chown=nginx /web-app-test/build /usr/share/nginx/html

# HTTP port
EXPOSE 3000

