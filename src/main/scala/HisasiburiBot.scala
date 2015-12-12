import slack.api.SlackApiClient          // Async
import slack.api.BlockingSlackApiClient  // Blocking
import slack.models.Attachment
import slack.models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}

object HisasiburiBotRunner extends App with Configure {
  //targetNames better naming? targetUsers.size <= targetNames.size
  val client = SlackApiClient(Token)
  val d = 5.seconds
  val loopd: Long = 10000

  val res = client.listUsers() // => Future[Seq[Channel]]

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
      channelId = Channel,
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

  import scala.annotation._
  @tailrec
  def loop(wasActive: Seq[Boolean]): Any = {
    // https://api.slack.com/docs/rate-limits
    val x: Seq[Future[String]] = targetUsers.map(u => client.getUserPresence(u.id))
    val isActive: Seq[Boolean] = Await.result(Future.sequence(x), d) map (_ == "active") //use map instead?
    assert(wasActive.size == isActive.size)
    assert(targetUsers.size == isActive.size)
    for(i <- 0 to wasActive.size-1) { // change to recursive?
      (wasActive(i), isActive(i)) match {
        case (true, false) => postMessage(ByeUrl, byeText(targetUsers(i).name))
        case (false, true) => postMessage(HiUrl, hiText(targetUsers(i).name))
        case _ => ()
      }
    }
    Thread.sleep(loopd)
    loop(isActive)
  }
  loop(Seq.fill(targetUsers.size)(false))

  //todo access logs with admin
  //todo outgoing webhook & heroku
}
