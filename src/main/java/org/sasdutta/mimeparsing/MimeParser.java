package org.sasdutta.mimeparsing;

import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.parser.ContentHandler;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;
import tech.blueglacier.email.Email;
import tech.blueglacier.parser.CustomContentHandler;

import java.io.IOException;
import java.io.InputStream;


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

    public Email getParsedEmail(InputStream input) throws IOException, MimeException {
        mime4jParser.parse(input);
        Email email = ((CustomContentHandler) contentHandler).getEmail();
        return email;
    }
}
