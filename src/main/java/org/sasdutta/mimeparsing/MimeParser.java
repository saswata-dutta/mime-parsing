package org.sasdutta.mimeparsing;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import tech.blueglacier.email.Attachment;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.spi.JsonProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class MimeParser {
    private final ContentHandler contentHandler;
    private final MimeStreamParser mime4jParser;

    public MimeParser() {
        contentHandler = new CustomContentHandler();

        MimeConfig mime4jParserConfig = MimeConfig.DEFAULT;
        BodyDescriptorBuilder bodyDescriptorBuilder = new DefaultBodyDescriptorBuilder();

        mime4jParser = new MimeStreamParser(mime4jParserConfig, DecodeMonitor.SILENT, bodyDescriptorBuilder);
        mime4jParser.setContentDecoding(true);
        mime4jParser.setContentHandler(contentHandler);
    }

    private Email getParsedEmail(InputStream input) throws IOException, MimeException {
        mime4jParser.parse(input);
        return ((CustomContentHandler) contentHandler).getEmail();
    }

    public JsonObject parse(InputStream input) throws IOException, MimeException {
        Email email = getParsedEmail(input);

        return Json.createObjectBuilder()
                .add("to", toJsonValue(email.getToEmailHeaderValue()))
                .add("cc", toJsonValue(email.getCCEmailHeaderValue()))
                .add("from", toJsonValue(email.getFromEmailHeaderValue()))
                .add("subject", toJsonValue(email.getEmailSubject()))
                .add("plaintext", toJsonValue(streamToString(email.getPlainTextEmailBody())))
                .build();
    }

    private static String streamToString(Attachment attachment) {
        if (attachment == null) return null;

        try (InputStream inputStream = attachment.getIs()) {
            return new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (IOException ex) {
            System.err.println("Failed to close stream " + ex.getMessage());
            ex.printStackTrace();
            return "";
        }
    }

    private static JsonValue toJsonValue(String value) {
        if (value == null) return JsonValue.NULL;
        else return JsonProvider.provider().createValue(value);
    }
}
