package app.parser

import java.util
import java.util.Properties

import app.kafka.ProduceData
import avro.AvroSerializer
import com.govcloud.digst
import com.govcloud.digst.Organisation
import javax.naming.Binding
import org.apache.log4j.Logger
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

class XMLHandler() extends DefaultHandler
{

  val LOG: org.slf4j.Logger = org.slf4j.LoggerFactory.getLogger(classOf[XMLHandler])

  val name = "name"
  val id = "id"
  val parentid = "parentid"
  val template = "template"

  val key = "key"


  var tfId_v:String = _
  var key_v:String = _
  var types_v:String = _
  var content_v:String = _

  var isContent:Boolean = false
  var avroOrg:Organisation = _
  var extras:util.Map[CharSequence,CharSequence] = new util.HashMap[CharSequence, CharSequence]()

  var producer:ProduceData[Array[Byte]] = _

  def setup(propertiesConfigProducer:Properties,propertiesConfigTopics:Properties): Unit =
  {
    producer = new ProduceData[Array[Byte]](propertiesConfigProducer, propertiesConfigTopics)
    AvroSerializer.instance().setUp(Organisation.SCHEMA$)

  }

  override def startElement(s: String, s1: String, qName: String, attributes: Attributes): Unit =
  {

    if (qName.equals("item"))
    {

      if (avroOrg!=null)
        {
          avroOrg.setExtras(extras)
          produceData(avroOrg)

        }

      avroOrg = new Organisation()
      var id_v = replaceChars(attributes.getValue(id))
      var parentId_v = replaceChars(attributes.getValue(parentid))
      val template_v = attributes.getValue(template)
      val name_v = attributes.getValue(name)

      avroOrg.put(id, id_v)
      avroOrg.put(parentid, parentId_v)
      avroOrg.put(template, template_v)
      avroOrg.put(name, name_v)



    }
    if (qName.equals("field"))
    {

      key_v = attributes.getValue(key)

    }
    if (qName.equals("content"))
    {

      isContent=true

    }

  }

  override def characters(ch: Array[Char], start: Int, length: Int): Unit =
  {
    if (isContent)
      {
        var content_v = new String(ch, start, length)


        try {

          avroOrg.put(key_v,content_v)

        }catch
          {

            case e:Exception => LOG.info("key does not exist", key_v)

            extras.put(key_v, content_v)

          }

        isContent=false

      }

  }

  def produceData(record:Organisation): Unit =
  {
    producer.produceData(serializeAvro(record))
  }

  def serializeAvro(record:Organisation): Array[Byte] = {

    val bytes:Array[Byte] = AvroSerializer.instance().serialize(record)
    bytes

  }

  def replaceChars(data:String): String ={

    var d = data.replace("{","")
    d = d.replace("}","")
    d


  }

}