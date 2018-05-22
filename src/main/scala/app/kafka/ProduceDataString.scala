package app.kafka

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

class ProduceDataString[DataObject](propertiesConfigProducer: Properties, propertiesConfigTopics: Properties) {


  val LOG: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(classOf[ProduceData[DataObject]])

  val topic: String = propertiesConfigTopics.getProperty("topic.name")
  val producer: KafkaProducer[String, DataObject] = new KafkaProducer[String, DataObject](propertiesConfigProducer)
  var count: Int = 1


  def produceData(record: DataObject): Unit = {


    try {

      val producerRecord: ProducerRecord[String, DataObject] = new ProducerRecord[String, DataObject](topic, record)
      producer.send(producerRecord)
      println("Messages Sent: ", count)
      count += 1


    } catch {
      case e: Exception => LOG.error("Error in ProduceData: ", e.printStackTrace())

    } finally {
      producer.flush()
    }
  }


}
