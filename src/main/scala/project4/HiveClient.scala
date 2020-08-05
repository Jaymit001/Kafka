package project4

import java.io.FileOutputStream
import org.apache.hadoop.fs.{FSDataOutputStream, Path}

object HiveClient extends App with Enricher{
  val projectDir = new Path("/user/winter2020/Jay/project4/trips")
  if (fs.exists(projectDir)) fs.delete(projectDir, true)
  fs.mkdirs(projectDir)
  fs.copyFromLocalFile(new Path("file:///home/Jay/Desktop/trips.txt"),
    new Path("/user/winter2020/Jay/project4/trips/"))

  val projectDir1 = new Path("/user/winter2020/Jay/project4/calendar_dates")
  if (fs.exists(projectDir1)) fs.delete(projectDir1, true)
  fs.mkdirs(projectDir1)
  fs.copyFromLocalFile(new Path("file:///home/Jay/Desktop/calendar_dates.txt"),
    new Path("/user/winter2020/Jay/project4/calendar_dates/"))

  val projectDir2 = new Path("/user/winter2020/Jay/project4/frequencies")
  if (fs.exists(projectDir2)) fs.delete(projectDir2, true)
  fs.mkdirs(projectDir2)
  fs.copyFromLocalFile(new Path("file:///home/Jay/Desktop/frequencies.txt"),
    new Path("/user/winter2020/Jay/project4/frequencies/"))

  stmt.executeUpdate("""CREATE DATABASE IF NOT EXISTS winter2020_Jay""")
  stmt.execute("USE winter2020_Jay")
  stmt.execute("DROP TABLE IF EXISTS winter2020_Jay.trips")
  stmt.executeUpdate(
    """CREATE EXTERNAL TABLE IF NOT EXISTS winter2020_Jay.trips(
      |routeId INT,
      |serviceId STRING,
      |tripId STRING,
      |tripHeadsign STRING,
      |directionId INT,
      |shapeId INT,
      |wheelchairAccessible INT )
      |ROW FORMAT DELIMITED
      |FIELDS TERMINATED BY ','
      |STORED AS TEXTFILE
      |LOCATION '/user/winter2020/Jay/project4/trips/'
      |tblproperties ("skip.header.line.count"="1")""".stripMargin)

  stmt.execute("DROP TABLE IF EXISTS winter2020_Jay.calendar_dates")
  stmt.executeUpdate(
    """CREATE EXTERNAL TABLE IF NOT EXISTS winter2020_Jay.calendar_dates(
      | serviceId STRING,
      | dateC STRING,
      | exceptionType INT
      | )
      | ROW FORMAT DELIMITED
      | FIELDS TERMINATED BY ','
      | STORED AS TEXTFILE
      | LOCATION '/user/winter2020/Jay/project4/calendar_dates/'
      | tblproperties ("skip.header.line.count"="1")""".stripMargin)

  stmt.execute("DROP TABLE IF EXISTS winter2020_Jay.frequencies")
  stmt.executeUpdate(
    """CREATE EXTERNAL TABLE IF NOT EXISTS winter2020_Jay.frequencies(
      | tripId STRING,
      | startTime STRING,
      | endIime STRING,
      | headwaySec INT
      | )
      | ROW FORMAT DELIMITED
      | FIELDS TERMINATED BY ','
      | STORED AS TEXTFILE
      | LOCATION '/user/winter2020/Jay/project4/frequencies/'
      | tblproperties ("skip.header.line.count"="1")""".stripMargin)

  stmt.execute("DROP TABLE IF EXISTS winter2020_Jay.enrichedTrip")
  stmt.execute("set hive.exec.dynamic.partition.mode=nonstrict")
  val enrichedTrip = stmt.execute(
    """CREATE TABLE IF NOT EXISTS winter2020_Jay.enrichedTrip(
      |routeID STRING,
      |serviceId STRING,
      |tripId STRING,
      |tripHeadsign STRING,
      |directionId INT,
      |dateC STRING,
      |startTime STRING
      |)
      |PARTITIONED BY (wheelchairAccessible INT)
      |ROW FORMAT DELIMITED
      | FIELDS TERMINATED BY ','
      |STORED AS PARQUET
      |""".stripMargin)

  stmt.executeUpdate(
    """INSERT OVERWRITE TABLE winter2020_Jay.enrichedTrip partition(wheelchairAccessible)
      |SELECT a.routeID ,a.serviceId ,a.tripId,a.tripHeadsign,a.directionId,dateC ,startTime,
      |a.wheelchairAccessible
      |FROM winter2020_Jay.trips a
      |LEFT JOIN winter2020_Jay.calendar_dates b ON a.serviceId=b.serviceId
      |LEFT JOIN winter2020_Jay.frequencies c ON a.tripId = c.tripId
      |""".stripMargin)
  stmt.execute("MSCK REPAIR TABLE winter2020_Jay.enrichedTrip")

  val rs = stmt.executeQuery("SHOW PARTITIONS winter2020_Jay.enrichedTrip")

  while (rs.next()){
    println(rs.getString(1))
  }

  val rs1 = stmt.executeQuery(
    """SELECT datec ,serviceId
      | FROM winter2020_Jay.enrichedtrip
      |  WHERE wheelchairaccessible =1 LIMIT 10""".stripMargin)
  println(s"wheelchairaccessible =1")
  while (rs1.next())
  {
    println(s"title:${rs1.getInt(1)} ,title1:${rs1.getString(2)}")
  }

  val rs2 = stmt.executeQuery(
    """SELECT starttime ,serviceId
      | FROM winter2020_Jay.enrichedtrip
      |  WHERE wheelchairaccessible =2 LIMIT 10""".stripMargin)
  println(s"wheelchair acccessible=2")
  while (rs2.next())
  {
    println(s"title:${rs2.getInt(1)} ,title2:${rs2.getString(2)}")
  }

  stmt.close()
  connection.close()
}

