trait Configure {
  val Token = sys.env("SLACK_BOT_TOKEN")
  val Names: List[String] = List() //When empty, defaults to all users
  val Channel = "general"
  val BotName = "hisasiburi-bot"

  val ProfileUrl = "http://file2.instiz.net/data/file/20150831/6/0/e/60e04df171c6564ac8d440d6de61dea1.jpg"
  //hisasiburi
  val HiUrl = Some("http://i1.ruliweb.daumcdn.net/uf/image/U01/ruliweb/55D8FA4F3C49FA001C")
  //kim
  val ByeUrl = Some("https://cdn.mirror.wiki/http://i.imgur.com/c6p6IeP.jpg")
  //??
  def hiText(name: String): String = "여어 " + name + "상, 히사시부리!"
  def byeText(name: String): String = "여어 " + name + "상, 사요나라!"
}
