#!/bin/bash

set -e

### Setup prerequisites
apt install git curl -y
pip install awscli


### Setup JAVA 8
echo "============================"
echo "Setting up OpenJDK 1.8.0_222"
echo "============================"
export JAVA_HOME=~/jdk
export PATH=$JAVA_HOME/bin:~/maven/bin:$PATH
rm -rf $JAVA_HOME && \
cd ~ && \
curl -s "https://cloud-pipeline-oss-builds.s3.amazonaws.com/tools/java/openjdk-1.8.0_222_linux-x64_bin.tar.gz" -o openjdk.tar.gz && \
tar -zxf openjdk.tar.gz --no-same-owner && \
rm -f openjdk.tar.gz && \
mv openjdk* "$JAVA_HOME" && \
java -version && \
rm -rf ~/maven && \
cd ~ && \
curl -s "https://cloud-pipeline-oss-builds.s3.amazonaws.com/tools/maven/apache-maven-3.6.1-bin.tar.gz" -o maven.tar.gz && \
tar -zxf maven.tar.gz --no-same-owner && \
rm -f maven.tar.gz && \
mv apache-maven* ~/maven && \
mvn -version
echo


echo "============="
echo "Cloud Pipeline Credentials Provider"
echo "============="
### Note: use `-Dhttps.proxyHost= -Dhttps.proxyPort=` if building with a proxy
cd credentials-provider && \
./gradlew jar && \
mkdir -p ~/credentials-provider/dist && \
\cp build/libs/credentials-provider-*.jar ~/credentials-provider/dist/
echo


echo "=============="
echo "Building Spark"
echo "=============="
### If this is not set, Maven will crash with StackOverflow exception
export MAVEN_OPTS="-Xss64m -Xmx2g -XX:ReservedCodeCacheSize=1g"
### Build Spark with Hadoop 3.1.0 and "Cloud Integration" (https://spark.apache.org/docs/3.2.1/cloud-integration.html)
rm -rf ~/spark && \
cd ~ && \
git clone https://github.com/apache/spark.git && \
cd spark && \
git checkout tags/v3.2.1 && \
rm -rf dist && \
./dev/make-distribution.sh  --name cloud-pipeline-spark \
                            --tgz \
                            --r \
                            -Phadoop-cloud \
                            -Dhadoop.version=3.2.2 \
                            -Phive \
                            -Phive-thriftserver \
                            -DskipTests \
                            --batch-mode
echo


echo "===================="
echo "Packing Spark distro"
echo "===================="
### Replace hive
\cp ~/hive/dist/* ~/spark/dist/jars/
### Add credentials provider
\cp ~/credentials-provider/dist/* ~/spark/dist/jars/
# Pack tar.gz
mv ~/spark/dist ~/spark/spark-3.2.1-bin-hadoop3.2 && \
cd ~/spark && \
tar -zcf spark-3.2.1.tgz spark-3.2.1-bin-hadoop3.2
echo

echo "======================="
echo "Publishing Spark distro"
echo "======================="
### Upload to the distro-S3
aws s3 cp spark-3.2.1.tgz s3://cloud-pipeline-oss-builds/tools/spark/spark-3.2.1.tgz
echo
