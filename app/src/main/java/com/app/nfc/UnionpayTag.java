package com.app.nfc;
import android.app.Activity;
import android.nfc.NfcAdapter;
import android.nfc.Tag;


public class UnionpayTag {

    public static String writeDataToTag(Tag mTag,String data) {
        if (mTag == null) {
            return "105";
        }
        return NfcUtils.writeNdef(mTag, data);
    }

    public static String readTagData(Tag mTag) {
        if (mTag == null) {
            return "105";
        }
        return NfcUtils.readNdef(mTag);
    }

    public static String getTagId(Tag mTag) {
        String uid = "";
        try {
            if (mTag != null) {
                byte[] id = mTag.getId();
                uid = NfcUtils.byte2hex(id);
            } else {
                return "105";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uid;
    }

    public static void closeNfc(Activity activity,NfcAdapter nfcAdapter) {
        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(activity);
        }
    }

    public String getCurrentVersion() { return "1.0.3"; }
}
