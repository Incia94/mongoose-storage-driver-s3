[![Gitter chat](https://badges.gitter.im/emc-mongoose.png)](https://gitter.im/emc-mongoose)
[![Issue Tracker](https://img.shields.io/badge/Issue-Tracker-red.svg)](https://mongoose-issues.atlassian.net/projects/GOOSE)
[![CI status](https://gitlab.com/emc-mongoose/mongoose-storage-driver-s3/badges/master/pipeline.svg)](https://gitlab.com/emc-mongoose/mongoose-storage-driver-s3/commits/master)
[![Tag](https://img.shields.io/github/tag/emc-mongoose/mongoose-storage-driver-s3.svg)](https://github.com/emc-mongoose/mongoose-storage-driver-s3/tags)
[![Maven metadata URL](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/github/emc-mongoose/mongoose-storage-driver-s3/maven-metadata.xml.svg)](http://central.maven.org/maven2/com/github/emc-mongoose/mongoose-storage-driver-s3)
[![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/http/oss.sonatype.org/com.github.emc-mongoose/mongoose-storage-driver-s3.svg)](http://oss.sonatype.org/com.github.emc-mongoose/mongoose-storage-driver-s3)
[![Docker Pulls](https://img.shields.io/docker/pulls/emcmongoose/mongoose-storage-driver-s3.svg)](https://hub.docker.com/r/emcmongoose/mongoose-storage-driver-s3/)

# S3 Storage Driver

## 1. Features

* API version: 2006-03-01
* Authentification:
    * [v2](https://docs.aws.amazon.com/general/latest/gr/signature-version-2.html) (by default)
    * [v4](https://docs.aws.amazon.com/general/latest/gr/signature-version-4.html)
* SSL/TLS
* Item types:
    * `data` (--> "object")
    * `path` (--> "bucket")
* Automatic destination path creation on demand
* Path listing input (with XML response payload)
* Data item operation types:
    * `create`
        * [copy](../../../../../../doc/design/copy_mode/README.md)
        * [Multipart Upload](../../../../../../src/main/java/com/emc/mongoose/base/item/op/composite/README.md)
    * `read`
        * full
        * random byte ranges
        * fixed byte ranges
        * content verification
        * [object tagging](https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectTagging.html)
    * `update`
        * full (overwrite)
        * random byte ranges
        * fixed byte ranges (with append mode)
        * [object tagging](https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectTagging.html)
    * `delete`
        * full
        * [object tagging](https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjectTagging.html)
    * `noop`
* Path item operation types:
    * `create`
    * `read`
    * `delete`
    * `noop`

## 2. Deployment

## 2.1. Basic

Java 11+ is required to build/run.

1. Get the latest `mongoose-base` jar from the 
[maven repo](http://repo.maven.apache.org/maven2/com/github/emc-mongoose/mongoose-base/)
and put it to your working directory. Note the particular version, which is referred as *BASE_VERSION* below.

2. Get the latest `mongoose-storage-driver-coop` jar from the
[maven repo](http://repo.maven.apache.org/maven2/com/github/emc-mongoose/mongoose-storage-driver-coop/)
and put it to the `~/.mongoose/<BASE_VERSION>/ext` directory.

3. Get the latest `mongoose-storage-driver-netty` jar from the
[maven repo](http://repo.maven.apache.org/maven2/com/github/emc-mongoose/mongoose-storage-driver-netty/)
and put it to the `~/.mongoose/<BASE_VERSION>/ext` directory.

4. Get the latest `mongoose-storage-driver-http` jar from the
[maven repo](http://repo.maven.apache.org/maven2/com/github/emc-mongoose/mongoose-storage-driver-http/)
and put it to the `~/.mongoose/<BASE_VERSION>/ext` directory.

5. Get the latest `mongoose-storage-driver-s3` jar from the
[maven repo](http://repo.maven.apache.org/maven2/com/github/emc-mongoose/mongoose-storage-driver-s3/)
and put it to the `~/.mongoose/<BASE_VERSION>/ext` directory.

```bash
java -jar mongoose-base-<BASE_VERSION>.jar \
    --storage-driver-type=s3 \
    ...
```
## 2.2. Docker

### 2.2.1. Standalone

Example:
```bash
docker run \
    --network host \
    emcmongoose/mongoose-storage-driver-s3 \
    --storage-net-node-addrs=<NODE_IP_ADDRS> \
    ...
```

### 2.2.2. Distributed

#### 2.2.2.1. Additional Node

Example:
```bash
docker run \
    --network host \
    --expose 1099 \
    emcmongoose/mongoose-storage-driver-s3 \
    --run-node
```

#### 2.2.2.2. Entry Node

Example:
```bash
docker run \
    --network host \
    emcmongoose/mongoose-storage-driver-s3 \
    --load-step-node-addrs=<ADDR1,ADDR2,...> \
    --storage-net-node-addrs=<NODE_IP_ADDRS> \
    ...
```


## 3. Configuration Reference

### 3.1. Specific Options

| Name                                           | Type         | Default Value    | Description                                      |
|:-----------------------------------------------|:-------------|:-----------------|:-------------------------------------------------|
| storage-object-fsAccess                        | Flag | false | Specifies whether filesystem access is enabled or not
| storage-object-tagging-enabled                 | Flag | false | Work (PUT/GET/DELETE) with object tagging or not (default)
| storage-object-tagging-tags                    | Map  | {} | Map of name-value tags, effective only for the `UPDATE` operation when tagging is enabled
| storage-object-versioning                      | Flag | false | Specifies whether the versioning storage feature is used or not

### 3.2. Other Options

* A **bucket** may be specified with either `item-input-path` or `item-output-path` configuration option
* Multipart upload should be enabled using the `item-data-ranges-threshold` configuration option
* The default storage port is set to 9020 for the docker image

## 4. Usage

### 4.1. Object Tagging

https://docs.aws.amazon.com/AmazonS3/latest/dev/object-tagging.html

#### 4.1.1. Put Object Tags

https://docs.aws.amazon.com/AmazonS3/latest/API/API_PutObjectTagging.html

Put (create or replace) the tags on the existing objects. The `update` load operation should be used for this. 
The objects should be specified by an 
[item input](https://github.com/emc-mongoose/mongoose-base/tree/master/doc/usage/item/input#items-input) 
(the bucket listing or the items input CSV file). 

Scenario example:
```javascript
var updateTaggingConfig = {
    "storage" : {
        "object" : {
            "tagging" : {
                "enabled" : true,
                "tags" : {
                    "tag0" : "value_0",
                    "tag1" : "value_1",
                    // ...
                    "tagN" : "value_N"
                }
            }
        }
    }
};

UpdateLoad
    .config(updateTaggingConfig)
    .run();
```

Command line example:
```bash
docker run \
    --network host \
    emcmongoose/mongoose-storage-driver-s3 \
    --storage-auth-uid=user1 \ 
    --storage-auth-secret=**************************************** \
    --item-input-file=objects_to_update_tagging.csv \
    --item-output-path=/bucket1 \
    --storage-net-transport=nio \
    --run-scenario=tagging.js
```

***Note***:
> It's not possible to use the command line to specify the tag set, a user should use the scenario file for this

##### 4.1.1.1. Tags Expressions

Both tag names and values support the 
[expression language](https://github.com/emc-mongoose/mongoose-base/blob/master/src/main/java/com/emc/mongoose/base/config/el/README.md):

Example:
```javascript
var updateTaggingConfig = {
    "storage" : {
        "object" : {
            "tagging" : {
                "enabled" : true,
                "tags" : {
                    "foo${rnd.nextInt()}" : "bar${time:millisSinceEpoch()}",
                    "key1" : "${date:formatNowIso8601()}",
                    "${e}" : "${pi}"
                }
            }
        }
    }
};

UpdateLoad
    .config(updateTaggingConfig)
    .run();
```

#### 4.1.2. Get Object Tags

https://docs.aws.amazon.com/AmazonS3/latest/API/API_GetObjectTagging.html

Example:
```bash
docker run \
    --network host \
    emcmongoose/mongoose-storage-driver-s3 \
    --storage-net-node-addrs=<NODE_IP_ADDRS> \
    --read \
    --item-input-path=/bucket1 \
    --storage-object-tagging-enabled \
    ...
```

#### 4.1.3. Delete Object Tags

https://docs.aws.amazon.com/AmazonS3/latest/API/API_DeleteObjectTagging.html

```bash
docker run \
    --network host \
    emcmongoose/mongoose-storage-driver-s3 \
    --storage-net-node-addrs=<NODE_IP_ADDRS> \
    --delete \
    --item-input-file=objects_to_delete_tagging.csv \
    --storage-object-tagging-enabled \
    ...
```
