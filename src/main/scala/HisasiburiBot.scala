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
  val d = 5.seconds
  val loopd: Long = 10000
  val ChannelId = Await.result(client.listChannels(0), d).find(_.name == Channel).get.id

  val res = client.listUsers()

  val targetUsers: Seq[User] = Await.result((for {
    users <- res
    targetUsers = users.filter(u => Names.isEmpty || Names.contains(u.name))
  } yield targetUsers), d)

  Await.result(res, d)

  println(targetUsers map (x => x.name + " " + x.id))

  def postMessage(image_url: String, text: String): Future[String] = {
    val attachment = Attachment(
      fallback = Some("Hisasiburi Failed..."),
      image_url = Some(image_url),
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

}
