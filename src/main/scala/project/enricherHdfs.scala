package project.stm
import org.apache.hadoop.fs.{FSDataInputStream, FSDataOutputStream, Path}
import project.{Calendar, EnrichedTrip, Enricher, Route, Trip, TripRoute}
import scala.io.Source


object enricherHdfs extends Enricher with App {
  val routes: FSDataInputStream = fs.open(
    new Path("/user/winter2020/Jay/stm/routes.txt"))
  val routeMap: Map[Int, Route] = Source.fromInputStream(routes).getLines()
    .toList.tail
    .map(_.split(",", -1))
    .map(rout => rout(0).toInt -> Route(rout(0).toInt, rout(1), rout(2), rout(3),
      rout(4), rout(5), rout(6))).toMap
  routes.close()

  def lookup(routeId: Int): Route = routeMap.getOrElse(routeId, null)

  val calendars: FSDataInputStream = fs.open(
    new Path("/user/winter2020/Jay/stm/calendar.txt"))
  val calendarMap: Map[String, Calendar] = Source.fromInputStream(calendars).getLines().toList.tail
    .map(_.split(",", -1))
    .map(cal => cal(0) -> Calendar(cal(0), cal(1).toInt, cal(2).toInt, cal(3).toInt, cal(4).toInt,
      cal(5).toInt, cal(6).toInt, cal(7).toInt, cal(8), cal(9))).toMap
  calendars.close()

  def lookup(serviceId: String): Calendar = calendarMap.getOrElse(serviceId, null)

  val trips: FSDataInputStream = fs.open(
    new Path("/user/winter2020/Jay/stm/trips.txt"))
  val enhancedMap = Source.fromInputStream(trips).getLines().toList.tail
    .map(_.split(",", -1))
    .map(tri => Trip(tri(0).toInt, tri(1), tri(2), tri(3), tri(4).toInt, tri(5).toInt, tri(6).toInt))
    .map(trip => TripRoute(trip, lookup(trip.routeId)))
    .map(tripRoute => EnrichedTrip(tripRoute, lookup(tripRoute.trip.serviceId)))
  val enhancedOutput = enhancedMap.map(output => {

    val t = Trip.toCsv(output.tripRoute.trip)
    val r = Route.toCsv(output.tripRoute.route)
    val c = Calendar.toCsv(output.calendar)

    t + "," + r + "," + c
  })

  val header: String = "route_id,service_id,trip_id,tripHead,directionId,shape_id,wheelchair_accessible," +
    "route_id,agency_id,route_short_name,route_long_name,route_type,route_url,route_color," +
    "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date"

  val filePath = new Path("/user/winter2020/Jay/course3")

  if (fs.exists(filePath)) fs.delete(filePath, false)
  fs.mkdirs(filePath)
  val fileOutput: FSDataOutputStream = fs.create(new Path("/user/winter2020/Jay/course3/enhanced.csv"))

  fileOutput.writeChars(header)
  for (i <- enhancedOutput) {

    //write the output to file output
    fileOutput.writeChars("\n")
    fileOutput.writeChars(i)

  }


  trips.close()
  fileOutput.close()
  fs.close()

}

