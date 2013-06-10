package org.karpukhin.smsviewer.utils;

import org.karpukhin.smsviewer.model.Message;

import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Pavel Karpukhin
 */
public class VmessageParser {

    private static final Logger logger = Logger.getLogger(VmessageParser.class.getName());

    public static final String DATE_FORMAT =  "yyyyMMdd'T'HHmmssz";
    public static final String DATE_FORMAT2 = "dd.MM.yyyy HH:mm:ss";

    public static Message parse(String file) {
        Message result = null;
        try {
            InputStream stream = new FileInputStream(file);
            result = parse(stream);
            try {
                stream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    public static Message parse(InputStream stream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-16LE")));
        Message result = null;
        try {
            result = parse(reader);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        }
        return result;
    }

    public static Message parse(BufferedReader reader) throws IOException {
        Message result = new Message();
        String line = reader.readLine();
        while (line != null) {
            logger.log(Level.FINER, "**{}**", line);
            if (line.startsWith("BEGIN:")) {
                String type = line.substring("BEGIN:".length());
                if ("VMSG".equals(type)) {
                    parseVmsg(reader, result);
                } else {
                    logger.log(Level.FINE, "Unexpected: {}", type);
                }
            } else {
                logger.log(Level.FINE, "Unexpected: {}", line);
            }
            line = reader.readLine();
        }
        return result;
    }

    public static void parseVmsg(BufferedReader reader, Message message) throws IOException {
        final String expected = "VMSG";
        String line = reader.readLine();
        while (line != null) {
            logger.log(Level.FINER, "**{}**", line);
            if (line.startsWith("END:" + expected)) {
                break;
            }
            if (line.startsWith("BEGIN:")) {
                String type = line.substring("BEGIN:".length());
                if ("VCARD".equals(type)) {
                    parseVcard(reader, message);
                } else if ("VENV".equals(type)) {
                    parseVenv(reader, message);
                } else {
                    logger.log(Level.FINE, "Unexpected: {}", type);
                }
            } else if (line.startsWith("VERSION:")) {
                logger.log(Level.FINE, "Version: {}", line.substring("VERSION:".length()));
            } else if (line.startsWith("X-MESSAGE-TYPE:")) {
                String type = line.substring("X-MESSAGE-TYPE:".length());
                if ("SUBMIT".equals(type)) {
                    message.setInbox(false);
                } else if ("DELIVER".equals(type)) {
                    message.setInbox(true);
                }
            } else if (line.startsWith("X-NOK-DT:")) {
                try {
                    String tmp = line.substring("X-NOK-DT:".length()).replace("Z", "GMT+00:00");
                    Date date = DateUtils.parseDate(tmp, DATE_FORMAT);
                    if (message.getDate() == null) {
                        message.setDate(date);
                    }
                } catch (ParseException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            } else {
                logger.log(Level.FINE, "Unexpected: {}", line);
            }
            line = reader.readLine();
        }
    }

    public static void parseVcard(BufferedReader reader, Message message) throws IOException {
        final String expected = "VCARD";
        String line = reader.readLine();
        while (line != null) {
            logger.log(Level.FINER, "**{}**", line);
            if (line.startsWith("END:" + expected)) {
                break;
            }
            if (line.startsWith("TEL:")) {
                message.setNumber(line.substring("TEL:".length()));
            } else if (line.startsWith("VERSION:")) {
                logger.log(Level.FINE, "Version: {}", line.substring("VERSION:".length()));
            } else {
                logger.log(Level.FINE, "Unexpected: {}", line);
            }
            line = reader.readLine();
        }
    }

    public static void parseVenv(BufferedReader reader, Message message) throws IOException {
        final String expected = "VENV";
        String line = reader.readLine();
        while (line != null) {
            logger.log(Level.FINER, "**{}**", line);
            if (line.startsWith("END:" + expected)) {
                break;
            }
            if (line.startsWith("BEGIN:")) {
                String type = line.substring("BEGIN:".length());
                if ("VBODY".equals(type)) {
                    parseVbody(reader, message);
                } else if ("VCARD".equals(type)) {
                    parseVcard(reader, message);
                } else if ("VENV".equals(type)) {
                    parseVenv(reader, message);
                } else {
                    logger.log(Level.FINE, "Unexpected: {}", type);
                }
            } else if (line.startsWith("TEL:")) {
                message.setNumber(line.substring("TEL:".length()));
            } else if (line.startsWith("VERSION:")) {
                logger.log(Level.FINE, "Version: {}", line.substring("VERSION:".length()));
            } else {
                logger.log(Level.FINE, "Unexpected: {}", line);
            }
            line = reader.readLine();
        }
    }

    public static void parseVbody(BufferedReader reader, Message message) throws IOException {
        final String expected = "VBODY";
        String line = reader.readLine();
        while (line != null) {
            logger.log(Level.FINER, "**{}**", line);
            if (line.startsWith("END:" + expected)) {
                break;
            }
            if (line.startsWith("Date:")) {
                try {
                    Date date = DateUtils.parseDate(line.substring("Date:".length()), DATE_FORMAT2);
                    if (message.getDate() == null) {
                        message.setDate(date);
                    }
                } catch (ParseException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            } else {
                if (message.getText() != null) {
                    message.setText(message.getText() + line);
                } else {
                    message.setText(line);
                }
            }
            line = reader.readLine();
        }
    }

    public static interface LineParser {
        boolean parse(BufferedReader reader, Message message);
    }

    public static class VbodyParser implements LineParser {
        @Override
        public boolean parse(BufferedReader reader, Message message) {
            return false;
        }
    }
}
