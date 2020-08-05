package hadoop

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FileSystem, Path}
import scala.io.{BufferedSource, Source}

object hdfs extends App {
  val conf = new Configuration()
  conf.addResource(new Path("/home/Jay/opt/hadoop-2.7.7/etc/cloudera/core-site.xml"))
  conf.addResource(new Path("/home/Jay/opt/hadoop-2.7.7/etc/cloudera/hdfs-site.xml"))
  val fs: FileSystem = FileSystem.get(conf)

  // hadoop fs -ls /
  fs
    .listStatus(new Path("/"))
    .map(_.getPath)
    .foreach(println)

}
