package android.icu.impl;

import android.icu.text.StringTransform;
import android.icu.text.SymbolTable;
import android.icu.text.UnicodeSet;
import android.icu.util.Freezable;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;
import org.w3c.dom.traversal.NodeFilter;
import org.xmlpull.v1.XmlPullParser;

public class UnicodeRegex implements Cloneable, Freezable<UnicodeRegex>, StringTransform {
    private static UnicodeRegex STANDARD;
    private Comparator<Object> LongestFirst;
    private String bnfCommentString;
    private String bnfLineSeparator;
    private String bnfVariableInfix;
    private Appendable log;
    private SymbolTable symbolTable;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.icu.impl.UnicodeRegex.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.icu.impl.UnicodeRegex.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1197)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.icu.impl.UnicodeRegex.<clinit>():void");
    }

    public UnicodeRegex() {
        this.bnfCommentString = "#";
        this.bnfVariableInfix = "=";
        this.bnfLineSeparator = "\n";
        this.log = null;
        this.LongestFirst = new Comparator<Object>() {
            public int compare(Object obj0, Object obj1) {
                String arg0 = obj0.toString();
                String arg1 = obj1.toString();
                int len0 = arg0.length();
                int len1 = arg1.length();
                if (len0 != len1) {
                    return len1 - len0;
                }
                return arg0.compareTo(arg1);
            }
        };
    }

    public SymbolTable getSymbolTable() {
        return this.symbolTable;
    }

    public UnicodeRegex setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        return this;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public String transform(String regex) {
        StringBuilder result = new StringBuilder();
        UnicodeSet temp = new UnicodeSet();
        ParsePosition pos = new ParsePosition(0);
        int state = 0;
        int i = 0;
        while (i < regex.length()) {
            char ch = regex.charAt(i);
            switch (state) {
                case XmlPullParser.START_DOCUMENT /*0*/:
                    if (ch != PatternTokenizer.BACK_SLASH) {
                        if (ch == '[' && UnicodeSet.resemblesPattern(regex, i)) {
                            i = processSet(regex, i, result, temp, pos);
                            break;
                        }
                    } else if (UnicodeSet.resemblesPattern(regex, i)) {
                        i = processSet(regex, i, result, temp, pos);
                        continue;
                    } else {
                        state = 1;
                    }
                case NodeFilter.SHOW_ELEMENT /*1*/:
                    if (ch == 'Q') {
                        state = 1;
                    } else {
                        state = 0;
                    }
                    result.append(ch);
                    break;
                case NodeFilter.SHOW_ATTRIBUTE /*2*/:
                    if (ch == PatternTokenizer.BACK_SLASH) {
                        state = 3;
                    }
                    result.append(ch);
                    break;
                case XmlPullParser.END_TAG /*3*/:
                    if (ch == 'E') {
                    }
                    state = 2;
                    result.append(ch);
                    break;
            }
            result.append(ch);
            i++;
        }
        return result.toString();
    }

    public static String fix(String regex) {
        return STANDARD.transform(regex);
    }

    public static Pattern compile(String regex) {
        return Pattern.compile(STANDARD.transform(regex));
    }

    public static Pattern compile(String regex, int options) {
        return Pattern.compile(STANDARD.transform(regex), options);
    }

    public String compileBnf(String bnfLines) {
        return compileBnf(Arrays.asList(bnfLines.split("\\r\\n?|\\n")));
    }

    public String compileBnf(List<String> lines) {
        Map<String, String> variables = getVariables(lines);
        Set<String> unused = new LinkedHashSet(variables.keySet());
        for (int i = 0; i < 2; i++) {
            for (Entry<String, String> entry : variables.entrySet()) {
                String variable = (String) entry.getKey();
                String definition = (String) entry.getValue();
                for (Entry<String, String> entry2 : variables.entrySet()) {
                    String variable2 = (String) entry2.getKey();
                    String definition2 = (String) entry2.getValue();
                    if (!variable.equals(variable2)) {
                        String altered2 = definition2.replace(variable, definition);
                        if (altered2.equals(definition2)) {
                            continue;
                        } else {
                            unused.remove(variable);
                            variables.put(variable2, altered2);
                            if (this.log != null) {
                                try {
                                    this.log.append(variable2 + "=" + altered2 + ";");
                                } catch (IOException e) {
                                    throw ((IllegalArgumentException) new IllegalArgumentException().initCause(e));
                                }
                            }
                            continue;
                        }
                    }
                }
            }
        }
        if (unused.size() == 1) {
            return (String) variables.get(unused.iterator().next());
        }
        throw new IllegalArgumentException("Not a single root: " + unused);
    }

    public String getBnfCommentString() {
        return this.bnfCommentString;
    }

    public void setBnfCommentString(String bnfCommentString) {
        this.bnfCommentString = bnfCommentString;
    }

    public String getBnfVariableInfix() {
        return this.bnfVariableInfix;
    }

    public void setBnfVariableInfix(String bnfVariableInfix) {
        this.bnfVariableInfix = bnfVariableInfix;
    }

    public String getBnfLineSeparator() {
        return this.bnfLineSeparator;
    }

    public void setBnfLineSeparator(String bnfLineSeparator) {
        this.bnfLineSeparator = bnfLineSeparator;
    }

    public static List<String> appendLines(List<String> result, String file, String encoding) throws IOException {
        InputStream is = new FileInputStream(file);
        try {
            List<String> appendLines = appendLines((List) result, is, encoding);
            return appendLines;
        } finally {
            is.close();
        }
    }

    public static List<String> appendLines(List<String> result, InputStream inputStream, String encoding) throws UnsupportedEncodingException, IOException {
        if (encoding == null) {
            encoding = "UTF-8";
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, encoding));
        while (true) {
            String line = in.readLine();
            if (line == null) {
                return result;
            }
            result.add(line);
        }
    }

    public UnicodeRegex cloneAsThawed() {
        try {
            return (UnicodeRegex) clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalArgumentException();
        }
    }

    public UnicodeRegex freeze() {
        return this;
    }

    public boolean isFrozen() {
        return true;
    }

    private int processSet(String regex, int i, StringBuilder result, UnicodeSet temp, ParsePosition pos) {
        try {
            pos.setIndex(i);
            UnicodeSet x = temp.clear().applyPattern(regex, pos, this.symbolTable, 0);
            x.complement().complement();
            result.append(x.toPattern(false));
            return pos.getIndex() - 1;
        } catch (Exception e) {
            throw ((IllegalArgumentException) new IllegalArgumentException("Error in " + regex).initCause(e));
        }
    }

    private Map<String, String> getVariables(List<String> lines) {
        Map<String, String> variables = new TreeMap(this.LongestFirst);
        String variable = null;
        StringBuffer definition = new StringBuffer();
        int count = 0;
        for (String line : lines) {
            count++;
            String line2;
            if (line2.length() != 0) {
                if (line2.charAt(0) == '\ufeff') {
                    line2 = line2.substring(1);
                }
                if (this.bnfCommentString != null) {
                    int hashPos = line2.indexOf(this.bnfCommentString);
                    if (hashPos >= 0) {
                        line2 = line2.substring(0, hashPos);
                    }
                }
                String trimline = line2.trim();
                if (trimline.length() != 0) {
                    String linePart = line2;
                    if (line2.trim().length() != 0) {
                        boolean terminated = trimline.endsWith(";");
                        if (terminated) {
                            linePart = linePart.substring(0, linePart.lastIndexOf(59));
                        }
                        int equalsPos = linePart.indexOf(this.bnfVariableInfix);
                        if (equalsPos >= 0) {
                            if (variable != null) {
                                throw new IllegalArgumentException("Missing ';' before " + count + ") " + line2);
                            }
                            variable = linePart.substring(0, equalsPos).trim();
                            if (variables.containsKey(variable)) {
                                throw new IllegalArgumentException("Duplicate variable definition in " + line2);
                            }
                            definition.append(linePart.substring(equalsPos + 1).trim());
                        } else if (variable == null) {
                            throw new IllegalArgumentException("Missing '=' at " + count + ") " + line2);
                        } else {
                            definition.append(this.bnfLineSeparator).append(linePart);
                        }
                        if (terminated) {
                            variables.put(variable, definition.toString());
                            variable = null;
                            definition.setLength(0);
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        if (variable == null) {
            return variables;
        }
        throw new IllegalArgumentException("Missing ';' at end");
    }
}
