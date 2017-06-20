RoboSlack
=========
[![CircleCI](https://circleci.com/gh/palantir/roboslack/tree/master.svg?style=shield)](https://circleci.com/gh/palantir/roboslack/tree/master)  [ ![Download](https://api.bintray.com/packages/palantir/releases/roboslack/images/download.svg) ](https://bintray.com/palantir/releases/roboslack/_latestVersion)


RoboSlack is a Java 8 API which handles all aspects of authenticating and sending messages to Slack as an incoming webhook
 service. RoboSlack features a fully-articulated Java API that allows consumers to easily create rich Slack messages 
 with features including:

1. Text with Slack Markdown features (bold, italic, lists, quotes, emojis)
2. Links
3. Attachments
4. Pictures, Icons, Thumbnails
5. Titles, authors, footers
6. Notifications, including: `!channel`, `!here`, `@users`, `#channel`

It also handles Slack authentication tokens and can perform synchronous message posting requests with basic error handling.

At this time, RoboSlack does not support core Slack API features like buttons and other interactive items within Slack messages.

Core libraries:

- **roboslack-webhook**: Implementation of the [Incoming Webhooks] functionality via the `SlackWebHookService`

Auxiliary libraries:
- **roboslack-api**: Data structures for creating and sending messages
- **roboslack-webhook-api**: Data structures and service interfaces for using [Incoming Webhooks]

Setup
-----

Artifacts are published to JCenter, the following is an example gradle dependency configuration:

```
repositories {
  jcenter()
}

dependencies {
    compile "com.palantir.roboslack:roboslack-webhook:${version}"
}
```

roboslack-webhook
-----------------

Provides a mechanism to send messages through the use of a `WebHookToken`.

### Setting up a `WebHookToken`

1. As an administrator for your Slack team, visit the [Incoming Webhook] application page
2. Select the **Post to Channel** channel that you wish to use
    1. **NOTE** that RoboSlack will allow you to post
to any channel, but the **Post to Channel** option will be the default channel the web hook client (ie. RoboSlack) posts to if no channel is specified
3.  Click **Add Incoming WebHooks integration**
4.  Copy the **Webhook URL** into a safe place

### Using a `WebHookToken`

The **Incoming Webhook URL** should look something like the following:
```
// (where each `?` represents one alphanumeric character)
https://hooks.slack.com/services/T????????/B????????/????????????????????????
```

The `WebHookToken` refers to the composition of the three path parts at the end of the WebHook URL (ie `T12345678/B12345678/123456789012345678901234`). A `WebHookToken` can be instantiated in several ways:
```
// Builder
WebHookToken token = WebHookToken.builder()
                    .partT("T12345678")
                    .partB("B12345678")
                    .partX("123456789012345678901234")
                    .build();
// Static factory
WebHookToken token = WebHookToken.fromString("T12345678/B12345678/123456789012345678901234");
// Static factory with URL
WebHookToken token = WebHookToken.fromString("https://hooks.slack.com/services/T12345678/B12345678/123456789012345678901234");
// Or from system environment variables:
//      ROBOSLACK_TOKEN_TPART
//      ROBOSLACK_TOKEN_BPART
//      ROBOSLACK_TOKEN_XPART
WebHookToken token = WebHookToken.fromEnvironment();
```

### Sending Messages

Create a `MessageRequest` via the builder pattern:
```
// Simple example
MessageRequest message = MessageRequest.builder()
                .username("roboslack")
                
                // SlackMarkdown string decoration is handled automatically in fields that require it,
                // so this is valid:
                .iconEmoji(SlackMarkdown.EMOJI.decorate("smile"))
                // and passing in the raw decorated string is valid:
                .iconEmoji(":smile:")
                // or lastly, just pass the undecorated string (also valid):
                .iconEmoji("smile")
                
                .text("The simplest message")
                .build();
```

Next, send your message using the `SlackWebHookService` and receive the result:

```
ResponseCode response = SlackWebHookService.with(token)
                        .sendMessage(message);
```

RoboSlack handles error responses through the ``ResponseCode`` class. Check your ``ResponseCode`` to see why a message
may have failed to send.

Development
-----------

Looking to build upon RoboSlack?  Come join us in our [RoboSlack Team](https://robo-slack.slack.com)!

License
-------
This project is made available under the
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

[Incoming Webhooks]: https://api.slack.com/incoming-webhooks
[Incoming Webhook]: https://my.slack.com/services/new/incoming-webhook/
