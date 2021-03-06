package com.android.ims;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.telecom.VideoProfile;

public class ImsCallProfile implements Parcelable {
    public static final int CALL_RESTRICT_CAUSE_DISABLED = 2;
    public static final int CALL_RESTRICT_CAUSE_HD = 3;
    public static final int CALL_RESTRICT_CAUSE_NONE = 0;
    public static final int CALL_RESTRICT_CAUSE_RAT = 1;
    public static final int CALL_TYPE_VIDEO_N_VOICE = 3;
    public static final int CALL_TYPE_VOICE = 2;
    public static final int CALL_TYPE_VOICE_N_VIDEO = 1;
    public static final int CALL_TYPE_VS = 8;
    public static final int CALL_TYPE_VS_RX = 10;
    public static final int CALL_TYPE_VS_TX = 9;
    public static final int CALL_TYPE_VT = 4;
    public static final int CALL_TYPE_VT_NODIR = 7;
    public static final int CALL_TYPE_VT_RX = 6;
    public static final int CALL_TYPE_VT_TX = 5;
    public static final Creator<ImsCallProfile> CREATOR = null;
    public static final int DIALSTRING_NORMAL = 0;
    public static final int DIALSTRING_SS_CONF = 1;
    public static final int DIALSTRING_USSD = 2;
    public static final String EXTRA_ADDITIONAL_CALL_INFO = "AdditionalCallInfo";
    public static final String EXTRA_CALL_MODE_CHANGEABLE = "call_mode_changeable";
    public static final String EXTRA_CALL_RAT_TYPE = "CallRadioTech";
    public static final String EXTRA_CHILD_NUMBER = "ChildNum";
    public static final String EXTRA_CNA = "cna";
    public static final String EXTRA_CNAP = "cnap";
    public static final String EXTRA_CODEC = "Codec";
    public static final String EXTRA_CONFERENCE = "conference";
    public static final String EXTRA_CONFERENCE_AVAIL = "conference_avail";
    public static final String EXTRA_DIALSTRING = "dialstring";
    public static final String EXTRA_DISPLAY_TEXT = "DisplayText";
    public static final String EXTRA_E_CALL = "e_call";
    public static final String EXTRA_IS_CALL_PULL = "CallPull";
    public static final String EXTRA_OEM_EXTRAS = "OemCallExtras";
    public static final String EXTRA_OI = "oi";
    public static final String EXTRA_OIR = "oir";
    public static final String EXTRA_REMOTE_URI = "remote_uri";
    public static final String EXTRA_USSD = "ussd";
    public static final String EXTRA_VMS = "vms";
    public static final int OIR_DEFAULT = 0;
    public static final int OIR_PRESENTATION_NOT_RESTRICTED = 2;
    public static final int OIR_PRESENTATION_PAYPHONE = 4;
    public static final int OIR_PRESENTATION_RESTRICTED = 1;
    public static final int OIR_PRESENTATION_UNKNOWN = 3;
    public static final int SERVICE_TYPE_EMERGENCY = 2;
    public static final int SERVICE_TYPE_NONE = 0;
    public static final int SERVICE_TYPE_NORMAL = 1;
    private static final String TAG = "ImsCallProfile";
    public Bundle mCallExtras;
    public int mCallType;
    public ImsStreamMediaProfile mMediaProfile;
    public int mRestrictCause;
    public int mServiceType;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: com.android.ims.ImsCallProfile.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: com.android.ims.ImsCallProfile.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 7 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1197)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 8 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.ims.ImsCallProfile.<clinit>():void");
    }

    public ImsCallProfile(Parcel in) {
        this.mRestrictCause = SERVICE_TYPE_NONE;
        readFromParcel(in);
    }

    public ImsCallProfile() {
        this.mRestrictCause = SERVICE_TYPE_NONE;
        this.mServiceType = SERVICE_TYPE_NORMAL;
        this.mCallType = SERVICE_TYPE_NORMAL;
        this.mCallExtras = new Bundle();
        this.mMediaProfile = new ImsStreamMediaProfile();
    }

    public ImsCallProfile(int serviceType, int callType) {
        this.mRestrictCause = SERVICE_TYPE_NONE;
        this.mServiceType = serviceType;
        this.mCallType = callType;
        this.mCallExtras = new Bundle();
        this.mMediaProfile = new ImsStreamMediaProfile();
    }

    public String getCallExtra(String name) {
        return getCallExtra(name, "");
    }

    public String getCallExtra(String name, String defaultValue) {
        if (this.mCallExtras == null) {
            return defaultValue;
        }
        return this.mCallExtras.getString(name, defaultValue);
    }

    public boolean getCallExtraBoolean(String name) {
        return getCallExtraBoolean(name, false);
    }

    public boolean getCallExtraBoolean(String name, boolean defaultValue) {
        if (this.mCallExtras == null) {
            return defaultValue;
        }
        return this.mCallExtras.getBoolean(name, defaultValue);
    }

    public int getCallExtraInt(String name) {
        return getCallExtraInt(name, -1);
    }

    public int getCallExtraInt(String name, int defaultValue) {
        if (this.mCallExtras == null) {
            return defaultValue;
        }
        return this.mCallExtras.getInt(name, defaultValue);
    }

    public void setCallExtra(String name, String value) {
        if (this.mCallExtras != null) {
            this.mCallExtras.putString(name, value);
        }
    }

    public void setCallExtraBoolean(String name, boolean value) {
        if (this.mCallExtras != null) {
            this.mCallExtras.putBoolean(name, value);
        }
    }

    public void setCallExtraInt(String name, int value) {
        if (this.mCallExtras != null) {
            this.mCallExtras.putInt(name, value);
        }
    }

    public void updateCallType(ImsCallProfile profile) {
        this.mCallType = profile.mCallType;
    }

    public void updateCallExtras(ImsCallProfile profile) {
        this.mCallExtras.clear();
        this.mCallExtras = (Bundle) profile.mCallExtras.clone();
    }

    public String toString() {
        return "{ serviceType=" + this.mServiceType + ", callType=" + this.mCallType + ", restrictCause=" + this.mRestrictCause + ", mediaProfile=" + this.mMediaProfile.toString() + " }";
    }

    public int describeContents() {
        return SERVICE_TYPE_NONE;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mServiceType);
        out.writeInt(this.mCallType);
        out.writeParcelable(this.mCallExtras, SERVICE_TYPE_NONE);
        out.writeParcelable(this.mMediaProfile, SERVICE_TYPE_NONE);
    }

    private void readFromParcel(Parcel in) {
        this.mServiceType = in.readInt();
        this.mCallType = in.readInt();
        this.mCallExtras = (Bundle) in.readParcelable(null);
        this.mMediaProfile = (ImsStreamMediaProfile) in.readParcelable(null);
    }

    public static int getVideoStateFromImsCallProfile(ImsCallProfile callProfile) {
        int videostate = getVideoStateFromCallType(callProfile.mCallType);
        if (!callProfile.isVideoPaused() || VideoProfile.isAudioOnly(videostate)) {
            return videostate & -5;
        }
        return videostate | OIR_PRESENTATION_PAYPHONE;
    }

    public static int getVideoStateFromCallType(int callType) {
        switch (callType) {
            case SERVICE_TYPE_EMERGENCY /*2*/:
                return SERVICE_TYPE_NONE;
            case OIR_PRESENTATION_PAYPHONE /*4*/:
                return OIR_PRESENTATION_UNKNOWN;
            case CALL_TYPE_VT_TX /*5*/:
                return SERVICE_TYPE_NORMAL;
            case CALL_TYPE_VT_RX /*6*/:
                return SERVICE_TYPE_EMERGENCY;
            default:
                return SERVICE_TYPE_NONE;
        }
    }

    public static int getCallTypeFromVideoState(int videoState) {
        boolean videoTx = isVideoStateSet(videoState, SERVICE_TYPE_NORMAL);
        boolean videoRx = isVideoStateSet(videoState, SERVICE_TYPE_EMERGENCY);
        if (isVideoStateSet(videoState, OIR_PRESENTATION_PAYPHONE)) {
            return CALL_TYPE_VT_NODIR;
        }
        if (videoTx && !videoRx) {
            return CALL_TYPE_VT_TX;
        }
        if (videoTx || !videoRx) {
            return (videoTx && videoRx) ? OIR_PRESENTATION_PAYPHONE : SERVICE_TYPE_EMERGENCY;
        } else {
            return CALL_TYPE_VT_RX;
        }
    }

    public static int presentationToOIR(int presentation) {
        switch (presentation) {
            case SERVICE_TYPE_NORMAL /*1*/:
                return SERVICE_TYPE_EMERGENCY;
            case SERVICE_TYPE_EMERGENCY /*2*/:
                return SERVICE_TYPE_NORMAL;
            case OIR_PRESENTATION_UNKNOWN /*3*/:
                return OIR_PRESENTATION_UNKNOWN;
            case OIR_PRESENTATION_PAYPHONE /*4*/:
                return OIR_PRESENTATION_PAYPHONE;
            default:
                return SERVICE_TYPE_NONE;
        }
    }

    public static int OIRToPresentation(int oir) {
        switch (oir) {
            case SERVICE_TYPE_NORMAL /*1*/:
                return SERVICE_TYPE_EMERGENCY;
            case SERVICE_TYPE_EMERGENCY /*2*/:
                return SERVICE_TYPE_NORMAL;
            case OIR_PRESENTATION_UNKNOWN /*3*/:
                return OIR_PRESENTATION_UNKNOWN;
            case OIR_PRESENTATION_PAYPHONE /*4*/:
                return OIR_PRESENTATION_PAYPHONE;
            default:
                return OIR_PRESENTATION_UNKNOWN;
        }
    }

    public boolean isVideoPaused() {
        return this.mMediaProfile.mVideoDirection == 0;
    }

    private static boolean isVideoStateSet(int videoState, int videoStateToCheck) {
        return (videoState & videoStateToCheck) == videoStateToCheck;
    }
}
