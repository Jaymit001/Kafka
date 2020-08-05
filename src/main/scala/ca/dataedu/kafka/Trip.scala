package ca.dataedu.kafka

case class Trip(
                 routeId: Int,
                 serviceId: String,
                 tripId: String,
                 tripHeadsign: String,
                 directionId:Int ,
                 shapeId:Int,
                 wheelchairAccessible: Int
               )
object Trip {
  def toCsv(trip: Trip): String = {
    trip.routeId + "," +
      trip.serviceId + "," +
      trip.tripId + "," +
      trip.tripHeadsign + "," +
      trip.directionId + "," +
      trip.shapeId + "," +
      trip.wheelchairAccessible
  }
}
