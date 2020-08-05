package ca.dataedu.kafka

case class EnrichedTrip(trip: Trip, calendar: Option[Calendar], route: Option[Route])

object EnrichedTrip {
  def toCsv(enrichedTrip: EnrichedTrip): String =
    s"${Trip.toCsv(enrichedTrip.trip)},,,,,,,,,,,,,,,,,"
}
