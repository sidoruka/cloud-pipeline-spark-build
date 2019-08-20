#!/bin/bash

set -e

### Setup JAVA 8
echo "============================"
echo "Setting up OpenJDK 1.8.0_222"
echo "============================"
export JAVA_HOME=~/jdk
export PATH="$JAVA_HOME/bin:$PATH"
rm -rf $JAVA_HOME && \
cd ~ && \
curl -s "https://cloud-pipeline-oss-builds.s3.amazonaws.com/tools/java/openjdk-1.8.0_222_linux-x64_bin.tar.gz" -o openjdk.tar.gz && \
tar -zxf openjdk.tar.gz --no-same-owner && \
rm -f openjdk.tar.gz && \
mv openjdk* "$JAVA_HOME" && \
java -version
echo

# echo "============="
# echo "Building hive"
# echo "============="
# ### See https://github.com/apache/spark/pull/20923 and https://github.com/JoshRosen/hive/pull/2 for the hive rebuild reasons
# rm -rf ~/hive && \
# cd ~ && \
# git clone https://github.com/sidoruka/hive.git && \
# cd hive && \
# git checkout release-1.2.1-spark2 && \
# mvn clean install -Phadoop-2 -DskipTests -Psources -q --batch-mode && \
# rm -rf dist && \
# mkdir dist && \
# \cp ~/.m2/repository/org/spark-project/hive/hive-beeline/1.2.1.spark2/hive-beeline-1.2.1.spark2.jar \
#     ~/.m2/repository/org/spark-project/hive/hive-cli/1.2.1.spark2/hive-cli-1.2.1.spark2.jar \
#     ~/.m2/repository/org/spark-project/hive/hive-exec/1.2.1.spark2/hive-exec-1.2.1.spark2.jar \
#     ~/.m2/repository/org/spark-project/hive/hive-jdbc/1.2.1.spark2/hive-jdbc-1.2.1.spark2.jar \
#     ~/.m2/repository/org/spark-project/hive/hive-metastore/1.2.1.spark2/hive-metastore-1.2.1.spark2.jar \
#     dist/
# echo

echo "=============="
echo "Building Spark"
echo "=============="
### Build Spark with Hadoop 3.1.0 and "Cloud Integration" (https://spark.apache.org/docs/2.4.3/cloud-integration.html)
rm -rf ~/spark && \
cd ~ && \
git clone https://github.com/apache/spark.git && \
cd spark && \
git checkout tags/v2.4.3 && \
rm -rf dist && \
./dev/make-distribution.sh --name cloud-pipeline-spark --tgz -Phadoop-cloud -Dhadoop.version=3.1.0 -Phive -DskipTests
echo


echo "===================="
echo "Packing Spark distro"
echo "===================="
### Replace hive
\cp ~/hive/dist/* ~/spark/dist/jars/
# Pack tar.gz
mv ~/spark/dist ~/spark/spark-2.4.3-bin-hadoop3.1 && \
cd ~/spark && \
tar -zcf spark-2.4.3.tgz spark-2.4.3-bin-hadoop3.1
echo

echo "======================="
echo "Publishing Spark distro"
echo "======================="
### Upload to the distro-S3
aws s3 cp spark-2.4.3.tgz s3://cloud-pipeline-oss-builds/tools/spark/spark-2.4.3.tgz
echo

