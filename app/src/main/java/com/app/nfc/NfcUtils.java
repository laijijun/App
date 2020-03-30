package com.app.nfc;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

public class NfcUtils {
    private static String MIMETYPE = "application/com.tag.tagpay";

    public static String writeNdef(Tag tag, String value) {
        int minLength = 197;
        int maxLength = 231;


        Ndef ndef = Ndef.get(tag);
        try {
            if (ndef == null) {
                return "102";
            }
            if (!ndef.isConnected()) {
                ndef.connect();
            }
            if (ndef.isWritable()) {
                ndef.close();
                Log.d("nfc", value.length() + "");
                String writeData = value;
                int length = (writeData.getBytes("utf-8")).length;

                if (length < minLength) {

                    String result = nfcA(tag, 1);
                    if (!"100".equals(result)) {
                        return result;
                    }
                } else if (length >= minLength && length <= maxLength) {
                    String result = nfcA(tag, 2);
                    if (!"100".equals(result)) {
                        return result;
                    }
                } else {
                    String result = nfcA(tag, 3);
                    if (!"100".equals(result)) {
                        return result;
                    }
                }
                if (!ndef.isConnected())
                {
                    ndef.connect();
                }

                NdefRecord mime = NdefRecord.createMime(MIMETYPE, new byte[0]);
                NdefRecord[] records = new NdefRecord[2];
                records[0] = mime;
                int maxSize = ndef.getMaxSize();
                Log.d("nfc", maxSize + "----");
                records[1] = createTextRecord(byte2hex(tag.getId()) + "x000000" + writeData);
                NdefMessage ndefMessage = new NdefMessage(records);
                int byteArrayLength = ndefMessage.getByteArrayLength();
                Log.d("nfc", byteArrayLength + "");
                if (byteArrayLength > maxSize) {
                    return "103";
                }
                ndef.writeNdefMessage(new NdefMessage(records));
            } else {
                return "101";
            }
            if (ndef.canMakeReadOnly()) {
                boolean b = ndef.makeReadOnly();
                if (b) {
                    return "100";
                }

                return "106";
            }


            return "106";

        }
        catch (IOException e) {
            e.printStackTrace();
            return "105";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ndef != null) {
                    ndef.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "107";
    }

    private static boolean NdefFormatable(Tag tag, String text, boolean lock) {
        boolean isSuccess = false;
        NdefFormatable format = NdefFormatable.get(tag);
        try {
            if (format != null) {
                format.connect();
                NdefRecord mime = NdefRecord.createMime(MIMETYPE, new byte[0]);
                NdefRecord[] records = new NdefRecord[2];
                records[0] = mime;

                records[1] = createTextRecord(text);
                NdefMessage message = new NdefMessage(records);
                if (lock) {
                    format.formatReadOnly(message);
                } else {
                    format.format(message);
                }
                isSuccess = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return isSuccess;
    }

    private static String nfcA(Tag tag, int type) {
        NfcA nfcA = NfcA.get(tag);
        try {
            if (nfcA != null) {
                if (!nfcA.isConnected())
                {
                    nfcA.connect();
                }
                byte[] select = new byte[0];
                byte[] select1 = { -94, -28, 16, 0, 0, 0 };
                switch (type) {
                    case 1:
                        select = new byte[] { -94, -29, -25, 0, 13, -1 };
                        break;
                    case 2:
                        select = new byte[] { -94, -29, -57, 0, 14, -1 };
                        break;
                    case 3:
                        select = new byte[] { -94, -29, -9, 0, 14, -1 };
                        break;
                }


                nfcA.transceive(select);
                nfcA.transceive(select1);
                return "100";
            }
            return "102";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "105";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                nfcA.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return "107";
    }



    private static NdefRecord createTextRecord(String text) {
        byte[] langBytes = Locale.ENGLISH.getLanguage().getBytes(Charset.forName("US-ASCII"));

        Charset utfEncoding = Charset.forName("UTF-8");

        byte[] textBytes = text.getBytes(utfEncoding);
        int utfBit = 0;

        char status = (char)(utfBit + langBytes.length);

        byte[] data = new byte[1 + langBytes.length + textBytes.length];

        data[0] = (byte)status;

        System.arraycopy(langBytes, 0, data, 1, langBytes.length);

        System.arraycopy(textBytes, 0, data, langBytes.length + 1, textBytes.length);

        return new NdefRecord((short)1, NdefRecord.RTD_TEXT, new byte[0], data);
    }


    public static String readNdef(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        try {
            if (ndef != null) {
                ndef.connect();
                NdefMessage ndefMessage = ndef.getNdefMessage();
                NdefRecord[] records = ndefMessage.getRecords();

                StringBuilder stringBuilder = new StringBuilder();


                for (NdefRecord ndefRecord : records) {
                    String text = parseNdefRecord(ndefRecord);
                    if (!TextUtils.isEmpty(text)) {
                        stringBuilder.append(text);
                    }
                }
                return stringBuilder.toString();
            }
            return "102";
        }
        catch (IOException e) {
            e.printStackTrace();
            return "105";
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            try {
                if (ndef != null)
                {
                    ndef.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "107";
    }


    private static String parseNdefRecord(NdefRecord ndefRecord) {
        if (1 != ndefRecord.getTnf()) {
            return "";
        }
        if (!Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return "";
        }


        try {
            byte[] payload = ndefRecord.getPayload();

            String textEncoding = ((payload[0] & 0x80) == 0) ? "utf-8" : "utf-16";

            int languageCodeLength = payload[0] & 0x3F;

            String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            return "";
        }
    }
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();

        for (int n = 0; b != null && n < b.length; n++) {
            String stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1) {
                hs.append('0');
            }
            hs.append(stmp);
        }
        return hs.toString().toUpperCase();
    }
}
