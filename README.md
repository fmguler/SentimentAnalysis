SentimentAnalysis
=================

A sentiment analysis server using Stanford CoreNLP

This is a sub project created for Koding Global Hackathon.

It is used to analyze tweets sent for sentiment using Stanford CoreNLP library. The tweets are sent like /main/analyzeTweet.htm?tweets=xyz (each tweet separated with new line) and the results are returned in JSON format.

This is a Netbeans project, you can open it with Netbeans and deploy to any Tomcat/Jetty like. Stanford models are not included (stanford-corenlp-3.5.0-models.jar).

