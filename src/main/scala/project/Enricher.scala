package project

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FSDataOutputStream, FSDataInputStream, FileSystem, Path, FileUtil}

trait Enricher {
  val conf = new Configuration()
  conf.addResource(new Path("/home/Jay/opt/hadoop-2.7.7/etc/cloudera/core-site.xml"))
  conf.addResource(new Path("/home/Jay/opt/hadoop-2.7.7/etc/cloudera/hdfs-site.xml"))
  val fs: FileSystem = FileSystem.get(conf)

  fs.copyFromLocalFile(new Path("/home/Jay/Downloads/STM/trips.txt"),
    new Path("/user/winter2020/Jay/stm/"))

  fs.copyFromLocalFile(new Path("/home/Jay/Downloads/STM/routes.txt"),
    new Path("/user/winter2020/Jay/stm/"))

  fs.copyFromLocalFile(new Path("/home/Jay/Downloads/STM/calendar.txt"),
    new Path("/user/winter2020/Jay/stm/"))

}
