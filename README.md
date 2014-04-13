Spoken
======

This is a hack developed at the 24 hour news hackathon [Hackaway](http://www.hackaway.com) in the Stockholm archipelago. 

Check it out at [Nyhetspr.at](http://nyhetspr.at).

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
