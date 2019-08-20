### Build hive
### See https://github.com/apache/spark/pull/20923 and https://github.com/JoshRosen/hive/pull/2 for the hive rebuild reasons
apt install maven -y && \
rm -rf ~/hive && \
cd ~ && \
git clone https://github.com/sidoruka/hive.git && \
cd hive && \
git checkout release-1.2.1-spark2 && \
mvn clean install -Phadoop-2 -DskipTests -Psources && \
rm -rf dist && \
mkdir dist && \
\cp ~/.m2/repository/org/spark-project/hive/hive-beeline/1.2.1.spark2/hive-beeline-1.2.1.spark2.jar \
    ~/.m2/repository/org/spark-project/hive/hive-cli/1.2.1.spark2/hive-cli-1.2.1.spark2.jar \
    ~/.m2/repository/org/spark-project/hive/hive-exec/1.2.1.spark2/hive-exec-1.2.1.spark2.jar \
    ~/.m2/repository/org/spark-project/hive/hive-jdbc/1.2.1.spark2/hive-jdbc-1.2.1.spark2.jar \
    ~/.m2/repository/org/spark-project/hive/hive-metastore/1.2.1.spark2/hive-metastore-1.2.1.spark2.jar \
    dist/

### Build Spark with Hadoop 3.1.0 and "Cloud Integration" (https://spark.apache.org/docs/2.4.3/cloud-integration.html)
rm -rf ~/spark && \
cd ~ && \
git clone https://github.com/apache/spark.git && \
cd spark && \
git checkout tags/v2.4.3 && \
rm -rf dist && \
./dev/make-distribution.sh --name cloud-pipeline-spark --tgz -Phadoop-cloud -Dhadoop.version=3.1.0 -Phive -DskipTests

### Replace hive
\cp ~/hive/dist/* ~/spark/dist/jars/

### Pack Spark distro
mv ~/spark/dist ~/spark/spark-2.4.3-bin-hadoop3.1
cd ~/spark && \
tar -cvf spark-2.4.3.tgz spark-2.4.3-bin-hadoop3.1

### Upload to the distro-S3
# aws s3 cp spark-2.4.3.tgz s3://https://cloud-pipeline-oss-builds.s3.amazonaws.com/tools/spark/spark-2.4.3.tgz
