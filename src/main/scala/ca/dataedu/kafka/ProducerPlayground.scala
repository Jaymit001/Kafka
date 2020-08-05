package ca.dataedu.kafka

import java.time.Duration
import java.util.Properties
import scala.collection.JavaConverters._

import org.apache.kafka.clients.consumer.ConsumerConfig.{AUTO_OFFSET_RESET_CONFIG, BOOTSTRAP_SERVERS_CONFIG, ENABLE_AUTO_COMMIT_CONFIG, GROUP_ID_CONFIG, KEY_DESERIALIZER_CLASS_CONFIG, VALUE_DESERIALIZER_CLASS_CONFIG}
import org.apache.kafka.clients.consumer.{ConsumerRecords, KafkaConsumer}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.{IntegerDeserializer, IntegerSerializer, StringDeserializer, StringSerializer}


object ProducerPlayground extends App {
  val topicName = "trip"
  val consumerProperties = new Properties()
  consumerProperties.setProperty(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  consumerProperties.setProperty(GROUP_ID_CONFIG, "group-id-09")
  consumerProperties.setProperty(AUTO_OFFSET_RESET_CONFIG, "earliest")
  consumerProperties.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, classOf[IntegerDeserializer].getName)
  consumerProperties.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, classOf[StringDeserializer].getName)
  consumerProperties.setProperty(ENABLE_AUTO_COMMIT_CONFIG, "false")
  val consumer = new KafkaConsumer[Int, String](consumerProperties)
  consumer.subscribe(List(topicName).asJava)
  val producerProperties = new Properties()
  producerProperties.setProperty(
    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"
  )
  producerProperties.setProperty(
    ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
  )
  producerProperties.setProperty(
    ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer].getName
  )
  val producer = new KafkaProducer[String, String](producerProperties)
  val outputTopic = "enriched_trip"

  while (true) {
    val polledRecords: ConsumerRecords[Int, String] = consumer.poll(Duration.ofSeconds(1))
    if (!polledRecords.isEmpty) {
      val recordIterator = polledRecords.iterator()
      while (recordIterator.hasNext) {
        val record = recordIterator.next()
        // Parse the input
        val p = record.value().split(",", -1)
        val trip = Trip(p(0).toInt, p(1), p(2), p(3), p(4).toInt, p(5).toInt, p(6).toInt)
        // Enrich
        val enrichedTrip = EnrichedTrip(trip, None, None)
        // Produce enriched trip
        val output = EnrichedTrip.toCsv(enrichedTrip)
        println(output)
        consumer.commitSync(Duration.ofSeconds(1))
        producer.send(new ProducerRecord[String, String](outputTopic, trip.tripId, output))

      }
      producer.flush()
    }
  }
}