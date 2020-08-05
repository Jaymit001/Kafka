package hadoop

import org.apache.hadoop.fs.{FSDataOutputStream, Path}

object HadoopPlayground extends HdfsClient {
  // hadoop fs -ls /
    fs
      .listStatus(new Path("/"))
      .map(_.getPath)
      .foreach(println)

    /** hadoop fs -mkdir /user/winter2020/Jay/NewFolder2 */
//    fs.create(new Path("/user/winter2020/Jay/NewFolder2"))
//    fs.mkdirs(new Path("/user/winter2020/Jay/NewFolder2"))

    /** hadoop fs -rm -R /user/winter2020/Jay/NewFolder2 */
//    fs.delete(new Path("/user/winter2020/Jay/NewFolder2"))

  /** The path on the HDFS to write data */
  val filePath = new Path("/user/winter2020/Jay/NewFolder2/students")

  /** The list of student to write on HDFS */
  val names = List ("Jaymit","Kishore","Parva")

  /** Open a stream from client to the file on HDFS */
  val y: FSDataOutputStream = fs.create(filePath)

  /** Write names on HDFS */
  names.foreach(name => y.writeChars(name))

  /** clean-up */
  y.flush()
  y.close()


}
