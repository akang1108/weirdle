# Weirdle

## Description

Trying out Discord bot functionality. Made a small riddle guessing game as an experiment.

## Dependencies

- Java 11
- Javacord - https://javacord.org/
- Slf4j / logback - for logging
- Lombok - boiler plate code convenience

## Notes

- Used Discord and Heroku documentation
- This blog also helped: https://medium.com/@nastyworldgamer/deploying-a-java-discord-bot-jar-on-heroku-32020474ffcd

## Run locally

```bash
./mvnw clean package
DISCORD_BOT_TOKEN={your bot token} java -jar target/weirdle.jar
# Or alternatively, run the Bot main method from your IDE
```

## Using Heroku

One-time install on Mac, login, and create app. Change the BOT_APP_NAME

```bash
brew tap heroku/brew && brew install heroku
heroku plugins:install java
heroku login -i

export BOT_APP_NAME=weirdle-bot
heroku create --no-remote $BOT_APP_NAME
heroku config:set --app weirdle-bot DISCORD_BOT_TOKEN={your bot token}
```

Build & deploy to Heroku

```bash
export BOT_APP_NAME=weirdle-bot

# Build and deploy
./mvnw clean heroku:deploy

# Follow logs
heroku logs --app $BOT_APP_NAME -f

# Stop
heroku ps:scale --app $BOT_APP_NAME worker=0

# Start again
heroku ps:scale --app $BOT_APP_NAME worker=1
```


