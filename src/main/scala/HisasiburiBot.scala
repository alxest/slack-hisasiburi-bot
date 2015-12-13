import slack.api.SlackApiClient          // Async
import slack.api.BlockingSlackApiClient  // Blocking
import slack.models.Attachment
import slack.models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}

object HisasiburiBotRunner extends App with Configure {
  val client = SlackApiClient(Token)
  val d = Duration.Inf//5.seconds
  val ChannelId = Await.result(client.listChannels(0), d).find(_.name == Channel).get.id

  val res = client.listUsers()

  val targetUsers: Seq[User] = Await.result((for {
    users <- res
    targetUsers = users.filter(u => Names.isEmpty || Names.contains(u.name))
  } yield targetUsers), d)

  println(targetUsers map (x => x.name + " " + x.id))

  def postMessage(image_url: Option[String], text: String): Future[String] = {
    val attachment = Attachment(
      fallback = Some("Hisasiburi Failed..."),
      image_url = image_url,
      color = Some("#764FA5")
    )
    println("sending " + text)
    val res = client.postChatMessage(
      channelId = "#" + Channel,
      text = text,
      username = Some(BotName),
      asUser = Some(false),
      attachments = Some(Seq(attachment)),
      iconUrl = Some(ProfileUrl)
    )
    res.onComplete(x => x match {
      case Success(s) => println("sent " + text + ", success: " + s)
      case Failure(f) => println("sent " + text + ", error: " + f)
    })
    res
  }

  import slack.rtm.SlackRtmClient
  import akka.actor.ActorSystem
  implicit val system = ActorSystem("slack")
  val rtmClient = SlackRtmClient(Token)
  rtmClient.onEvent {
    case e: PresenceChange =>
      println(e)
      for {
        t <- targetUsers.find(_.id == e.user)
        if(e.user == t.id)
      } e.presence match {
        case "active" => postMessage(HiUrl, hiText(t.name))
        //don't know why but currently this event occurs twice every time. If this status continues, should add some logic to block it.
        case "away" => postMessage(ByeUrl, byeText(t.name))
        case _ => throw new Exception("Presence is not active or away: " + e)
      }
    case _ => ()
  }
}
