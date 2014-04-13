Spoken
======

This is a hack developed at the 24 hour news hackathon [Hackaway](http://www.hackaway.com) in the Stockholm archipelago. 

Check it out at [Nyhetspr.at](http://nyhetspr.at).

Periodically consumes the RSS feeds for the major Swedish news sites Svenska Dagbladet, Dagens Nyheter, Aftonbladet and Expressen. Reads you the latest headline from each when you call the service. Marks headlines as "heard" so you won't be bothered with the same news over and over. Will send you a welcome SMS the first time you call the service to which you can, optionally, reply with your email address or Twitter handle in which case we'll send you the links to the full articles whose headlines you've listened to.

----

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
