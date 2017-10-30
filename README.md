RoboSlack
=========
[![CircleCI](https://circleci.com/gh/palantir/roboslack/tree/master.svg?style=shield)](https://circleci.com/gh/palantir/roboslack/tree/master)
[![Download](https://api.bintray.com/packages/palantir/releases/roboslack/images/download.svg)](https://bintray.com/palantir/releases/roboslack/_latestVersion)


RoboSlack is a Java 8 API which handles all aspects of authenticating and sending messages to Slack as an incoming webhook
 service. RoboSlack features a fully-articulated Java API that allows consumers to easily create rich Slack messages 
 with features including:

1. Text with Slack Markdown features (bold, italic, lists, quotes, emojis, Slack dates)
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

#### Slack Date Formatting

RoboSlack supports Slack's Date formatting in fields that allow Slack Markdown. 
See [the "Formatting Dates" section in the Slack Docs for more information](https://api.slack.com/docs/message-formatting). 
The main advantage of the `SlackDateTime` format is that it renders appropriately based on where and when the client is viewing it: 
for example, if the client views a `SlackDateTime` today, Slack will render it as `today`, but when they view it again 
tomorrow, Slack will render the date as `yesterday` instead. Normal DateTimes don't have this dynamic advantage, and also
don't respect clients' timezones as easily as the `SlackDateTime` will: if a client crosses a timezone and views a 
`SlackDateTime` again, it will be rendered according to their new location's timezone.

```
// Simple Date Formatting example
MessageRequest message = MessageRequest.builder()
                .username("roboslack")
                .text(String.format("date_short_pretty: %s",
                    SlackDateTime.create(ZonedDateTime.now(), DateTimeFormatToken.DATE_SHORT_PRETTY)))
                .build();
```

Using the `SlackDateTime.create()` method, input a Java `Temporal` and a `DateTimeFormatToken` that represents how you'd
like to see your DateTime rendered in your Slack message.

Make sure that you specify a `Temporal` that contains all of the necessary Time/Date fields for proper rendering 
according to this table:


|`DateTimeFormatToken` Name        | Required Temporal Fields | Description                                                                           | Example `Temporal` types to use                                                         |
|----------------------------------|--------------------------|---------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| DATE                             | MMMM, dd, yyyy           | Long month name, day with ordinal, and year                                           | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| DATE_NUM                         | mm, dd, yyyy             | All numbers, with leading zeroes                                                      | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| DATE_SHORT                       | MMM, dd, yyyy            | Short month name, day (no ordinal), and year                                          | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| DATE_LONG                        | eeee, MMMM, dd, yyyy     | Day of week, long month name, day with ordinal, and year                              | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| DATE_PRETTY                      | MMMM, dd, yyyy           | Like `DATE` but uses `yesterday`, `today`, `tomorrow` when appropriate                | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| DATE_SHORT_PRETTY                | MM, dd, yyyy             | Like `DATE_SHORT` but uses `yesterday`, `today`, `tomorrow` when appropriate          | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| DATE_LONG_PRETTY                 | eeee, MMMM, dd, yyyy     | Like `DATE_LONG` but uses `yesterday`, `today`, `tomorrow` when appropriate           | `Instant`, `LocalDateTime`, `LocalDate`, `OffsetDateTime`, `OffsetDate`, `ZonedDateTime`|
| TIME                             | kk, mm                   | 12/24 hour : minute, AM/PM if client configured for 12-hour time                      | `Instant`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`                           |
| TIME_SECS                        | kk, mm, ss               | 12/24 hour : minute : second, AM/PM if client configured for 12-hour time             | `Instant`, `LocalDateTime`, `OffsetDateTime`, `ZonedDateTime`                           |

For more information, see the [Java DateTime API Pages].
Use any combination of these `DateTimeFormatTokens` to display the Date or Time as needed.

If you supply a `Temporal` that doesn't support all of the required `TemporalFields` for your `DateTimeFormatToken`, or that isn't
convertible to an Epoch Timestamp as Slack requires, RoboSlack will throw an `IllegalArgumentException` and won't proceed
with parsing your message.

Development
-----------

Looking to build upon RoboSlack?  Come join us in our [RoboSlack Team](https://robo-slack.slack.com)!

License
-------
This project is made available under the
[Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0).

[Incoming Webhooks]: https://api.slack.com/incoming-webhooks
[Incoming Webhook]: https://my.slack.com/services/new/incoming-webhook/
[Java DateTime API Pages]: http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html
