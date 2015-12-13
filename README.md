# slack-hisasiburi-bot
Greet your colleague with nice-looking pics.

![Hisasiburi!](http://file2.instiz.net/data/file/20150831/6/0/e/60e04df171c6564ac8d440d6de61dea1.jpg)

## What & How ##
- Use web socket API to check presence change.
- If presence has changed, use web API to greet/farewell them with nice-looking pics in #general.
  - As no attachment is allowed in websocket sendMessage.
- Used [this](https://github.com/gilbertw1/slack-scala-client)

## Run ##
- Make [slack bot integration](https://slack.com/services/new/bot).
- Clone this repo.
- Intall sbt. (Tested on version 0.13.9)
- Modify [Config.scala](src/main/scala/Config.scala) as you want. (with bot token from above)
- "sbt run".

## Run With Jar ##
- Make [slack bot integration](https://slack.com/services/new/bot).
- Install java. (Tested on version 1.8)
- Just download .jar file.
- Set "SLACK_BOT_TOKEN" environment variable with your token. (i.e. export SLACK_BOT_TOKEN="blahblahblah")
- Run with "java -jar".

## Design Philosophy ##
- Minimal config
  + Do NOT use "as_user" when posting message, as user should set profile pic for the bot

## Future Plan ##
- Use "outgoing webhook" feature to allow each user to add/remove him/herself from stalking.
  + With Heroku deployment support

## Contribution ##
- I like functional style.
- I will follow [Scalastyle](http://www.scalastyle.org/sbt.html) in most cases.
