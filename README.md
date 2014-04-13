Spoken
======

This project was born at a 24 hour news hackathon in the Stockholm archipelago.

* [Nyhetspr.at](http://nyhetspr.at)
* [Hackaway](http://www.hackaway.com)

### Running it

Built to be run locally with [foreman](https://github.com/ddollar/foreman) and in ze clouds on [Heroku](http://heroku.com/).

You'll need...
* ...a Heroku instance
   * ...with an attached Redis instance
* ...a Twilio account with
   * ...a SMS number
   * ...a voice number
* ...a Twitter account
   * ...with a Twitter application

The following environment variables needs to be setup

* `REDISCLOUD_URL`
* `TWILIO_SID`
* `TWILIO_TOKEN`
* `TWILIO_SMS_NUMBER`
* `TWITTER_KEY`
* `TWITTER_SECRET`
* `TWITTER_ACCESS_TOKEN`
* `TWITTER_ACCESS_SECRET`
