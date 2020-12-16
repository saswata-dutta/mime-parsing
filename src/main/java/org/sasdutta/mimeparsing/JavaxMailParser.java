package org.sasdutta.mimeparsing;

import javax.json.Json;
import javax.json.JsonObject;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.sasdutta.mimeparsing.Utils.toJsonValue;

public class JavaxMailParser {

  public static JsonObject parse(InputStream input) throws MessagingException, UnsupportedEncodingException {
    MimeMessage mimeMessage = new MimeMessage(null, input);

    return Json.createObjectBuilder()
        .add("to", toJsonValue(getRecipients(mimeMessage, MimeMessage.RecipientType.TO)))
        .add("cc", toJsonValue(getRecipients(mimeMessage, MimeMessage.RecipientType.CC)))
        .add("bcc", toJsonValue(getRecipients(mimeMessage, MimeMessage.RecipientType.BCC)))
        .add("from", toJsonValue(stringify(mimeMessage.getFrom())))
        .add("subject", toJsonValue(getSubject(mimeMessage)))
        .add("plaintext", toJsonValue("TODO parse based on multipart mime"))
        .build();
  }

  private static String getSubject(MimeMessage mimeMessage) throws MessagingException, UnsupportedEncodingException {
    String subject = mimeMessage.getSubject();
    System.out.println(subject);
    if (subject != null) {
      // https://en.wikipedia.org/wiki/MIME?oldformat=true#Encoded-Word
      return subject.startsWith("=?") ?
          MimeUtility.decodeWord(subject) :
          MimeUtility.decodeText(subject);
    }
    return null;
  }

  private static String getRecipients(MimeMessage mimeMessage, Message.RecipientType recipientType) throws MessagingException {
    return stringify(mimeMessage.getRecipients(recipientType));
  }

  private static String stringify(Address[] addresses) {
    if (addresses == null) return null;
    return Arrays.stream(addresses).
        map(Address::toString).
        collect(Collectors.joining(";"));
  }
}
