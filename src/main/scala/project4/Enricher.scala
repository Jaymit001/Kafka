package project4

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{  FileSystem, Path}
import java.sql.DriverManager

trait Enricher extends App {

  val conf = new Configuration()
  conf.addResource(new Path("/home/Jay/opt/hadoop-2.7.7/etc/cloudera/core-site.xml"))
  conf.addResource(new Path("/home/Jay/opt/hadoop-2.7.7/etc/cloudera/hdfs-site.xml"))
  val fs:FileSystem = FileSystem.get(conf)
  val driverName: String = "org.apache.hive.jdbc.HiveDriver"
  Class.forName(driverName)
  val connectionString: String = "jdbc:hive2://quickstart.cloudera:10000/winter2020_Jay;user=Jay;"
  val connection = DriverManager.getConnection(connectionString)
  val stmt = connection.createStatement()

}
