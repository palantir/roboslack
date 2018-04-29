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

Artifacts are published to Bintray, the following is an example gradle dependency configuration:

```
repositories {
  maven { url  "http://palantir.bintray.com/releases" }
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
> See ["Formatting Dates" section in the Slack Docs](https://api.slack.com/docs/message-formatting) for more information.

The main advantage of the `SlackDateTime` format over simply formatting a [`Temporal`](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/Temporal.html) into a [`String`](https://docs.oracle.com/javase/7/docs/api/java/lang/String.html) and sending that within the Slack message is that the `SlackDateTime` renders differently based on where and when the client is viewing it.  **For example, if the client views a `SlackDateTime` with Date granularity (that represents a point in time at the same day of the client) Slack will render it as`today`, but when they view the same message again the following day, Slack will render the message as `yesterday` instead.**. 

Any [`Temporal`](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/Temporal.html) object alone doesn't have this dynamic advantage, and also
don't respect clients' timezones as easily as the `SlackDateTime` will.  **If a client crosses a timezone and views a 
`SlackDateTime` again, it will be rendered according to their new location's timezone.**

##### Usage

```
// Assuming you have some Temporal object (for example a ZonedDateTime)
ZonedDateTime now = ZonedDateTime.now();

// Create a new SlackDateTime for that Temporal
SlackDateTime slackDateTime = SlackDateTime.of(now);

// Then create a formatter for it using DateTimeFormatTokens
SlackDateTimeFormatter formatter = SlackDateTimeFormatter.of("sent {date} at {time}");

// Finally create a message containing the SlackDateTime
MessageRequest message = MessageRequest.builder()
                .username("roboslack")
                .text(String.format("This is a message with a SlackDateTime: %s",
                    formatter.format(slackDateTime)))
                .build();
// Which would dynamically render to (when read on a client at the same day):
//  This is a message with a SlackDateTime: sent today at 12:23PM
```

Using the `SlackDateTime.of()` method, input a [`Temporal`](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/Temporal.html) (and optionally a hyperlink reference for the generated text).  Be sure to use a `Temporal` that contains all of the necessary Time/Date fields for proper rendering 
according to this table:


|`DateTimeFormatToken`        | Format | Description                                                                           | Valid `Temporal` types to use                                                         |
|----------------------------------|--------------------------|---------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------|
| `{date}`                             | `MMMMM dd, yyyy`           | Long month name, day with ordinal, and year                                           | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{date_num}`                         | `yyyy-mm-dd`             | All numbers, with leading zeroes                                                      | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{date_short}`                       | `MMM dd, yyyy`            | Short month name, day (no ordinal), and year                                          | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{date_long}`                        | `eeee, MMMM dd, yyyy`     | Day of week, long month name, day with ordinal, and year                              | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{date_pretty}`                      | `MMMM dd, yyyy`           | Like `DATE` but uses `yesterday`, `today`, `tomorrow` when appropriate                | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{date_short_pretty}`                | `MMM dd, yyyy`             | Like `DATE_SHORT` but uses `yesterday`, `today`, `tomorrow` when appropriate          | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{date_long_pretty}`                 | `eeee, MMMMM dd, yyyy`     | Like `DATE_LONG` but uses `yesterday`, `today`, `tomorrow` when appropriate           | `Instant`, `*Date`, `*DateTime` (ie. `OffsetDate` or `ZonedDateTime`)|
| `{time}`                             | `kk:mm`                   | 12/24 hour : minute, AM/PM if client configured for 12-hour time                      | `Instant`, `*Time` (ie. `LocalTime`)                           |
| `{time_secs}`                        | `kk:mm:ss`               | 12/24 hour : minute : second, AM/PM if client configured for 12-hour time             | `Instant`, `*Time` (ie. `LocalTime`)                           |

For more information, see the [Java DateTime API Pages].
Use any combination of these `DateTimeFormatTokens` to display the Date or Time as needed.

If you supply a `Temporal` that doesn't support all of the required  for your `DateTimeFormatToken`, or that isn't
convertible to an Epoch Timestamp as Slack requires, RoboSlack will throw an `IllegalArgumentException` and won't proceed
with parsing your message.

Development
-----------

Looking to build upon RoboSlack?  Come join us in our [RoboSlack Slack Channel]!

License
-------
This project is made available under the [Apache 2.0 License].

[Incoming Webhooks]: https://api.slack.com/incoming-webhooks
[Incoming Webhook]: https://my.slack.com/services/new/incoming-webhook/

[Java DateTime API Pages]: http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html

[RoboSlack Slack Channel]: https://robo-slack.slack.com

[Apache 2.0 License]: http://www.apache.org/licenses/LICENSE-2.0
